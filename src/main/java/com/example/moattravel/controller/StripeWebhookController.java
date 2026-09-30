package com.example.moattravel.controller; //StripeからのWebhook（決済完了などの非同期通知）を受信して検証し、決済完了に伴うデータベースの予約データ保存処理などを制御するコントローラークラス。

// --- Spring Framework / Spring Boot 関連のインポート ---
import org.springframework.beans.factory.annotation.Value; // application.propertiesなどの設定値をフィールドに注入するためのアノテーション
import org.springframework.http.ResponseEntity; // HTTPレスポンスのステータスコードやボディを自由に変更して返すためのクラス
import org.springframework.stereotype.Controller; // Spring Bootに「このクラスはWebリクエスト（Webhookなど）を受け付けるコントローラー」と認識させる
import org.springframework.web.bind.annotation.PostMapping; // POSTリクエストを受け付けるためのアノテーション
import org.springframework.web.bind.annotation.RequestBody; // HTTPリクエストのボディ（中身のJSONテキスト）をそのまま受け取るためのアノテーション
import org.springframework.web.bind.annotation.RequestHeader; // HTTPリクエストのヘッダー情報（署名など）を受け取るためのアノテーション

// --- アプリケーション内のサービスクラスのインポート ---
import com.example.moattravel.service.StripeService; // Stripe決済に関するビジネスロジックやデータベース更新処理を行うクラス
// --- Stripe Java SDK 関連のインポート ---
import com.stripe.exception.SignatureVerificationException; // StripeからのWebhook署名検証に失敗した際に発生する例外クラス
import com.stripe.model.Event; // Stripeから送信されたイベント情報を格納するクラス
import com.stripe.model.checkout.Session; // Checkoutのセッション情報（決済データ）を扱うためのクラス
import com.stripe.net.Webhook; // StripeからのWebhookリクエストを検証・解析するためのユーティリティクラス

@Controller // コントローラークラスであることをSpring Bootに登録
public class StripeWebhookController {

    // Stripe関連のデータ処理や更新を行うサービスを定義（finalで書き換え不可に保護）
    private final StripeService stripeService;
    
    // application.properties に設定されている "stripe.webhook-secret" の値を自動で読み込んで代入
    @Value("${stripe.webhook-secret}")
    private String webhookSecret;

    // コンストラクタインジェクション（Spring起動時に依存するStripeServiceを自動でセットする）
    public StripeWebhookController(StripeService stripeService) {
        this.stripeService = stripeService;
    }

    // StripeからのWebhookリクエストを受け取るエンドポイント
    @PostMapping("/stripe/webhook") // POSTリクエストのパス（URL: /stripe/webhook）を指定
    public ResponseEntity<String> webhook(@RequestBody String payload, @RequestHeader("Stripe-Signature") String sigHeader) {
        Event event = null;

        // 【ステップ1】リクエストの署名検証（第三者によるなりすましや改ざんリクエストを防止するセキュリティ処理）
        try {
            event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
        } catch (SignatureVerificationException e) {
            // 署名検証に失敗した場合は不正なリクエストとみなし、HTTP 400 Bad Request を返却して処理を中断
            return ResponseEntity.status(400).body("Webhook signature verification failed.");
        }

        // 【ステップ2】イベントの種別判定
        // 受信したイベントが「Checkoutの決済が完了したイベント（checkout.session.completed）」であるか確認
        if ("checkout.session.completed".equals(event.getType())) {
            
            // イベントオブジェクトから安全に Session データ（決済セッション情報）を抽出する
            Session session = (Session) event.getDataObjectDeserializer().getObject().orElse(null);
            
            // Sessionが存在する場合は、StripeServiceを呼び出して決済完了に伴うデータベースの予約データ保存処理等を実行
            if (session != null) {
                stripeService.completeSession(session);
            }
        }

        // 【ステップ3】Stripe側に対して、Webhookを正常に受信・処理できたことを通知するため、HTTP 200 OK を返却
        return ResponseEntity.status(200).body("Success");
    }
}