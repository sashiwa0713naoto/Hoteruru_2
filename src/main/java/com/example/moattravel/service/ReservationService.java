package com.example.moattravel.service; // 1. このクラスが属するパッケージ（サービス層）を指定


import java.time.LocalDate;
import java.time.temporal.ChronoUnit; // 日付間の差分（宿泊日数）を計算するために利用

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
// Spring Framework のアノテーションおよびトランザクション管理機能をインポート
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// エンティティとリポジトリクラスをインポート
import com.example.moattravel.entity.House;
import com.example.moattravel.entity.Reservation;
import com.example.moattravel.entity.User;
import com.example.moattravel.repository.HouseRepository;
import com.example.moattravel.repository.ReservationRepository;
import com.example.moattravel.repository.UserRepository;


@Service // 2. このクラスを Spring のサービス層コンポーネント（Bean）として自動登録
public class ReservationService {

    // 依存するリポジトリ群のフィールド宣言（不変にするため final）
    private final ReservationRepository reservationRepository;
    private final HouseRepository houseRepository;
    private final UserRepository userRepository;

    // 3. コンストラクタインジェクション（3つのリポジトリを自動注入）
    public ReservationService(ReservationRepository reservationRepository, 
                              HouseRepository houseRepository, 
                              UserRepository userRepository) {
        this.reservationRepository = reservationRepository;
        this.houseRepository = houseRepository;
        this.userRepository = userRepository;
    }

    /**
     * 4. チェックイン日とチェックアウト日から宿泊日数を算出し、合計金額を計算するメソッド
     * @param checkinDate チェックイン日
     * @param checkoutDate チェックアウト日
     * @param price 1泊あたりの宿泊料金
     * @return 合計金額（円）
     */
    public Integer calculateAmount(LocalDate checkinDate, LocalDate checkoutDate, Integer price) {
        // ChronoUnit.DAYS.between でチェックインからチェックアウトまでの日数（泊数）を取得
        long nights = ChronoUnit.DAYS.between(checkinDate, checkoutDate);
        int amount = price * (int) nights;
        return amount;
    }

    /**
     * 5. Stripe決済完了後などに、予約情報を生成してDBに登録する処理
     * @param houseId 予約対象の民宿ID
     * @param userId 予約を行ったユーザーID
     * @param checkinDate チェックイン日
     * @param checkoutDate チェックアウト日
     * @param numberOfPeople 宿泊人数
     * @param stripeSessionId Stripe決済のセッションID
     */
    @Transactional // 6. 一連のDB保存操作を単一のトランザクションとして実行（エラー発生時は自動ロールバック）
    public void createReservation(Integer houseId, 
                                  Integer userId, 
                                  LocalDate checkinDate, 
                                  LocalDate checkoutDate, 
                                  Integer numberOfPeople, 
                                  String stripeSessionId) {
        
        // IDに該当する民宿が存在しない場合は例外（IllegalArgumentException）をスロー
        House house = houseRepository.findById(houseId)
                .orElseThrow(() -> new IllegalArgumentException("指定された民宿が見つかりません。ID: " + houseId));
        
        // IDに該当するユーザーが存在しない場合は例外をスロー
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("指定されたユーザーが見つかりません。ID: " + userId));

        // 宿泊料金を動的に計算
        Integer amount = calculateAmount(checkinDate, checkoutDate, house.getPrice());

        // Reservation エンティティを生成し、各フィールドの値をセット
        Reservation reservation = new Reservation();
        reservation.setHouse(house);
        reservation.setUser(user);
        reservation.setCheckinDate(checkinDate);
        reservation.setCheckoutDate(checkoutDate);
        reservation.setNumberOfPeople(numberOfPeople);
        reservation.setAmount(amount);
        reservation.setStripeSessionId(stripeSessionId); // 決済履歴との突合用に保存

        // 予約レコードを DB に永続化（INSERT）
        reservationRepository.save(reservation);
    }
    
    //8. 【管理者用】すべての予約情報を作成日時の降順でページング取得するメソッド

    public Page<Reservation> findAllReservations(Pageable pageable) {
        return reservationRepository.findAllByOrderByCreatedAtDesc(pageable);
    }


     //9. 【会員用】特定のユーザーの予約情報を作成日時の降順でページング取得するメソッド

    public Page<Reservation> findReservationsByUserOrderByCreatedAtDesc(User user, Pageable pageable) {
        return reservationRepository.findByUserOrderByCreatedAtDesc(user, pageable);
    }

		
 }
