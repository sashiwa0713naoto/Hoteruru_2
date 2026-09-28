package com.example.moattravel.event; // 1. このクラスが属するパッケージ（フォルダ構成）を指定

// Spring Framework（イベントパブリッシャー・コンポーネント）のクラスをインポート
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

// アプリケーション内のエンティティをインポート
import com.example.moattravel.entity.User;

@Component // 2. Springに「このクラスはコンポーネント（Bean）です」と認識させ、自動検出・管理対象にする
public class SignupEventPublisher {

    // 3. Springのイベント配信・通知機能を担う ApplicationEventPublisher を保持するフィールド
    private final ApplicationEventPublisher applicationEventPublisher;

    // 4. コンストラクタインジェクション（Springが自動的に ApplicationEventPublisher を注入）
    public SignupEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    /**
     * 会員登録イベント（SignupEvent）を発行するメソッド
     * @param user 新規登録されたユーザー情報
     * @param requestUrl 認証メール用リンク生成に必要なリクエストURL
     */
    public void publishSignupEvent(User user, String requestUrl) {
        // 5. SignupEvent のインスタンスを作成し、Springコンテナ全体へイベントを発行（Publish）する
        applicationEventPublisher.publishEvent(new SignupEvent(this, user, requestUrl));
    }
}