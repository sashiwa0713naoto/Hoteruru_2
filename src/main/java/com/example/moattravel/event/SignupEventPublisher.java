package com.example.moattravel.event; //新規会員登録が完了した際などに、Springのイベント配信機能を使ってSignupEventを生成・発行（パブリッシュ）し、リスナーへ通知するためのパブリッダークラス。

// Spring Framework（イベントパブリッシャー・コンポーネント）のクラスをインポート
import org.springframework.context.ApplicationEventPublisher; // Springのイベントを発行するためのインターフェース
import org.springframework.stereotype.Component; // SpringのコンポーネントとしてDIコンテナに登録するためのアノテーション

// アプリケーション内のエンティティをインポート
import com.example.moattravel.entity.User; // ユーザー情報を保持するエンティティクラス

@Component // Springに「このクラスはコンポーネント（Bean）です」と認識させ、自動検出・管理対象にする
public class SignupEventPublisher {

    // Springのイベント配信・通知機能を担う ApplicationEventPublisher を保持するフィールド
    private final ApplicationEventPublisher applicationEventPublisher;

    // コンストラクタインジェクション（Springが自動的に ApplicationEventPublisher を注入）
    public SignupEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    // 会員登録イベント（SignupEvent）を発行するメソッド。userは新規登録されたユーザー情報、requestUrlは認証メール用リンク生成に必要なリクエストURL
    public void publishSignupEvent(User user, String requestUrl) {
        // SignupEvent のインスタンスを作成し、Springコンテナ全体へイベントを発行（Publish）する
        applicationEventPublisher.publishEvent(new SignupEvent(this, user, requestUrl));
    }
}