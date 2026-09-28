package com.example.moattravel.controller; // 1. このクラスが属するパッケージ（フォルダ構成）を指定

// Spring Data Pageableおよびソート機能関連のクラスをインポート
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
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

import com.example.moattravel.entity.User;
import com.example.moattravel.form.UserEditForm;
import com.example.moattravel.repository.UserRepository;
import com.example.moattravel.service.UserService;

@Controller // 2. Spring Bootに「このクラスはWebリクエストを処理するコントローラーです」と認識させる
@RequestMapping("/admin/users") // 3. このクラス内のすべてのメソッドの基準URLを "/admin/users" に設定
public class AdminUserController {

    // 4. データベース操作およびビジネスロジックを行う依存クラス（フィールド）を宣言
    private final UserRepository userRepository;
    private final UserService userService;

    // 5. コンストラクタインジェクション（Springが自動で依存インスタンスを注入する）
    public AdminUserController(UserRepository userRepository, UserService userService) {
        this.userRepository = userRepository;
        this.userService = userService;
    }

    /**
     * 会員一覧画面表示・検索処理（GET /admin/users）
     */
    @GetMapping // 6. GETリクエスト（/admin/users）を受け付ける
    public String index(@RequestParam(name = "keyword", required = false) String keyword, // 7. 検索キーワードを受け取る（任意）
                        @PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.ASC) Pageable pageable, // 8. デフォルトのページネーション・ソート設定（IDの昇順、10件）
                        Model model) 
    {
        Page<User> userPage;

        // 9. 検索キーワードの有無に応じて取得処理を分岐
        if (keyword != null && !keyword.isEmpty()) {
            // キーワードがある場合：氏名またはフリガナで部分一致検索（曖昧検索）
            userPage = userRepository.findByNameLikeOrFuriganaLike("%" + keyword + "%", "%" + keyword + "%", pageable);
        } else {
            // キーワードがない場合：全会員を取得（ページネーション適用）
            userPage = userRepository.findAll(pageable);
        }

        // 10. View（HTML）へ渡すデータをModelに登録
        model.addAttribute("userPage", userPage);
        model.addAttribute("keyword", keyword);

        return "admin/users/index"; // 11. 表示するHTMLテンプレート（admin/users/index.html）を返す
    }

    /**
     * 会員詳細画面表示（GET /admin/users/{id}）
     */
    @GetMapping("/{id}") // 12. GETリクエスト（/admin/users/{id}）を受け付ける
    public String show(@PathVariable(name = "id") Integer id, Model model) { // 13. URLパスから会員IDを取得
        User user = userRepository.findById(id).orElse(null); // 14. 指定されたIDの会員を取得（存在しない場合はnull）
        
        if (user == null) {
            return "redirect:/admin/users"; // 15. 会員が存在しない場合は一覧ページへリダイレクト
        }

        model.addAttribute("user", user); // HTMLへ表示対象の会員データを渡す

        return "admin/users/show"; // admin/users/show.html を表示
    }

    /**
     * 会員編集画面表示（GET /admin/users/{id}/edit）
     */
    @GetMapping("/{id}/edit")
    public String edit(@PathVariable(name = "id") Integer id, Model model) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return "redirect:/admin/users";
        }

        // 16. DBから取得したEntity（User）の情報を編集用Form（UserEditForm）へセット
        UserEditForm userEditForm = new UserEditForm(
            user.getId(),
            user.getName(),
            user.getFurigana(),
            user.getPostalCode(),
            user.getAddress(),
            user.getPhoneNumber(),
            user.getEmail()
        );

        model.addAttribute("userEditForm", userEditForm);

        return "admin/users/edit"; // admin/users/edit.html を表示
    }

    /**
     * 会員情報更新処理（POST /admin/users/{id}/update）
     */
    @PostMapping("/{id}/update") // 17. POSTリクエスト（/admin/users/{id}/update）を受け付ける
    public String update(@PathVariable(name = "id") Integer id,
                         @ModelAttribute @Validated UserEditForm userEditForm, // 18. フォームデータ取得と入力チェック（バリデーション）
                         BindingResult bindingResult, // 19. バリデーション結果の保持
                         RedirectAttributes redirectAttributes) { // 20. リダイレクト先へメッセージを渡すためのオブジェクト
        
        // 21. 入力エラーがある場合は編集画面へ戻る
        if (bindingResult.hasErrors()) {
            return "admin/users/edit";
        }

        // 22. 更新処理を実行し、完了メッセージを設定して詳細画面へリダイレクト
        userService.update(userEditForm);
        redirectAttributes.addFlashAttribute("successMessage", "会員情報を編集しました。");

        return "redirect:/admin/users/" + id; // 編集した会員の詳細画面へリダイレクト
    }

    /**
     * 会員削除処理（POST /admin/users/{id}/delete）
     */
    @PostMapping("/{id}/delete") // 23. POSTリクエスト（/admin/users/{id}/delete）を受け付ける
    public String delete(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes) {
        // 24. サービスクラスの削除処理（ロジック）を呼び出して会員を削除
        userService.deleteUser(id);
        
        redirectAttributes.addFlashAttribute("successMessage", "会員を削除しました。");

        return "redirect:/admin/users"; // 削除後は一覧ページへリダイレクト
    }
}