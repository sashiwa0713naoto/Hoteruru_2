package com.example.moattravel.event; // 1. このクラスが属するパッケージ（フォルダ構成）を指定

// Spring Frameworkのイベント基底クラスをインポート
import org.springframework.context.ApplicationEvent;

// ユーザー情報を扱うエンティティをインポート
import com.example.moattravel.entity.User;

// Lombok（Getterの自動生成用）のクラスをインポート
import lombok.Getter;

@Getter // 2. クラス内の全フィールドに対する Getter メソッドを自動生成
public class SignupEvent extends ApplicationEvent { // 3. ApplicationEventを継承し、Springのイベントオブジェクトとして定義

    private User user; // 4. 新規登録したユーザー情報を保持するフィールド
    private String requestUrl; // 5. メール認証リンク作成用のリクエストURL（ドメインやパスなど）を保持するフィールド

    /**
     * コンストラクタ
     * @param source イベントの発生元（通常は処理を実行した Controller や Service など）
     * @param user 会員登録を行ったユーザーオブジェクト
     * @param requestUrl 認証メールに記載するリンク生成用URL
     */
    public SignupEvent(Object source, User user, String requestUrl) {
        super(source); // 6. 親クラス（ApplicationEvent）のコンストラクタを呼び出し、イベント発生元を登録
        this.user = user; // 7. 引数で受け取ったユーザー情報をフィールドにセット
        this.requestUrl = requestUrl; // 8. 引数で受け取ったURLをフィールドにセット
    }
}