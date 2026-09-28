package com.example.moattravel.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import com.example.moattravel.service.ReservationService;
import com.example.moattravel.service.StripeService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;

/**
 * StripeからのWebhook（決済完了などの非同期通知）を受信して処理するコントローラー
 */
@Controller
public class StripeWebhookController {

    // Stripe関連の処理を行うサービスクラス
    private final StripeService stripeService;
    
    // 予約関連の処理を行うサービスクラス
    private final ReservationService reservationService;

    // application.properties に設定された Webhook シークレットキーを注入
    @Value("${stripe.webhook-secret}")
    private String webhookSecret;

    // コンストラクタによる依存性の注入（DI）
    public StripeWebhookController(StripeService stripeService, ReservationService reservationService) {
        this.stripeService = stripeService;
        this.reservationService = reservationService;
    }

    /**
     * StripeからのWebhookリクエストを受け取るエンドポイント
     * 
     * @param payload   Stripeから送信された生のリクエストボディ（JSONテキスト）
     * @param sigHeader Stripeのリクエストヘッダーに含まれる検証用署名（Stripe-Signature）
     * @return Stripeに対するレスポンス（成功時は200、検証失敗時は400）
     */
    @PostMapping("/stripe/webhook")
    public ResponseEntity<String> webhook(@RequestBody String payload, @RequestHeader("Stripe-Signature") String sigHeader) {
        Event event = null;

        // 【ステップ1】リクエストの署名検証（第三者によるなりすましリクエストを防止）
        try {
            event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
        } catch (SignatureVerificationException e) {
            // 署名検証に失敗した場合は不正なリクエストとみなし HTTP 400 を返却
            return ResponseEntity.status(400).body("Webhook signature verification failed.");
        }

        // 【ステップ2】イベントの種別判定
        // 「Checkoutの決済が完了したイベント（checkout.session.completed）」であるか確認
        if ("checkout.session.completed".equals(event.getType())) {
            
            // イベントオブジェクトから安全に Session データ（決済セッション情報）を抽出
            Session session = (Session) event.getDataObjectDeserializer().getObject().orElse(null);
            
            // Sessionが存在する場合は、決済完了に伴うデータ更新処理などを実行
            if (session != null) {
                stripeService.completeSession(session);
            }
        }

        // 【ステップ3】Stripe側に正常受信したことを通知（HTTP 200 OK）
        return ResponseEntity.status(200).body("Success");
    }
}