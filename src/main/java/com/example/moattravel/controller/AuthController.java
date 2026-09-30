package com.example.moattravel.controller; //会員登録、メール認証、ログイン画面表示などの認証・登録関連の機能（Webリクエスト）を統括・制御するコントローラークラス。

// Jakarta EE（リクエスト情報取得用）のクラスをインポート
import jakarta.servlet.http.HttpServletRequest; // HTTPリクエストのURLやホスト名などの情報を取得するためのクラスを読み込む

// Spring MVCおよび入力チェック（バリデーション）関連のクラスをインポート
import org.springframework.stereotype.Controller; // このクラスがSpring MVCのコントローラー（Webリクエストを処理する役割）であることを示すアノテーションを読み込む
import org.springframework.ui.Model; // コントローラーからView（HTMLテンプレート）へデータを渡すためのコンテナクラスを読み込む
import org.springframework.validation.BindingResult; // フォームデータのバリデーション（入力チェック）のエラー結果を格納・受け取るためのクラスを読み込む
import org.springframework.validation.FieldError; // フォームの特定の項目（フィールド）に対するエラー内容を動的に追加・生成するためのクラスを読み込む
import org.springframework.validation.annotation.Validated; // フォームオブジェクトに対してバリデーションの実行を指示するためのアノテーションを読み込む
import org.springframework.web.bind.annotation.GetMapping; // HTTPのGETメソッド（データの取得・画面表示）のリクエストをマッピングするためのアノテーションを読み込む
import org.springframework.web.bind.annotation.ModelAttribute; // リクエストパラメータやフォーム送信データをオブジェクトにバインド（結びつけ）するためのアノテーションを読み込む
import org.springframework.web.bind.annotation.PostMapping; // HTTPのPOSTメソッド（データの登録・更新など）のリクエストをマッピングするためのアノテーションを読み込む
import org.springframework.web.bind.annotation.RequestParam; // URLのクエリパラメータ（例: ?token=...）の値を受け取るためのアノテーションを読み込む
import org.springframework.web.servlet.mvc.support.RedirectAttributes; // リダイレクト先の画面に一度だけ表示するメッセージなどを引き渡すためのクラスを読み込む

// アプリケーション内のEntity・Event・Form・Serviceをインポート
import com.example.moattravel.entity.User; // データベースのusersテーブルと1対1で対応するエンティティクラスを読み込む
import com.example.moattravel.entity.VerificationToken; // メール認証用のトークンエンティティクラスを読み込む
import com.example.moattravel.event.SignupEventPublisher; // 会員登録時のメール送信イベントを発行するパブリッダークラスを読み込む
import com.example.moattravel.form.SignupForm; // 会員登録時の入力フォームのデータ構造を定義したフォームクラスを読み込む
import com.example.moattravel.service.UserService; // 会員に関するビジネスロジックを実行するサービスを読み込む
import com.example.moattravel.service.VerificationTokenService; // 認証トークンに関するビジネスロジックを実行するサービスを読み込む

@Controller // Spring Bootに「このクラスはWebリクエストを処理するコントローラーです」と認識させる
public class AuthController {

    // 会員処理・イベント発行・認証トークン処理を担当する依存コンポーネント（フィールド）を宣言
    private final UserService userService;
    private final SignupEventPublisher signupEventPublisher;
    private final VerificationTokenService verificationTokenService;

    // コンストラクタインジェクション（Springが自動で依存インスタンスを注入する）
    public AuthController(UserService userService,
                          SignupEventPublisher signupEventPublisher,
                          VerificationTokenService verificationTokenService) {
        this.userService = userService;
        this.signupEventPublisher = signupEventPublisher;
        this.verificationTokenService = verificationTokenService;
    }

    // 会員登録画面表示（GET /signup）
        @GetMapping("/signup") // GETリクエスト（/signup）を受け付ける
    public String signup(Model model) {
    // 画面描画用に空のSignupFormオブジェクトをModelへ登録
        model.addAttribute("signupForm", new SignupForm());
        return "auth/signup"; // auth/signup.html を表示
    }
        
    //会員登録実行処理（POST /signup）
        @PostMapping("/signup") // POSTリクエスト（/signup）を受け付ける
    public String signup(@ModelAttribute @Validated SignupForm signupForm, // フォームデータ取得と単体バリデーションの実行
                         BindingResult bindingResult, // 入力エラー結果を保持するオブジェクト
                         RedirectAttributes redirectAttributes, // リダイレクト先へメッセージを渡すオブジェクト
                         HttpServletRequest httpServletRequest) { // リクエスト情報（ホスト名やURL）を取得するオブジェクト

        // メールアドレスの重複チェック（すでに登録されている場合はエラーを追加）
        if (userService.isEmailRegistered(signupForm.getEmail())) {
            FieldError fieldError = new FieldError(bindingResult.getObjectName(), "email", "すでに登録済みのメールアドレスです。");
            bindingResult.addError(fieldError);
        }

        // パスワードと確認用パスワードの一致チェック（不一致の場合はエラーを追加）
        if (!userService.isSamePassword(signupForm.getPassword(), signupForm.getPasswordConfirmation())) {
            FieldError fieldError = new FieldError(bindingResult.getObjectName(), "password", "パスワードが一致しません。");
            bindingResult.addError(fieldError);
        }

        // エラーが存在する場合はログを出力して会員登録画面へ戻る
        if (bindingResult.hasErrors()) {
            System.out.println("★バリデーションエラーが発生しました: " + bindingResult.getAllErrors());
            return "auth/signup";
        }

        // DBへの仮登録・ユーザー作成処理を実行
        User createdUser = userService.create(signupForm);

        // リクエスト情報からアプリのベースURLを取得し、メール送信イベントを発行
        String requestUrl = httpServletRequest.getRequestURL().toString().replace(httpServletRequest.getServletPath(), "");
        signupEventPublisher.publishSignupEvent(createdUser, requestUrl);

        // 案内メッセージを設定してログイン画面へリダイレクト
        redirectAttributes.addFlashAttribute("successMessage", "ご入力いただいたメールアドレスに認証メールを送信しました。メールに記載されているリンクをクリックし、会員登録を完了してください。");
        return "redirect:/login";
    }
    
    //メール認証用の処理エンドポイント（GET /signup/verify）
    @GetMapping("/signup/verify") // GETリクエスト（/signup/verify）を受け付ける
    public String verify(@RequestParam(name = "token") String token, Model model) { // URLクエリパラメータから token（?token=xxx）を取得
        // 送信されたトークン文字列をもとにDBから検証用トークンオブジェクトを取得
        VerificationToken verificationToken = verificationTokenService.getVerificationToken(token);

        // トークンの有効性判定（トークンが存在する場合はユーザーを有効化、無効な場合はエラー設定）
        if (verificationToken != null) {
            User user = verificationToken.getUser();
            userService.enableUser(user); // ユーザーアカウントを「有効（enabled）」状態へ更新
            model.addAttribute("successMessage", "会員登録が完了しました。");
        } else {
            model.addAttribute("errorMessage", "トークンが無効です。");
        }

        return "auth/verify"; // auth/verify.html を表示
    }

    
    //ログイン画面表示（GET /login）

    @GetMapping("/login") // GETリクエスト（/login）を受け付ける
    public String login() {
        return "auth/login"; // auth/login.html を表示
    }
}