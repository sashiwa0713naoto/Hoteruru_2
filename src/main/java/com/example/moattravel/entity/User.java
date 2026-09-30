package com.example.moattravel.entity; //データベースのusersテーブルと1対1で対応し、ユーザーID、氏名、フリガナ、郵便番号、住所、電話番号、メールアドレス、暗号化パスワード、権限（ロール）、アカウント有効化フラグ、作成・更新日時などの情報を保持するエンティティクラス。

// 日時情報を扱うJava標準クラスをインポート
import java.sql.Timestamp; // SQLのタイムスタンプ（日時）を扱うためのクラス

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
@Table(name = "users") // 対応するデータベースのテーブル名を "users" に明示的に指定
@Data // Lombokにより、全フィールドのGetter/Setter、toString、equals、hashCode等を自動生成
public class User {

    @Id // このフィールドがテーブルの主キー（Primary Key）であることを指定
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 主キーの値をDBのAuto Increment（自動連番）機能で生成
    @Column(name = "id") // 対応するカラム名を "id" に指定
    private Integer id;

    @Column(name = "name") // 氏名を保持するカラム "name" とマッピング
    private String name;

    @Column(name = "furigana") // フリガナを保持するカラム "furigana" とマッピング
    private String furigana;

    @Column(name = "postal_code") // 郵便番号を保持するカラム "postal_code" とマッピング
    private String postalCode;

    @Column(name = "address") // 住所を保持するカラム "address" とマッピング
    private String address;

    @Column(name = "phone_number") // 電話番号を保持するカラム "phone_number" とマッピング
    private String phoneNumber;

    @Column(name = "email") // メールアドレス（ログインID）を保持するカラム "email" とマッピング
    private String email;

    @Column(name = "password") // 暗号化されたパスワードを保持するカラム "password" とマッピング
    private String password;

    @ManyToOne // 多対1のリレーション（複数のユーザーが1つのロールを持つ）を定義
    @JoinColumn(name = "role_id") // 外部キーカラム名 "role_id" を指定して Role エンティティと結合
    private Role role;

    @Column(name = "enabled") // アカウントの有効/無効状態（メール認証完了フラグ）を保持するカラム "enabled" とマッピング
    private Boolean enabled;

    // 作成日時。DB側の初期設定（DEFAULT CURRENT_TIMESTAMP）に任せるため、insert/update対象外にする
    @Column(name = "created_at", insertable = false, updatable = false)
    private Timestamp createdAt;

    // 更新日時。DB側の自動更新機能に任せるため、insert/update対象外にする
    @Column(name = "updated_at", insertable = false, updatable = false)
    private Timestamp updatedAt;
}