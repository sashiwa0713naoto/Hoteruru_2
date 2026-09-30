package com.example.moattravel.entity; //データベースのreservationsテーブルと1対1で対応し、予約ID、関連する民宿やユーザー情報、チェックイン日、チェックアウト日、宿泊人数、合計宿泊料金、作成・更新日時、Stripe決済セッションIDなどの情報を保持するエンティティクラス。

// 日付・時間関連のJava標準クラスをインポート
import java.sql.Timestamp; // SQLのタイムスタンプ（日時）を扱うためのクラス
import java.time.LocalDate; // 年月日のみを扱う日付クラス

// JPA（データベースとJavaオブジェクトのマッピング用）のクラスをインポート
import jakarta.persistence.Column; // フィールドとデータベースのカラムをマッピングするためのアノテーション
import jakarta.persistence.Entity; // このクラスがDBテーブルに対応するエンティティであることを示すアノテーション
import jakarta.persistence.GeneratedValue; // 主キーの自動生成戦略を指定するためのアノテーション
import jakarta.persistence.GenerationType; // 主キーの自動生成の種類（Auto Increment等）を指定するための列挙型
import jakarta.persistence.Id; // このフィールドがテーブルの主キーであることを示すアノテーション
import jakarta.persistence.JoinColumn; // 外部キーのカラム名を指定するためのアノテーション
import jakarta.persistence.ManyToOne; // 多対1のリレーションシップを定義するためのアノテーション
import jakarta.persistence.Table; // 対応するデータベースのテーブル名を指定するためのアノテーション

// Lombok（Getter/Setter等の自動生成用）のクラスをインポート
import lombok.Data; // ゲッター、セッター、toString、equals、hashCodeなどを自動生成するアノテーション

@Entity // Spring/JPAに対して「このクラスはDBテーブルとマッピングされるエンティティです」と指示
@Table(name = "reservations") // 対応するデータベースのテーブル名を "reservations" に明示的に指定
@Data // Lombokにより、全フィールドのGetter/Setter、toString、equals、hashCode等を自動生成
public class Reservation {

    @Id // このフィールドがテーブルの主キー（Primary Key）であることを指定
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 主キーの値をDBのAuto Increment（自動連番）機能で生成
    @Column(name = "id") // 対応するカラム名を "id" に指定
    private Integer id;

    @ManyToOne // 多対1のリレーション（複数の予約が1つの民宿に関連づく）を定義
    @JoinColumn(name = "house_id") // 外部キーカラム名 "house_id" を指定して House エンティティと結合
    private House house;

    @ManyToOne // 多対1のリレーション（複数の予約が1人のユーザーに関連づく）を定義
    @JoinColumn(name = "user_id") // 外部キーカラム名 "user_id" を指定して User エンティティと結合
    private User user;

    @Column(name = "checkin_date") // チェックイン日を保持するカラム "checkin_date" とマッピング
    private LocalDate checkinDate;

    @Column(name = "checkout_date") // チェックアウト日を保持するカラム "checkout_date" とマッピング
    private LocalDate checkoutDate;

    @Column(name = "number_of_people") // 宿泊人数を保持するカラム "number_of_people" とマッピング
    private Integer numberOfPeople;

    @Column(name = "amount") // 合計宿泊料金（金額）を保持するカラム "amount" とマッピング
    private Integer amount;

    // 作成日時。DB側の初期設定（DEFAULT CURRENT_TIMESTAMP）に任せるため、insert/update対象外にする
    @Column(name = "created_at", insertable = false, updatable = false)
    private Timestamp createdAt;

    // 更新日時。DB側の自動更新機能に任せるため、insert/update対象外にする
    @Column(name = "updated_at", insertable = false, updatable = false)
    private Timestamp updatedAt;
    
    @Column(name = "stripe_session_id") // Stripe決済のセッション識別子（ID）を保持するカラム "stripe_session_id" とマッピング
    private String stripeSessionId;
    
    public String getStripeSessionId() {
        return stripeSessionId;
    }

    public void setStripeSessionId(String stripeSessionId) {
        this.stripeSessionId = stripeSessionId;
    }
}