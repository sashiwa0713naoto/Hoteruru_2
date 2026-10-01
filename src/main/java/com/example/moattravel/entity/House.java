package com.example.moattravel.entity; //データベースのhousesテーブルと1対1で対応し、民宿のID、名称、画像名、説明文、宿泊料金、定員、郵便番号、住所、電話番号などの情報として保持する(エンティティ)クラス。

import java.sql.Timestamp;//日時・タイムスタンプ）を扱うためのクラス

import jakarta.persistence.Column; // フィールドとデータベースのカラムをマッピングするためのアノテーション(@Columnを使うための付箋）*jakarta.persistenceとはJavaでデータベースを扱うための標準規格
import jakarta.persistence.Entity; // このクラス(entity)がDBテーブルに対応するクラス(エンティティ)であることを示すアノテーション
import jakarta.persistence.GeneratedValue; // 主キーの自動生成戦略を指定するためのアノテーション(@GeneratedValueを使うための付箋）
import jakarta.persistence.GenerationType; // 主キーの自動生成の種類（Auto Increment等）を指定するための列挙型
import jakarta.persistence.Id; // このフィールドがテーブルの主キーであることを示すアノテーション
import jakarta.persistence.Table; // 対応するデータベースのテーブル名を指定するためのアノテーション

// Lombok（Getter/Setter等のボイラープレートコード自動生成用）のクラスをインポート
import lombok.Data; // ゲッター、セッター、toString、equals、hashCodeなどを自動生成するアノテーション

@Entity // Spring/JPAに対して「このクラスはDBテーブルとマッピングされるエンティティです」と指示
@Table(name = "houses") // 対応するデータベースのテーブル名を "houses" に明示的に指定
@Data // Lombokにより、全フィールドのGetter/Setter、toString、equals、hashCode等を自動生成
public class House {

    @Id // このフィールドがテーブルの主キー（Primary Key）であることを指定
    @GeneratedValue(strategy = GenerationType.IDENTITY) // このID（主キー）はデータがデータベースに保存されるタイミングで、DB側の自動連番（Auto Increment）機能を使って自動で採番・設定する機能
    @Column(name = "id") // 対応するカラム名を "id" に指定
    private Integer id; // 民宿の固有ID（主キー）を保持するフィールド

    @Column(name = "name") // 民宿名を保持するカラム "name" とマッピング
    private String name; // 民宿の名称（タイトル）を保持するフィールド

    @Column(name = "image_name") // 画像ファイル名を保持するカラム "image_name" とマッピング
    private String imageName; // 民宿の紹介画像ファイル名を保持するフィールド

    @Column(name = "description") // 民宿の説明文を保持するカラム "description" とマッピング
    private String description; // 民宿の詳細な説明文を保持するフィールド

    @Column(name = "price") // 1泊あたりの宿泊料金を保持するカラム "price" とマッピング
    private Integer price; // 民宿の1泊あたりの料金（金額）を保持するフィールド

    @Column(name = "capacity") // 宿泊可能人数（定員）を保持するカラム "capacity" とマッピング
    private Integer capacity; // 宿泊可能な最大人数（定員）を保持するフィールド

    @Column(name = "postal_code") // 郵便番号を保持するカラム "postal_code" とマッピング
    private String postalCode; // 民宿の所在地の郵便番号を保持するフィールド

    @Column(name = "address") // 住所を保持するカラム "address" とマッピング
    private String address; // 民宿の所在地の住所を保持するフィールド

    @Column(name = "phone_number") // 電話番号を保持するカラム "phone_number" とマッピング
    private String phoneNumber; // 民宿の連絡先電話番号を保持するフィールド
    
    @Column(name = "created_at", insertable = false, updatable = false) // 登録日時を保持するカラム "created_at" とマッピング（Java側からのINSERT/UPDATE対象外にしてDB側で自動管理）
    private Timestamp createdAt; // レコードが新規作成された日時を保持するフィールド

    @Column(name = "updated_at", insertable = false, updatable = false) // 更新日時を保持するカラム "updated_at" とマッピング（Java側からのINSERT/UPDATE対象外にしてDB側で自動管理）
    private Timestamp updatedAt; // レコードが最後に更新された日時を保持するフィールド

}