package com.example.moattravel.service; // Stripe決済との連携やチェックアウト画面の作成、決済完了後の予約データ保存といった処理をまとめて行うサービスクラス。

import java.time.LocalDate; // 年月日を扱うためのクラス
import java.util.Map; // キーと値のペアでデータを管理するための仕組み

import jakarta.servlet.http.HttpServletRequest; // クライアントからのリクエスト情報を取得するためのクラス

import org.springframework.beans.factory.annotation.Value; // 設定ファイルの値を取り込むための仕組み
import org.springframework.stereotype.Service; // Springにこのクラスがサービス層の部品であることを伝えるアノテーション
import org.springframework.transaction.annotation.Transactional; // 処理途中でエラーが起きたらデータベースの変更を自動で元に戻す仕組み

import com.example.moattravel.entity.Reservation; // 予約データをデータベースのテーブルと対応付けるためのクラス
import com.example.moattravel.form.ReservationRegisterForm; // 予約画面からの入力データを受け取るためのフォーム
import com.example.moattravel.repository.HouseRepository; // 民宿データをデータベースから探すための仕組み
import com.example.moattravel.repository.ReservationRepository; // 予約データのデータベース操作を行う仕組み
import com.example.moattravel.repository.UserRepository; // ユーザーデータをデータベースから探すための仕組み
import com.stripe.Stripe; // Stripe決済を利用するための基本的な仕組み
import com.stripe.exception.StripeException; // Stripe決済処理でエラーが発生したときのエラーを扱うクラス
import com.stripe.model.checkout.Session; // Stripeの決済画面（チェックアウト）のセッション情報を扱うクラス
import com.stripe.param.checkout.SessionCreateParams; // Stripeの決済画面を作成するためのパラメータを設定するクラス

@Service // このクラスをSpringの部品（サービス）として登録する
public class StripeService { // ネット決済サービスであるStripeと連携し、支払い画面の作成や決済完了後の処理を行うクラス

    private final ReservationRepository reservationRepository; // 予約データをデータベースに保存・取得するための仕組み
    private final HouseRepository houseRepository; // 民宿データをデータベースから探すための仕組み
    private final UserRepository userRepository; // ユーザーデータをデータベースから探すための仕組み

    @Value("${stripe.api-key}") // 設定ファイルからStripeの秘密鍵の値を読み込んでセットする
    private String stripeApiKey; // Stripeの機能を利用するための秘密鍵

    public StripeService(ReservationRepository reservationRepository,HouseRepository houseRepository, UserRepository userRepository) { // 必要なデータベース操作の仕組みをまとめて受け取るコンストラクタ
        this.reservationRepository = reservationRepository;
        this.houseRepository = houseRepository;
        this.userRepository = userRepository;
    }

