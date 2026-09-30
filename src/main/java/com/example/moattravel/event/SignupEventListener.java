package com.example.moattravel.event; //新規会員登録イベント（SignupEvent）を受け取り、一意の認証トークンの生成・DB保存およびメール認証用のリンクを含んだ確認メールの送信を行うリスナークラス。

import java.util.UUID; // ランダムなユニークIDを生成するためのクラス

import org.springframework.context.event.EventListener; // Springのイベントハンドラーメソッドであることを示すアノテーション
import org.springframework.mail.SimpleMailMessage; // プレーンテキストのメールメッセージを構築するためのクラス
import org.springframework.mail.javamail.JavaMailSender; // メールを送信するためのインターフェース
import org.springframework.stereotype.Component; // SpringのコンポーネントとしてDIコンテナに登録するためのアノテーション

import com.example.moattravel.entity.User; // ユーザー情報を保持するエンティティクラス
import com.example.moattravel.service.VerificationTokenService; // 認証トークンのデータベース保存処理等を行うサービス

@Component // SpringのコンポーネントとしてDIコンテナに登録
public class SignupEventListener {
    
    // 依存するサービスのフィールド定義（不変にするため final を付与）
    private final VerificationTokenService verificationTokenService;
    private final JavaMailSender javaMailSender;

    // コンストラクタ注入（DI）。Springが自動的に必要なBean（ServiceやMailSender）を注入する
    public SignupEventListener(VerificationTokenService verificationTokenService, JavaMailSender javaMailSender) {
        this.verificationTokenService = verificationTokenService;
        this.javaMailSender = javaMailSender;
    }

    // SignupEventが発生した際に自動的に呼び出されるイベントハンドラー。signupEventは会員登録イベントオブジェクト（登録ユーザー情報やリクエストURLを保持）
    @EventListener // イベント受領用アノテーション
    public void onSignupEvent(SignupEvent signupEvent) {
        // イベントオブジェクトから登録されたユーザー情報を取得
        User user = signupEvent.getUser();
        
        // メール認証用のユニーク（唯一）なトークン文字列をUUIDで生成
        String token = UUID.randomUUID().toString();
        
        // 生成したトークンとユーザー情報をDBに保存（有効期限等の管理用）
        verificationTokenService.create(user, token);
        
        // 送信メールの基本情報設定
        String senderAddress = "springboot.samurai@example.com"; // 送信元メールアドレス
        String recipientAddress = user.getEmail(); // 送信先（登録ユーザーのメールアドレス）
        String subject = "メール認証"; // 件名
        
        // 会員登録を完了するための認証用URLを作成（ドメイン名 + パス + 生成したトークン）
        String confirmationUrl = signupEvent.getRequestUrl() + "/signup/verify?token=" + token;
        
        // 開発・テスト用にコンソールへ認証用リンクを出力（ローカル実行時の動作確認用）
        System.out.println("==================================================");
        System.out.println("【認証用リンク】: " + confirmationUrl);
        System.out.println("==================================================");

        // SimpleMailMessage オブジェクトを作成し、メール本文・宛先等をセット
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(senderAddress);
        mailMessage.setTo(recipientAddress);
        mailMessage.setSubject(subject);
        mailMessage.setText("以下のリンクをクリックして会員登録を完了してください。\n" + confirmationUrl);
        
        // メール送信処理（SMTPサーバー未設定時などのエラーでアプリが停止しないよう try-catch で保護）
        try {
            javaMailSender.send(mailMessage);
        } catch (Exception e) {
            // メール送信失敗時もコンソールのURLからテストを続行できるようログを出力
            System.out.println("メール送信に失敗しましたが、上記のコンソールリンクから認証できます。");
        }
    }
}