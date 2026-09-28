package com.example.moattravel.controller; // 1. このクラスが属するパッケージ（フォルダ構成）を指定

// Jakarta EE（リクエスト情報取得用）のクラスをインポート
import jakarta.servlet.http.HttpServletRequest;

// Spring MVCおよび入力チェック（バリデーション）関連のクラスをインポート
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

// アプリケーション内のEntity・Event・Form・Serviceをインポート
import com.example.moattravel.entity.User;
import com.example.moattravel.entity.VerificationToken;
import com.example.moattravel.event.SignupEventPublisher;
import com.example.moattravel.form.SignupForm;
import com.example.moattravel.service.UserService;
import com.example.moattravel.service.VerificationTokenService;

@Controller // 2. Spring Bootに「このクラスはWebリクエストを処理するコントローラーです」と認識させる
public class AuthController {

    // 3. 会員処理・イベント発行・認証トークン処理を担当する依存コンポーネント（フィールド）を宣言
    private final UserService userService;
    private final SignupEventPublisher signupEventPublisher;
    private final VerificationTokenService verificationTokenService;

    // 4. コンストラクタインジェクション（Springが自動で依存インスタンスを注入する）
    public AuthController(UserService userService,
                          SignupEventPublisher signupEventPublisher,
                          VerificationTokenService verificationTokenService) {
        this.userService = userService;
        this.signupEventPublisher = signupEventPublisher;
        this.verificationTokenService = verificationTokenService;
    }

    /**
     * 会員登録画面表示（GET /signup）
     */
    @GetMapping("/signup") // 5. GETリクエスト（/signup）を受け付ける
    public String signup(Model model) {
        // 6. 画面描画用に空のSignupFormオブジェクトをModelへ登録
        model.addAttribute("signupForm", new SignupForm());
        return "auth/signup"; // auth/signup.html を表示
    }

    /**
     * 会員登録実行処理（POST /signup）
     */
    @PostMapping("/signup") // 7. POSTリクエスト（/signup）を受け付ける
    public String signup(@ModelAttribute @Validated SignupForm signupForm, // 8. フォームデータ取得と単体バリデーションの実行
                         BindingResult bindingResult, // 9. 入力エラー結果を保持するオブジェクト
                         RedirectAttributes redirectAttributes, // 10. リダイレクト先へメッセージを渡すオブジェクト
                         HttpServletRequest httpServletRequest) { // 11. リクエスト情報（ホスト名やURL）を取得するオブジェクト

        // 12. メールアドレスの重複チェック（すでに登録されている場合はエラーを追加）
        if (userService.isEmailRegistered(signupForm.getEmail())) {
            FieldError fieldError = new FieldError(bindingResult.getObjectName(), "email", "すでに登録済みのメールアドレスです。");
            bindingResult.addError(fieldError);
        }

        // 13. パスワードと確認用パスワードの一致チェック（不一致の場合はエラーを追加）
        if (!userService.isSamePassword(signupForm.getPassword(), signupForm.getPasswordConfirmation())) {
            FieldError fieldError = new FieldError(bindingResult.getObjectName(), "password", "パスワードが一致しません。");
            bindingResult.addError(fieldError);
        }

        // 14. エラーが存在する場合はログを出力して会員登録画面へ戻る
        if (bindingResult.hasErrors()) {
            System.out.println("★バリデーションエラーが発生しました: " + bindingResult.getAllErrors());
            return "auth/signup";
        }

        // 15. DBへの仮登録・ユーザー作成処理を実行
        User createdUser = userService.create(signupForm);

        // 16. リクエスト情報からアプリのベースURLを取得し、メール送信イベントを発行
        String requestUrl = httpServletRequest.getRequestURL().toString().replace(httpServletRequest.getServletPath(), "");
        signupEventPublisher.publishSignupEvent(createdUser, requestUrl);

        // 17. 案内メッセージを設定してログイン画面へリダイレクト
        redirectAttributes.addFlashAttribute("successMessage", "ご入力いただいたメールアドレスに認証メールを送信しました。メールに記載されているリンクをクリックし、会員登録を完了してください。");
        return "redirect:/login";
    }

    /**
     * メール認証用の処理エンドポイント（GET /signup/verify）
     */
    @GetMapping("/signup/verify") // 18. GETリクエスト（/signup/verify）を受け付ける
    public String verify(@RequestParam(name = "token") String token, Model model) { // 19. URLクエリパラメータから token（?token=xxx）を取得
        // 20. 送信されたトークン文字列をもとにDBから検証用トークンオブジェクトを取得
        VerificationToken verificationToken = verificationTokenService.getVerificationToken(token);

        // 21. トークンの有効性判定（トークンが存在する場合はユーザーを有効化、無効な場合はエラー設定）
        if (verificationToken != null) {
            User user = verificationToken.getUser();
            userService.enableUser(user); // ユーザーアカウントを「有効（enabled）」状態へ更新
            model.addAttribute("successMessage", "会員登録が完了しました。");
        } else {
            model.addAttribute("errorMessage", "トークンが無効です。");
        }

        return "auth/verify"; // auth/verify.html を表示
    }

    /**
     * ログイン画面表示（GET /login）
     */
    @GetMapping("/login") // 22. GETリクエスト（/login）を受け付ける
    public String login() {
        return "auth/login"; // auth/login.html を表示
    }
}