    public String createStripeSession(String houseName, ReservationRegisterForm reservationRegisterForm, HttpServletRequest httpServletRequest) { // Stripeの決済画面を作成し、そのページへ移動するためのURLを返すメソッド
        Stripe.apiKey = stripeApiKey; // Stripeを利用するためのAPIキーを設定する
        
        String requestUrl = new String(httpServletRequest.getRequestURL()); // 現在のリクエストURLを取得する
        String baseUrl = requestUrl.replace(httpServletRequest.getRequestURI(), ""); // URLからサイトの基本アドレス（ドメイン部分）を取り出す

        try {
            SessionCreateParams params = SessionCreateParams.builder() // 決済画面に表示する内容や金額などの詳細設定を作成する
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD) // 支払い方法としてクレジットカードを指定する
                .addLineItem(
                		    SessionCreateParams.LineItem.builder()
                        .setQuantity(1L) // 購入する商品の数量を1つに設定する
                        .setPriceData(
                            SessionCreateParams.LineItem.PriceData.builder()
                                .setCurrency("jpy") // 通貨を日本円に設定する
                                .setUnitAmount((long) reservationRegisterForm.getAmount()) // 支払い金額をセットする
                                .setProductData(
                                    SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                        .setName(houseName) // 決済画面に表示する商品名として民宿名をセットする
                                        .build()
                                )
                                .build()
                        )
                        .build()
                )
                .setMode(SessionCreateParams.Mode.PAYMENT) // 1回限りの支払いモードに設定する
                .setSuccessUrl(baseUrl + "/reservations?reserved&session_id={CHECKOUT_SESSION_ID}") // 支払いが成功したときの移動先アドレスを指定する
                .setCancelUrl(baseUrl + "/houses/" + reservationRegisterForm.getHouseId()) // 支払いをやめて戻るときの移動先アドレスを指定する
                
                .putMetadata("houseId", String.valueOf(reservationRegisterForm.getHouseId())) // 決済完了後に識別するため民宿のIDを添える
                .putMetadata("userId", String.valueOf(reservationRegisterForm.getUserId())) // 決済完了後に識別するためユーザーのIDを添える
                .putMetadata("checkinDate", reservationRegisterForm.getCheckinDate()) // チェックイン日を添える
                .putMetadata("checkoutDate", reservationRegisterForm.getCheckoutDate()) // チェックアウト日を添える
                .putMetadata("numberOfPeople", String.valueOf(reservationRegisterForm.getNumberOfPeople())) // 宿泊人数を添える
                .putMetadata("amount", String.valueOf(reservationRegisterForm.getAmount())) // 金額を添える
                .build();

            Session session = Session.create(params); // Stripeのサーバーに決済画面の作成を依頼する
            return session.getUrl(); // 発行された決済画面のページURLを返す
        } catch (StripeException e) {
            System.err.println("Stripe API Exception: " + e.getMessage()); // Stripe関連のエラーが発生した場合は内容を出力する
            return ""; // エラー時は空文字を返す
        }
    }

    @Transactional // 処理の途中でエラーが発生した場合、データベースの変更をすべて自動で元に戻す
    public void completeSession(Session session) { // 決済完了後に、Stripeから受け取った情報をもとにデータベースへ予約データを保存するメソッド
        Map<String, String> metadata = session.getMetadata(); // 決済データに添えられていた詳細情報（メタデータ）を取り出す

        Integer houseId = Integer.valueOf(metadata.get("houseId")); // 民宿IDを取り出す
        Integer userId = Integer.valueOf(metadata.get("userId")); // ユーザーIDを取り出す
        LocalDate checkinDate = LocalDate.parse(metadata.get("checkinDate")); // チェックイン日を取り出す
        LocalDate checkoutDate = LocalDate.parse(metadata.get("checkoutDate")); // チェックアウト日を取り出す
        Integer numberOfPeople = Integer.valueOf(metadata.get("numberOfPeople")); // 宿泊人数を取り出す
        Integer amount = Integer.valueOf(metadata.get("amount")); // 金額を取り出す

        Reservation reservation = new Reservation(); // 新しい予約データの入れ物を作る
        
        reservation.setHouse(houseRepository.getReferenceById(houseId)); // 民宿データを効率よく関連付ける
        reservation.setUser(userRepository.getReferenceById(userId)); // ユーザーデータを効率よく関連付ける
        
        reservation.setCheckinDate(checkinDate); // チェックイン日をセットする
        reservation.setCheckoutDate(checkoutDate); // チェックアウト日をセットする
        reservation.setNumberOfPeople(numberOfPeople); // 宿泊人数をセットする
        reservation.setAmount(amount); // 金額をセットする

        reservationRepository.save(reservation); // 完成した予約データをデータベースに保存する
    }

    @Transactional // 処理の途中でエラーが発生した場合、データベースの変更をすべて自動で元に戻す
    public void createReservation(String sessionId) { // 決済IDをもとにStripeから詳細情報を取得し、予約データを作成・保存するメソッド
        try {
            Stripe.apiKey = stripeApiKey; // APIキーを設定する
            Session session = Session.retrieve(sessionId); // 決済IDからStripeの決済情報を取得する
            completeSession(session); // 取得した情報をもとに予約データを保存する処理を呼び出す
        } catch (StripeException e) {
            System.err.println("Stripe API Exception (Create Reservation): " + e.getMessage()); // エラー発生時は内容を出力する
        }
     }
}