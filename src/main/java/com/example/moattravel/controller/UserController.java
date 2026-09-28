package com.example.moattravel.controller; // 1. このクラスが属するパッケージ（フォルダ構成）を指定

// Spring Security（認証ユーザー情報取得用）のクラスをインポート
import org.springframework.security.core.annotation.AuthenticationPrincipal;
// Spring MVC（コントローラー・Web機能）および入力チェック関連のクラスをインポート
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.moattravel.entity.User;
import com.example.moattravel.form.UserEditForm;
import com.example.moattravel.repository.UserRepository;
import com.example.moattravel.security.UserDetailsImpl;
import com.example.moattravel.service.UserService;

@Controller // 2. Spring Bootに「このクラスはWebリクエストを処理するコントローラーです」と認識させる
@RequestMapping("/user") // 3. このクラス内のすべてのメソッドの基準URLを "/user" に設定
public class UserController {

    // 4. 会員データの取得および更新ロジックを担当する依存コンポーネント（フィールド）を宣言
    private final UserRepository userRepository;
    private final UserService userService;

    // 5. コンストラクタインジェクション（Springが自動で依存インスタンスを注入する）
    public UserController(UserRepository userRepository, UserService userService) {
        this.userRepository = userRepository;
        this.userService = userService;
    }

    /**
     * 会員詳細（マイページ）画面表示（GET /user）
     */
    @GetMapping // 6. GETリクエスト（/user）を受け付ける
    public String index(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl, Model model) { // 7. ログイン中のユーザー情報を取得
        // 8. ログインユーザーのIDをもとに、最新のユーザー情報をDBから取得
        User user = userRepository.getReferenceById(userDetailsImpl.getUser().getId());
        
        // 9. View（HTML）へ引き渡すユーザー情報をModelに追加
        model.addAttribute("user", user);
        
        return "user/index"; // 10. 表示するHTMLテンプレート（user/index.html）を返す
    }

    /**
     * 会員情報編集画面表示（GET /user/edit）
     */
    @GetMapping("/edit") // 11. GETリクエスト（/user/edit）を受け付ける
    public String edit(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl, Model model) {
        // 12. ログインユーザーの最新情報をDBから取得
        User user = userRepository.getReferenceById(userDetailsImpl.getUser().getId());
        
        // 13. 取得したユーザー情報をもとに、フォーム初期化用の UserEditForm オブジェクトを構築
        UserEditForm userEditForm = new UserEditForm(
                user.getId(), user.getName(), user.getFurigana(),
                user.getPostalCode(), user.getAddress(), user.getPhoneNumber(), user.getEmail());
        
        // 14. 編集画面の入力初期値として UserEditForm をModelに追加
        model.addAttribute("userEditForm", userEditForm);
        
        return "user/edit"; // 15. 表示するHTMLテンプレート（user/edit.html）を返す
    }

    /**
     * 会員情報更新処理（POST /user/update）
     */
    @PostMapping("/update") // 16. POSTリクエスト（/user/update）を受け付ける
    public String update(@ModelAttribute @Validated UserEditForm userEditForm, // 17. フォームデータ取得と単体バリデーション実行
                          BindingResult bindingResult, // 18. バリデーション結果の保持
                          RedirectAttributes redirectAttributes) { // 19. リダイレクト先へフラッシュメッセージを渡すオブジェクト

        // 20. メールアドレスが元の値から変更されており、かつ新しいアドレスが既に他ユーザーに登録されている場合はエラーを追加
        if (userService.isEmailChanged(userEditForm) && userService.isEmailRegistered(userEditForm.getEmail())) {
            FieldError fieldError = new FieldError(bindingResult.getObjectName(), "email", "すでに登録済みのメールアドレスです。");
            bindingResult.addError(fieldError);
        }

        // 21. エラーが存在する場合は、入力値を保持したまま編集画面（user/edit）へ戻る
        if (bindingResult.hasErrors()) {
            return "user/edit";
        }

        // 22. フォームの入力内容をもとに会員情報をDB上で更新
        userService.update(userEditForm);
        
        // 23. リダイレクト先（マイページ）に表示する成功メッセージをセット
        redirectAttributes.addFlashAttribute("successMessage", "会員情報を更新しました。");
        
        return "redirect:/user"; // 24. 会員詳細画面（/user）へリダイレクト
    }
}