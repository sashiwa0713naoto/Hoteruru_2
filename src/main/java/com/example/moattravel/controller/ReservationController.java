package com.example.moattravel.controller; //民宿の予約に関する入力確認、Stripe決済セッションの生成、予約情報の登録、ユーザーごとの予約一覧表示などの機能（Webリクエスト）を統括・制御するコントローラークラス。

// --- Java標準ライブラリのインポート ---
import java.time.LocalDate; // 日付を扱うためのクラス（チェックイン・チェックアウト日用）

// --- Jakarta EE（サーバー通信関連）のインポート ---
import jakarta.servlet.http.HttpServletRequest; // Webリクエストの情報を取得するクラス（StripeへのURL生成などに使用）

// --- Spring Data（データベース・ページ分割関連）のインポート ---
import org.springframework.data.domain.Page; // ページネーション（複数件のデータを分割表示）された結果を保持するクラス
import org.springframework.data.domain.Pageable; // 何ページ目の何件を取得するか、という条件を指定するクラス
import org.springframework.data.domain.Sort.Direction; // データの並び順（昇順・降順）を指定するクラス
import org.springframework.data.web.PageableDefault; // ページネーションのデフォルト値（初期設定）を指定するためのアノテーション
// --- Spring Security（認証関連）のインポート ---
import org.springframework.security.core.annotation.AuthenticationPrincipal; // 現在ログインしているユーザー情報を取得するためのアノテーション
// --- Spring MVC（Web機能・バリデーション関連）のインポート ---
import org.springframework.stereotype.Controller; // Spring Bootに「このクラスはWebリクエストを処理する窓口（コントローラー）です」と認識させる
import org.springframework.ui.Model; // Java側からHTMLテンプレートへデータを渡すための「運び屋」クラス
import org.springframework.validation.BindingResult; // 入力チェック（バリデーション）の結果やエラー情報を保持するクラス
import org.springframework.validation.annotation.Validated; // フォームの入力値に対して自動でバリデーションを実行させるアノテーション
import org.springframework.web.bind.annotation.GetMapping; // GETリクエスト（画面表示など）を受け付けるアノテーション
import org.springframework.web.bind.annotation.ModelAttribute; // 画面から送信されたデータをJavaのオブジェクト（Form等）に紐づけるアノテーション
import org.springframework.web.bind.annotation.PathVariable; // URLの一部（パス変数、例: {id}）を変数として受け取るためのアノテーション
import org.springframework.web.bind.annotation.PostMapping; // POSTリクエスト（データ送信や登録など）を受け付けるアノテーション
import org.springframework.web.bind.annotation.RequestParam; // URLのクエリパラメータの値を受け取るためのアノテーション
import org.springframework.web.servlet.mvc.support.RedirectAttributes; // リダイレクト（別画面への転送）時に1度だけメッセージなどを渡すためのクラス

// --- アプリケーション内の独自クラス（Entity, Form, Repository, Security, Service）のインポート ---
import com.example.moattravel.entity.House; // 民宿のデータを入れるEntity
import com.example.moattravel.entity.Reservation; // 予約のデータを入れるEntity
import com.example.moattravel.entity.User; // ユーザーのデータを入れるEntity
import com.example.moattravel.form.ReservationInputForm; // ユーザーが画面で入力した予約情報を受け取るためのForm
import com.example.moattravel.form.ReservationRegisterForm; // 決済やデータベース登録に使う最終的な予約情報を保持するForm
import com.example.moattravel.repository.HouseRepository; // DBから民宿データを出し入れするためのインターフェース
import com.example.moattravel.security.UserDetailsImpl; // ログイン中のユーザー情報を扱うためのクラス
import com.example.moattravel.service.ReservationService; // 予約に関する複雑な処理（計算など）を行うクラス
import com.example.moattravel.service.StripeService; // Stripe（決済システム）と通信するためのクラス

@Controller // コントローラーとして動作させるための宣言
public class ReservationController {
    
    private final HouseRepository houseRepository;
    private final ReservationService reservationService;
    private final StripeService stripeService;

    // コンストラクタインジェクション
    public ReservationController(HouseRepository houseRepository,
                                 ReservationService reservationService,
                                 StripeService stripeService) {
        this.houseRepository = houseRepository;
        this.reservationService = reservationService;
        this.stripeService = stripeService;
    }

    // 予約内容確認画面の表示およびStripe決済セッションの生成（GET /houses/{id}/reservations/confirm）
    @GetMapping("/houses/{id}/reservations/confirm")
    public String confirm(@PathVariable(name = "id") Integer id,
                          @ModelAttribute @Validated ReservationInputForm reservationInputForm,
                          BindingResult bindingResult,
                          @AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
                          HttpServletRequest httpServletRequest,
                          Model model) {

        System.out.println("=== 予約確認(confirm) 呼び出し ===");
        System.out.println("受け取った民宿ID: " + id);
        System.out.println("チェックイン日: " + reservationInputForm.getCheckinDate());
        System.out.println("チェックアウト日: " + reservationInputForm.getCheckoutDate());
        System.out.println("宿泊人数: " + reservationInputForm.getNumberOfPeople());

        House house = houseRepository.getReferenceById(id);

        if (bindingResult.hasErrors()) {
            System.out.println("※入力エラーが発生したため、詳細画面に戻ります: " + bindingResult.getAllErrors());
            model.addAttribute("house", house);
            return "houses/show";
        }

        LocalDate checkinDate = reservationInputForm.getCheckinDate();
        LocalDate checkoutDate = reservationInputForm.getCheckoutDate();
        
        Integer amount = reservationService.calculateAmount(checkinDate, checkoutDate, house.getPrice());

        ReservationRegisterForm reservationRegisterForm = new ReservationRegisterForm(
                house.getId(),
                userDetailsImpl.getUser().getId(),
                checkinDate.toString(),
                checkoutDate.toString(),
                reservationInputForm.getNumberOfPeople(),
                amount);

        String sessionId = stripeService.createStripeSession(house.getName(), reservationRegisterForm, httpServletRequest);
        
        if (sessionId != null && !sessionId.isEmpty()) {
        	return "redirect:" + sessionId;
        }

        model.addAttribute("house", house);
        model.addAttribute("reservationRegisterForm", reservationRegisterForm);
        return "reservations/confirm";
    }

    // 決済完了後や、予約確定のボタンを押したときの処理（POST /houses/{id}/reservations/create）
    @PostMapping("/houses/{id}/reservations/create")
    public String create(@PathVariable(name = "id") Integer id,
                         @ModelAttribute ReservationRegisterForm reservationRegisterForm,
                         RedirectAttributes redirectAttributes) {

        redirectAttributes.addFlashAttribute("successMessage", "民宿の予約が完了しました。");

        return "redirect:/reservations"; 
    }

    // 自分の予約一覧画面を表示するメソッド（GET /reservations）
    @GetMapping("/reservations")
    public String index(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
                        @PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.ASC) Pageable pageable,
                        @RequestParam(name = "reserved", required = false) String reserved,
                        @RequestParam(name = "session_id", required = false) String sessionId,
                        Model model,
                        RedirectAttributes redirectAttributes) {
        
        if (reserved != null && sessionId != null) {
            stripeService.createReservation(sessionId);
            redirectAttributes.addFlashAttribute("successMessage", "民宿の予約が完了しました。");
            return "redirect:/reservations";
        }

        User user = userDetailsImpl.getUser();
        
        Page<Reservation> reservationPage = reservationService.findReservationsByUserOrderByCreatedAtDesc(user, pageable);
        
        model.addAttribute("reservationPage", reservationPage);
        
        return "reservations/index";
    }
}