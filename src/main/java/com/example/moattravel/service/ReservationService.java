package com.example.moattravel.service; // 予約情報の登録や金額計算、予約一覧の取得といった処理をまとめて行うサービスクラス。

import java.time.LocalDate; // 年月日を扱うためのクラス
import java.time.temporal.ChronoUnit; // 日付同士の差（宿泊日数など）を計算するための仕組み

import org.springframework.data.domain.Page; // データをページごとに分けて取得するための仕組み
import org.springframework.data.domain.Pageable; // ページの順番や表示件数を指定するための仕組み
import org.springframework.stereotype.Service; // Springにこのクラスがサービス層の部品であることを伝えるアノテーション
import org.springframework.transaction.annotation.Transactional; // 処理途中でエラーが起きたらデータベースの変更を自動で元に戻す仕組み

import com.example.moattravel.entity.House; // 民宿データをデータベースのテーブルと対応付けるためのクラス
import com.example.moattravel.entity.Reservation; // 予約データをデータベースのテーブルと対応付けるためのクラス
import com.example.moattravel.entity.User; // ユーザーデータをデータベースのテーブルと対応付けるためのクラス
import com.example.moattravel.repository.HouseRepository; // 民宿データをデータベースから探すための仕組み
import com.example.moattravel.repository.ReservationRepository; // 予約データのデータベース操作を行う仕組み
import com.example.moattravel.repository.UserRepository; // ユーザーデータをデータベースから探すための仕組み

@Service // このクラスをSpringの部品（サービス）として登録する
public class ReservationService { // 予約に関するさまざまな処理をまとめて実行するクラス

    private final ReservationRepository reservationRepository; // 予約データをデータベースに保存・取得するための仕組み
    private final HouseRepository houseRepository; // 民宿データをデータベースから探すための仕組み
    private final UserRepository userRepository; // ユーザーデータをデータベースから探すための仕組み

    public ReservationService(ReservationRepository reservationRepository, 
                              HouseRepository houseRepository, 
                              UserRepository userRepository) { // 必要なデータベース操作の仕組みをまとめて受け取るコンストラクタ
        this.reservationRepository = reservationRepository;
        this.houseRepository = houseRepository;
        this.userRepository = userRepository;
    }

    public Integer calculateAmount(LocalDate checkinDate, LocalDate checkoutDate, Integer price) { // チェックイン日からチェックアウト日までの日数と料金から、宿泊料金の合計を計算するメソッド
        long nights = ChronoUnit.DAYS.between(checkinDate, checkoutDate); // チェックインからチェックアウトまでの宿泊日数（泊数）を計算する
        int amount = price * (int) nights; // 1泊あたりの料金に泊数をかけて合計金額を計算する
        return amount; // 計算した合計金額を返す
    }

    @Transactional // 処理の途中でエラーが発生した場合、データベースの変更をすべて自動で元に戻す
    public void createReservation(Integer houseId, 
                                    Integer userId, 
                                    LocalDate checkinDate, 
                                    LocalDate checkoutDate, 
                                    Integer numberOfPeople, 
                                    String stripeSessionId) { // 新しい予約情報をデータベースに登録するメソッド
        
        House house = houseRepository.findById(houseId)
                .orElseThrow(() -> new IllegalArgumentException("指定された民宿が見つかりません。ID: " + houseId)); // 予約する民宿をIDで探し、見つからなければエラーを発生させる
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("指定されたユーザーが見つかりません。ID: " + userId)); // 予約するユーザーをIDで探し、見つからなければエラーを発生させる

        Integer amount = calculateAmount(checkinDate, checkoutDate, house.getPrice()); // 宿泊料金の合計を計算する

        Reservation reservation = new Reservation(); // 新しい予約データの入れ物を作る
        reservation.setHouse(house); // 予約する民宿の情報をセットする
        reservation.setUser(user); // 予約したユーザーの情報をセットする
        reservation.setCheckinDate(checkinDate); // チェックイン日をセットする
        reservation.setCheckoutDate(checkoutDate); // チェックアウト日をセットする
        reservation.setNumberOfPeople(numberOfPeople); // 宿泊人数をセットする
        reservation.setAmount(amount); // 計算した合計金額をセットする
        reservation.setStripeSessionId(stripeSessionId); // 決済情報を確認するためのIDをセットする

        reservationRepository.save(reservation); // 作成した予約データをデータベースに保存する
    }

    public Page<Reservation> findAllReservations(Pageable pageable) { // 【管理者用】すべての予約情報を新しい順にページ単位で取得するメソッド
        return reservationRepository.findAllByOrderByCreatedAtDesc(pageable); // 予約情報を新しい順にまとめて取得して返す
    }

    public Page<Reservation> findReservationsByUserOrderByCreatedAtDesc(User user, Pageable pageable) { // 【会員用】特定のユーザーの予約情報を新しい順にページ単位で取得するメソッド
        return reservationRepository.findByUserOrderByCreatedAtDesc(user, pageable); // 指定したユーザーの予約情報を新しい順にまとめて取得して返す
    }
}