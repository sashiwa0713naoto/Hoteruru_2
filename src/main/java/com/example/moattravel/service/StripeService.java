package com.example.moattravel.service; // 1. このクラスが属するパッケージ（サービス層）を指定

import java.time.LocalDate;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest; // クライアントのリクエストURL情報を取得するために使用

// Spring Framework のアノテーションおよび設定値注入機能をインポート
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// エンティティ・フォーム・リポジトリのインポート
import com.example.moattravel.entity.Reservation;
import com.example.moattravel.form.ReservationRegisterForm;
import com.example.moattravel.repository.HouseRepository;
import com.example.moattravel.repository.ReservationRepository;
import com.example.moattravel.repository.UserRepository;
// Stripe（決済SDK）用クラスのインポート
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;

@Service // 2. このクラスを Spring のサービス層コンポーネント（Bean）として定義
public class StripeService {
    private final ReservationRepository reservationRepository;
    private final HouseRepository houseRepository;
    private final UserRepository userRepository;

    // 3. application.properties から Stripe シークレットキーを注入
    @Value("${stripe.api-key}")
    private String stripeApiKey;

    // 4. コンストラクタインジェクション
    public StripeService(ReservationRepository reservationRepository,
                         HouseRepository houseRepository,
                         UserRepository userRepository) {
        this.reservationRepository = reservationRepository;
        this.houseRepository = houseRepository;
        this.userRepository = userRepository;
    }

    /**
     * 5. Stripe の Checkout（決済画面）用セッションを作成し、セッションIDを返すメソッド
     * 
     * @param houseName 民宿名
     * @param reservationRegisterForm 予約フォームデータ
     * @param httpServletRequest ドメイン・リクエストURL情報
     * @return 生成された Stripe チェックアウトセッションの ID
     */
    public String createStripeSession(String houseName, ReservationRegisterForm reservationRegisterForm, HttpServletRequest httpServletRequest) {
        // APIキーの設定
        Stripe.apiKey = stripeApiKey;
        
        // リクエストURLからベースURL（例: http://localhost:8080）を動的に組み立て
        String requestUrl = new String(httpServletRequest.getRequestURL());
        String baseUrl = requestUrl.replace(httpServletRequest.getRequestURI(), "");

        try {
            // Stripe API に渡すパラメータの構築
            SessionCreateParams params = SessionCreateParams.builder()
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD) // 決済方法（クレジットカード）
                .addLineItem(
                    SessionCreateParams.LineItem.builder()
                        .setQuantity(1L) // 数量（1固定）
                        .setPriceData(
                            SessionCreateParams.LineItem.PriceData.builder()
                                .setCurrency("jpy") // 通貨（日本円）
                                .setUnitAmount((long) reservationRegisterForm.getAmount()) // 決済金額（円）
                                .setProductData(
                                    SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                        .setName(houseName) // 決済画面に表示される商品名（民宿名）
                                        .build()
                                )
                                .build()
                        )
                        .build()
                )
                .setMode(SessionCreateParams.Mode.PAYMENT) // 単発決済モード
                .setSuccessUrl(baseUrl + "/reservations?reserved") // 決済成功時のリダイレクト先
                .setCancelUrl(baseUrl + "/houses/" + reservationRegisterForm.getHouseId()) // キャンセル時の戻り先
                
                // 6. 決済完了後（Webhook）で予約を作成するために必要なデータをメタデータとして添付
                .putMetadata("houseId", String.valueOf(reservationRegisterForm.getHouseId()))
                .putMetadata("userId", String.valueOf(reservationRegisterForm.getUserId()))
                .putMetadata("checkinDate", reservationRegisterForm.getCheckinDate())
                .putMetadata("checkoutDate", reservationRegisterForm.getCheckoutDate())
                .putMetadata("numberOfPeople", String.valueOf(reservationRegisterForm.getNumberOfPeople()))
                .putMetadata("amount", String.valueOf(reservationRegisterForm.getAmount()))
                .build();

            // Stripeサーバーへセッション作成リクエストを送信
            Session session = Session.create(params);
            return session.getId(); // 発行されたセッションIDを返す
        } catch (StripeException e) {
            System.err.println("Stripe API Exception: " + e.getMessage());
            return "";
        }
    }

    /**
     * 7. Stripe Webhook イベント（決済完了）発生時に呼び出され、DBへ予約データを保存するメソッド
     * 
     * @param session Stripe から通知された Checkout セッションオブジェクト
     */
    @Transactional // 8. データベース書き込み処理のトランザクション管理
    public void completeSession(Session session) {
        // Stripe セッションに埋め込んでおいたメタデータを取得
        Map<String, String> metadata = session.getMetadata();

        // メタデータ（文字列）から各種型へキャスト・パース
        Integer houseId = Integer.valueOf(metadata.get("houseId"));
        Integer userId = Integer.valueOf(metadata.get("userId"));
        LocalDate checkinDate = LocalDate.parse(metadata.get("checkinDate"));
        LocalDate checkoutDate = LocalDate.parse(metadata.get("checkoutDate"));
        Integer numberOfPeople = Integer.valueOf(metadata.get("numberOfPeople"));
        Integer amount = Integer.valueOf(metadata.get("amount"));

        // 9. 予約（Reservation）エンティティの生成と値の設定
        Reservation reservation = new Reservation();
        
        // getReferenceById を使用し、不要なSELECTクエリを発行せずにプロキシ参照のみを設定
        reservation.setHouse(houseRepository.getReferenceById(houseId));
        reservation.setUser(userRepository.getReferenceById(userId));
        
        reservation.setCheckinDate(checkinDate);
        reservation.setCheckoutDate(checkoutDate);
        reservation.setNumberOfPeople(numberOfPeople);
        reservation.setAmount(amount);

        // データベースに予約情報を保存
        reservationRepository.save(reservation);
    }
}