package com.example.moattravel.controller; // 1. このクラスが属するパッケージ（フォルダ構成）を指定

// 日付操作用クラスをインポート
import java.time.LocalDate;
import org.springframework.data.domain.Page;
// Jakarta EE（リクエスト情報取得用）のクラスをインポート
import jakarta.servlet.http.HttpServletRequest;

// Spring Security（認証ユーザー情報取得用）のクラスをインポート
import org.springframework.security.core.annotation.AuthenticationPrincipal;
// Spring MVC（コントローラー・Web機能）および入力チェック関連のクラスをインポート
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

// アプリケーション内のEntity・Form・Repository・Security・Serviceをインポート
import com.example.moattravel.entity.House;
import com.example.moattravel.entity.Reservation;
import com.example.moattravel.form.ReservationInputForm;
import com.example.moattravel.form.ReservationRegisterForm;
import com.example.moattravel.repository.HouseRepository;
import com.example.moattravel.repository.ReservationRepository;
import com.example.moattravel.security.UserDetailsImpl;
import com.example.moattravel.service.ReservationService;
import com.example.moattravel.service.StripeService;
@Controller // 2. Spring Bootに「このクラスはWebリクエストを処理するコントローラーです」と認識させる
public class ReservationController {
    
    // 3. 予約・民宿データ操作および各種処理（計算・決済）を行う依存コンポーネント（フィールド）を宣言
    private final ReservationRepository reservationRepository;
    private final HouseRepository houseRepository;
    private final ReservationService reservationService;
    private final StripeService stripeService;

    // 4. コンストラクタインジェクション（Springが自動で依存インスタンスを注入する）
    public ReservationController(ReservationRepository reservationRepository,
                                 HouseRepository houseRepository,
                                 ReservationService reservationService,
                                 StripeService stripeService) {
        this.reservationRepository = reservationRepository;
        this.houseRepository = houseRepository;
        this.reservationService = reservationService;
        this.stripeService = stripeService;
    }

    /**
     * 予約内容確認画面の表示およびStripe決済セッションの生成（GET /houses/{id}/reservations/confirm）
     */
    @GetMapping("/houses/{id}/reservations/confirm") // 5. GETリクエスト（/houses/{id}/reservations/confirm）を受け付ける
    public String confirm(@PathVariable(name = "id") Integer id, // 6. URLパスから対象の民宿IDを取得
                          @ModelAttribute @Validated ReservationInputForm reservationInputForm, // 7. 詳細画面から送信された入力フォームデータとバリデーション
                          BindingResult bindingResult, // 8. バリデーション結果の保持
                          @AuthenticationPrincipal UserDetailsImpl userDetailsImpl, // 9. 現在ログイン中のユーザー情報を取得
                          HttpServletRequest httpServletRequest, // 10. Stripeの成功/キャンセルURL作成用リクエスト情報
                          Model model) {

        // 11. 指定されたIDの民宿参照を取得
        House house = houseRepository.getReferenceById(id);

        // 12. チェックイン・チェックアウト日等の入力エラーがある場合、詳細画面（houses/show）へ戻る
        if (bindingResult.hasErrors()) {
            model.addAttribute("house", house);
            return "houses/show";
        }

        // 13. フォームからチェックイン日とチェックアウト日を取得
        LocalDate checkinDate = reservationInputForm.getCheckinDate();
        LocalDate checkoutDate = reservationInputForm.getCheckoutDate();
        
        // 14. 宿泊日数と1泊あたりの料金から合計宿泊料金（金額）を計算
        Integer amount = reservationService.calculateAmount(checkinDate, checkoutDate, house.getPrice());

        // 15. 登録・決済処理に引き渡すための ReservationRegisterForm オブジェクトを構築
        ReservationRegisterForm reservationRegisterForm = new ReservationRegisterForm(
                house.getId(),
                userDetailsImpl.getUser().getId(),
                checkinDate.toString(),
                checkoutDate.toString(),
                reservationInputForm.getNumberOfPeople(),
                amount);

        // 16. Stripe決済画面（Checkout）へ遷移するためのセッションIDを生成
        String sessionId = stripeService.createStripeSession(house.getName(), reservationRegisterForm, httpServletRequest);

        // 17. 画面表示に必要な情報（民宿情報、登録フォームデータ、StripeセッションID）をModelに追加
        model.addAttribute("house", house);
        model.addAttribute("reservationRegisterForm", reservationRegisterForm);
        model.addAttribute("sessionId", sessionId);

        // 18. 表示するHTMLテンプレート（reservations/confirm.html）を返す
        return "reservations/confirm";
    }
    @PostMapping("/houses/{id}/reservations/create") // 19. 決済完了後や予約実行のPOSTリクエストを受け付ける
    public String create(@PathVariable(name = "id") Integer id, // 20. URLパスから対象の民宿IDを取得
                         @ModelAttribute ReservationRegisterForm reservationRegisterForm, // 21. 予約登録用フォームデータを受け取る
                         RedirectAttributes redirectAttributes) // 22. リダイレクト先にメッセージを渡すためのオブジェクト
    {

        // 23. 完了メッセージをフラッシュマ属性として設定
        redirectAttributes.addFlashAttribute("successMessage", "民宿の予約が完了しました。");

        // 24. 予約一覧画面や民宿詳細画面へリダイレクト
        return "redirect:/reservations"; 
        
    }
    /**
     * 予約一覧画面を表示するメソッド
     */
    @GetMapping("/reservations")
    public String index(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
                          @org.springframework.data.web.PageableDefault(page = 0, size = 10, sort = "id", direction = org.springframework.data.domain.Sort.Direction.ASC) org.springframework.data.domain.Pageable pageable,
                          Model model) {
        // ログイン中のユーザー情報を取得
        com.example.moattravel.entity.User user = userDetailsImpl.getUser();
        
        // ユーザーの予約情報を取得
        Page<Reservation> reservationPage = reservationService.findReservationsByUserOrderByCreatedAtDesc(user, pageable);
        // 画面にデータを渡す
        model.addAttribute("reservationPage", reservationPage);
        
        return "reservations/index";
    }
    
}