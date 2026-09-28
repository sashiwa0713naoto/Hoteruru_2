package com.example.moattravel.controller; // 1. このクラスが属するパッケージ（フォルダ構成）を指定

// Spring Data Pageable（ページネーション機能）関連のクラスをインポート
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
// Spring MVC（コントローラー・Web機能）関連のクラスをインポート
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

// アプリケーション内のEntity・Form・Repository・Serviceをインポート
import com.example.moattravel.entity.House;
import com.example.moattravel.form.HouseEditForm;
import com.example.moattravel.form.HouseRegisterForm;
import com.example.moattravel.repository.HouseRepository;
import com.example.moattravel.service.HouseService;

@Controller // 2. Spring Bootに「このクラスはWebリクエストを処理するコントローラーです」と認識させる
@RequestMapping("/admin/houses") // 3. このクラス内のすべてのメソッドの基準URLを "/admin/houses" に設定
public class AdminHouseController {
    // 4. データベース操作およびビジネスロジックを行う依存クラス（フィールド）を宣言
    private final HouseRepository houseRepository;
    private final HouseService houseService;

    // 5. コンストラクタインジェクション（Springが自動で依存関係を注入する）
    public AdminHouseController(HouseRepository houseRepository, HouseService houseService) {
        this.houseRepository = houseRepository;
        this.houseService = houseService;
    }

    /**
     * 民宿一覧ページ表示・検索処理（GET /admin/houses）
     */
    @GetMapping // 6. GETリクエスト（/admin/houses）を受け付ける
    public String index(Model model, 
                         @PageableDefault(page = 0, size = 10) Pageable pageable, // 7. ページネーション設定（デフォルト1ページ10件）
                         @RequestParam(name = "keyword", required = false) String keyword) { // 8. URLの検索クエリ(?keyword=...)を受け取る
        Page<House> housePage;
        
        // 9. 検索キーワードの有無で処理を分岐
        if (keyword != null && !keyword.isEmpty()) {
            // キーワードがある場合：名前・郵便番号・住所・電話番号で部分一致検索（曖昧検索）
            housePage = houseRepository.findByNameLikeOrPostalCodeLikeOrAddressLikeOrPhoneNumberLike(
                "%" + keyword + "%", 
                "%" + keyword + "%", 
                "%" + keyword + "%", 
                "%" + keyword + "%", 
                pageable
            );
        } else {
            // キーワードがない場合：全件取得（ページネーション適用）
            housePage = houseRepository.findAll(pageable);
        }

        // 10. View（HTML）へ渡すデータをModelに登録
        model.addAttribute("housePage", housePage);
        model.addAttribute("keyword", keyword);

        return "admin/houses/index"; // 11. 表示するHTMLテンプレート（admin/houses/index.html）を返す
    }

    /**
     * 民宿詳細ページ表示（GET /admin/houses/{id}）
     */
    @GetMapping("/{id}") // 12. GETリクエスト（/admin/houses/{id}）を受け付ける
    public String show(@PathVariable(name = "id") Integer id, Model model) { // 13. URLパスからIDを取得
        House house = houseRepository.findById(id).orElse(null); // 14. 指定されたIDの民宿を取得（存在しない場合はnull）
        
        if (house == null) {
            return "redirect:/admin/houses"; // 15. 民宿が存在しない場合は一覧ページへリダイレクト
        }
        
        model.addAttribute("house", house); // HTMLへ表示対象の民宿データを渡す

        return "admin/houses/show"; // admin/houses/show.html を表示
    }

    /**
     * 民宿登録画面表示（GET /admin/houses/register）
     */
    @GetMapping("/register")
    public String register(Model model) {
        // 16. フォーム用の空オブジェクトをModelにセットして登録画面を表示
        model.addAttribute("houseRegisterForm", new HouseRegisterForm());
        return "admin/houses/register";
    }

    /**
     * 民宿新規登録処理（POST /admin/houses/create）
     */
    @PostMapping("/create") // 17. POSTリクエスト（/admin/houses/create）を受け付ける
    public String create(@ModelAttribute @Validated HouseRegisterForm houseRegisterForm, // 18. フォームデータ取得と入力チェック（バリデーション）
                         BindingResult bindingResult, // 19. 入力エラー結果を受け取る
                         RedirectAttributes redirectAttributes, // 20. リダイレクト先にフラッシュメッセージ（1回限りの通知）を渡す
                         Model model) {
        
        // 21. 入力エラーがある場合は登録画面へ戻る
        if (bindingResult.hasErrors()) {
            return "admin/houses/register";
        }

        // 22. 登録処理の実行と成功メッセージの設定
        houseService.create(houseRegisterForm);
        redirectAttributes.addFlashAttribute("successMessage", "民宿を登録しました。");

        return "redirect:/admin/houses"; // 登録成功後は一覧ページへリダイレクト
    }

    /**
     * 民宿編集画面表示（GET /admin/houses/{id}/edit）
     */
    @GetMapping("/{id}/edit")
    public String edit(@PathVariable(name = "id") Integer id, Model model) {
        House house = houseRepository.findById(id).orElse(null);
        if (house == null) {
            return "redirect:/admin/houses";
        }

        // 23. DBから取得したEntityの情報を編集用Formオブジェクトに移し替える
        HouseEditForm houseEditForm = new HouseEditForm(
            house.getId(), 
            house.getName(), 
            house.getImageName(), 
            null, // 新しい画像ファイル入力用に初期値nullを設定
            house.getDescription(), 
            house.getPrice(), 
            house.getCapacity(), 
            house.getPostalCode(), 
            house.getAddress(), 
            house.getPhoneNumber()
        );
        
        model.addAttribute("houseEditForm", houseEditForm);

        return "admin/houses/edit";
    }

    /**
     * 民宿更新処理（POST /admin/houses/{id}/update）
     */
    @PostMapping("/{id}/update")
    public String update(@PathVariable(name = "id") Integer id, 
                         @ModelAttribute @Validated HouseEditForm houseEditForm, 
                         BindingResult bindingResult, 
                         RedirectAttributes redirectAttributes, 
                         Model model) {
        
        // 入力エラーがある場合は編集画面へ戻る
        if (bindingResult.hasErrors()) {
            return "admin/houses/edit";
        }

        // 24. 更新処理の実行と成功メッセージの設定
        houseService.update(houseEditForm);
        redirectAttributes.addFlashAttribute("successMessage", "民宿情報を編集しました。");

        return "redirect:/admin/houses";
    }

    /**
     * 民宿削除処理（POST /admin/houses/{id}/delete）
     */
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes) {
        // 25. IDを指定してデータベースから削除
        houseRepository.deleteById(id);
        
        redirectAttributes.addFlashAttribute("successMessage", "民宿を削除しました。");

        return "redirect:/admin/houses";
    }
}