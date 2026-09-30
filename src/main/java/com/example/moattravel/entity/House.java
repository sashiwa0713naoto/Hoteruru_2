package com.example.moattravel.entity; //データベースのhousesテーブルと1対1で対応し、民宿のID、名称、画像名、説明文、宿泊料金、定員、郵便番号、住所、電話番号などの情報を保持するエンティティクラス。

// JPA（データベースとJavaオブジェクトのマッピング用）のクラスをインポート
import jakarta.persistence.Column; // フィールドとデータベースのカラムをマッピングするためのアノテーション
import jakarta.persistence.Entity; // このクラスがDBテーブルに対応するエンティティであることを示すアノテーション
import jakarta.persistence.GeneratedValue; // 主キーの自動生成戦略を指定するためのアノテーション
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
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 主キーの値をDBのAuto Increment（自動連番）機能で生成
    @Column(name = "id") // 対応するカラム名を "id" に指定
    private Integer id;

    @Column(name = "name") // 民宿名を保持するカラム "name" とマッピング
    private String name;

    @Column(name = "image_name") // 画像ファイル名を保持するカラム "image_name" とマッピング
    private String imageName;

    @Column(name = "description") // 民宿の説明文を保持するカラム "description" とマッピング
    private String description;

    @Column(name = "price") // 1泊あたりの宿泊料金を保持するカラム "price" とマッピング
    private Integer price;

    @Column(name = "capacity") // 宿泊可能人数（定員）を保持するカラム "capacity" とマッピング
    private Integer capacity;

    @Column(name = "postal_code") // 郵便番号を保持するカラム "postal_code" とマッピング
    private String postalCode;

    @Column(name = "address") // 住所を保持するカラム "address" とマッピング
    private String address;

    @Column(name = "phone_number") // 電話番号を保持するカラム "phone_number" とマッピング
    private String phoneNumber;
}