package com.example.moattravel.event; //新規会員登録イベントが発生した際に、登録されたユーザー情報とメール認証リンク作成用のリクエストURLを保持してイベントリスナーへ受け渡すためのイベントクラス。

// Spring Frameworkのイベント基底クラスをインポート
import org.springframework.context.ApplicationEvent; // Springのイベント機能を実装するための基底クラス

// ユーザー情報を扱うエンティティをインポート
import com.example.moattravel.entity.User; // ユーザー情報を保持するエンティティクラス

// Lombok（Getterの自動生成用）のクラスをインポート
import lombok.Getter; // ゲッターを自動生成するアノテーション

@Getter // クラス内の全フィールドに対する Getter メソッドを自動生成
public class SignupEvent extends ApplicationEvent { // ApplicationEventを継承し、Springのイベントオブジェクトとして定義

    private User user; // 新規登録したユーザー情報を保持するフィールド
    private String requestUrl; // メール認証リンク作成用のリクエストURL（ドメインやパスなど）を保持するフィールド

    // コンストラクタ。sourceはイベントの発生元（通常は処理を実行した Controller や Service など）、userは会員登録を行ったユーザーオブジェクト、requestUrlは認証メールに記載するリンク生成用URL
    public SignupEvent(Object source, User user, String requestUrl) {
        super(source); // 親クラス（ApplicationEvent）のコンストラクタを呼び出し、イベント発生元を登録
        this.user = user; // 引数で受け取ったユーザー情報をフィールドにセット
        this.requestUrl = requestUrl; // 引数で受け取ったURLをフィールドにセット
    }
}