package com.example.moattravel.controller; //ログイン中のユーザーのマイページ（会員詳細）表示、会員情報編集画面の表示、および入力データのバリデーションを含んだ会員情報の更新処理などの機能（Webリクエスト）を統括・制御するコントローラークラス。

// Spring Security（認証ユーザー情報取得用）のクラスをインポート
import org.springframework.security.core.annotation.AuthenticationPrincipal; // 現在ログインしているユーザー情報を取得するためのアノテーション
// Spring MVC（コントローラー・Web機能）および入力チェック関連のクラスをインポート
import org.springframework.stereotype.Controller; // Spring Bootに「このクラスはWebリクエストを処理するコントローラーです」と認識させる
import org.springframework.ui.Model; // Java側からHTMLテンプレートへデータを渡すための「運び屋」クラス
import org.springframework.validation.BindingResult; // 入力チェック（バリデーション）の結果やエラー情報を保持するクラス
import org.springframework.validation.FieldError; // 入力項目の個別のエラー情報を表すクラス
import org.springframework.validation.annotation.Validated; // フォームの入力値に対して自動でバリデーションを実行させるアノテーション
import org.springframework.web.bind.annotation.GetMapping; // GETリクエスト（画面表示など）を受け付けるアノテーション
import org.springframework.web.bind.annotation.ModelAttribute; // 画面から送信されたデータをJavaのオブジェクト（Form等）に紐づけるアノテーション
import org.springframework.web.bind.annotation.PostMapping; // POSTリクエスト（データ送信や更新など）を受け付けるアノテーション
import org.springframework.web.bind.annotation.RequestMapping; // コントローラー全体やメソッドの共通URLプレフィックスを設定するためのアノテーション
import org.springframework.web.servlet.mvc.support.RedirectAttributes; // リダイレクト（別画面への転送）時に1度だけメッセージなどを渡すためのクラス

import com.example.moattravel.entity.User; // ユーザーのデータを入れるEntity
import com.example.moattravel.form.UserEditForm; // ユーザーが画面で編集した会員情報を受け取るためのForm
import com.example.moattravel.repository.UserRepository; // DBからユーザーデータを出し入れするためのインターフェース
import com.example.moattravel.security.UserDetailsImpl; // ログイン中のユーザー情報を扱うためのクラス
import com.example.moattravel.service.UserService; // 会員情報更新などの複雑な処理や重複チェックを行うクラス

@Controller // Spring Bootに「このクラスはWebリクエストを処理するコントローラーです」と認識させる
@RequestMapping("/user") // このクラス内のすべてのメソッドの基準URLを "/user" に設定
public class UserController {

    // 会員データの取得および更新ロジックを担当する依存コンポーネント（フィールド）を宣言
    private final UserRepository userRepository;
    private final UserService userService;

    // コンストラクタインジェクション（Springが自動で依存インスタンスを注入する）
    public UserController(UserRepository userRepository, UserService userService) {
        this.userRepository = userRepository;
        this.userService = userService;
    }

    // 会員詳細（マイページ）画面表示（GET /user）
    @GetMapping // GETリクエスト（/user）を受け付ける
    public String index(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl, Model model) { // ログイン中のユーザー情報を取得
        // ログインユーザーのIDをもとに、最新のユーザー情報をDBから取得
        User user = userRepository.getReferenceById(userDetailsImpl.getUser().getId());
        
        // View（HTML）へ引き渡すユーザー情報をModelに追加
        model.addAttribute("user", user);
        
        return "user/index"; // 表示するHTMLテンプレート（user/index.html）を返す
    }

    // 会員情報編集画面表示（GET /user/edit）
    @GetMapping("/edit") // GETリクエスト（/user/edit）を受け付ける
    public String edit(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl, Model model) {
        // ログインユーザーの最新情報をDBから取得
        User user = userRepository.getReferenceById(userDetailsImpl.getUser().getId());
        
        // 取得したユーザー情報をもとに、フォーム初期化用の UserEditForm オブジェクトを構築
        UserEditForm userEditForm = new UserEditForm(
                user.getId(), user.getName(), user.getFurigana(),
                user.getPostalCode(), user.getAddress(), user.getPhoneNumber(), user.getEmail());
        
        // 編集画面の入力初期値として UserEditForm をModelに追加
        model.addAttribute("userEditForm", userEditForm);
        
        return "user/edit"; // 表示するHTMLテンプレート（user/edit.html）を返す
    }

    // 会員情報更新処理（POST /user/update）
    @PostMapping("/update") // POSTリクエスト（/user/update）を受け付ける
    public String update(@ModelAttribute @Validated UserEditForm userEditForm, // フォームデータ取得と単体バリデーション実行
                         BindingResult bindingResult, // バリデーション結果の保持
                         RedirectAttributes redirectAttributes) { // リダイレクト先へフラッシュメッセージを渡すオブジェクト

        // メールアドレスが元の値から変更されており、かつ新しいアドレスが既に他ユーザーに登録されている場合はエラーを追加
        if (userService.isEmailChanged(userEditForm) && userService.isEmailRegistered(userEditForm.getEmail())) {
            FieldError fieldError = new FieldError(bindingResult.getObjectName(), "email", "すでに登録済みのメールアドレスです。");
            bindingResult.addError(fieldError);
        }

        // エラーが存在する場合は、入力値を保持したまま編集画面（user/edit）へ戻る
        if (bindingResult.hasErrors()) {
            return "user/edit";
        }

        // フォームの入力内容をもとに会員情報をDB上で更新
        userService.update(userEditForm);
        
        // リダイレクト先（マイページ）に表示する成功メッセージをセット
        redirectAttributes.addFlashAttribute("successMessage", "会員情報を更新しました。");
        
        return "redirect:/user"; // 会員詳細画面（/user）へリダイレクト
    }
}