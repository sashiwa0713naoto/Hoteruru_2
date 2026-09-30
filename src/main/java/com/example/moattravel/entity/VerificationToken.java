package com.example.moattravel.entity; //データベースのverification_tokensテーブルと1対1で対応し、認証トークンID、紐づくユーザー情報、メール認証用トークン文字列、作成・更新日時などの情報を保持するエンティティクラス。

// 日時情報を扱うJava標準クラスをインポート
import java.sql.Timestamp; // SQLのタイムスタンプ（日時）を扱うためのクラス

// JPA（データベースとJavaオブジェクトのマッピング用）のクラスをインポート
import jakarta.persistence.Column; // フィールドとデータベースのカラムをマッピングするためのアノテーション
import jakarta.persistence.Entity; // このクラスがDBテーブルに対応するエンティティであることを示すアノテーション
import jakarta.persistence.GeneratedValue; // 主キーの自動生成戦略を指定するためのアノテーション
import jakarta.persistence.GenerationType; // 主キーの自動生成の種類（Auto Increment等）を指定するための列挙型
import jakarta.persistence.Id; // このフィールドがテーブルの主キーであることを示すアノテーション
import jakarta.persistence.JoinColumn; // 外部キーのカラム名を指定するためのアノテーション
import jakarta.persistence.OneToOne; // 1対1のリレーションシップを定義するためのアノテーション
import jakarta.persistence.Table; // 対応するデータベースのテーブル名を指定するためのアノテーション

// Lombok（Getter/Setter等の自動生成用）のクラスをインポート
import lombok.Data; // ゲッター、セッター、toString、equals、hashCodeなどを自動生成するアノテーション

@Entity // Spring/JPAに対して「このクラスはDBテーブルとマッピングされるエンティティです」と指示
@Table(name = "verification_tokens") // 対応するデータベースのテーブル名を "verification_tokens" に明示的に指定
@Data // Lombokにより、全フィールドのGetter/Setter、toString、equals、hashCode等を自動生成
public class VerificationToken {

    @Id // このフィールドがテーブルの主キー（Primary Key）であることを指定
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 主キーの値をDBのAuto Increment（自動連番）機能で生成
    @Column(name = "id") // 対応するカラム名を "id" に指定
    private Integer id;

    @OneToOne // 1対1のリレーション（1つの認証トークンが1人のユーザーに紐づく）を定義
    @JoinColumn(name = "user_id") // 外部キーカラム名 "user_id" を指定して User エンティティと結合
    private User user;

    @Column(name = "token") // メール認証用のランダムなトークン文字列（UUIDなど）を保持するカラム "token" とマッピング
    private String token;

    // 作成日時。DB側の初期設定（DEFAULT CURRENT_TIMESTAMP）に任せるため、insert/update対象外にする
    @Column(name = "created_at", insertable = false, updatable = false)
    private Timestamp createdAt;

    // 更新日時。DB側の自動更新機能に任せるため、insert/update対象外にする
    @Column(name = "updated_at", insertable = false, updatable = false)
    private Timestamp updatedAt;
}