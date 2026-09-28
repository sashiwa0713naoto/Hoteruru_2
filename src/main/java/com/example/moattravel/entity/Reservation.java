package com.example.moattravel.entity; // 1. このクラスが属するパッケージ（フォルダ構成）を指定

// 日付・時間関連のJava標準クラスをインポート


import java.sql.Timestamp;
import java.time.LocalDate;

// JPA（データベースとJavaオブジェクトのマッピング用）のクラスをインポート
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

// Lombok（Getter/Setter等の自動生成用）のクラスをインポート
import lombok.Data;

@Entity // 2. Spring/JPAに対して「このクラスはDBテーブルとマッピングされるエンティティです」と指示
@Table(name = "reservations") // 3. 対応するデータベースのテーブル名を "reservations" に明示的に指定
@Data // 4. Lombokにより、全フィールドのGetter/Setter、toString、equals、hashCode等を自動生成
public class Reservation {

    @Id // 5. このフィールドがテーブルの主キー（Primary Key）であることを指定
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 6. 主キーの値をDBのAuto Increment（自動連番）機能で生成
    @Column(name = "id") // 7. 対応するカラム名を "id" に指定
    private Integer id;

    @ManyToOne // 8. 多対1のリレーション（複数の予約が1つの民宿に関連づく）を定義
    @JoinColumn(name = "house_id") // 9. 外部キーカラム名 "house_id" を指定して House エンティティと結合
    private House house;

    @ManyToOne // 10. 多対1のリレーション（複数の予約が1人のユーザーに関連づく）を定義
    @JoinColumn(name = "user_id") // 11. 外部キーカラム名 "user_id" を指定して User エンティティと結合
    private User user;

    @Column(name = "checkin_date") // 12. チェックイン日を保持するカラム "checkin_date" とマッピング
    private LocalDate checkinDate;

    @Column(name = "checkout_date") // 13. チェックアウト日を保持するカラム "checkout_date" とマッピング
    private LocalDate checkoutDate;

    @Column(name = "number_of_people") // 14. 宿泊人数を保持するカラム "number_of_people" とマッピング
    private Integer numberOfPeople;

    @Column(name = "amount") // 15. 合計宿泊料金（金額）を保持するカラム "amount" とマッピング
    private Integer amount;

    // 16. 作成日時。DB側の初期設定（DEFAULT CURRENT_TIMESTAMP）に任せるため、insert/update対象外にする
    @Column(name = "created_at", insertable = false, updatable = false)
    private Timestamp createdAt;

    // 17. 更新日時。DB側の自動更新機能に任せるため、insert/update対象外にする
    @Column(name = "updated_at", insertable = false, updatable = false)
    private Timestamp updatedAt;
    
    @Column(name = "stripe_session_id") // 18. Stripe決済のセッション識別子（ID）を保持するカラム "stripe_session_id" とマッピング
    private String stripeSessionId;
    
    public String getStripeSessionId() {
        return stripeSessionId;
    }

    public void setStripeSessionId(String stripeSessionId) {
        this.stripeSessionId = stripeSessionId;
    }
}