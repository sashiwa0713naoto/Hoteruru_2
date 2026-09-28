package com.example.moattravel.entity; // 1. このクラスが属するパッケージ（フォルダ構成）を指定

// JPA（データベースとJavaオブジェクトのマッピング用）のクラスをインポート
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

// Lombok（Getter/Setter等のボイラープレートコード自動生成用）のクラスをインポート
import lombok.Data;

@Entity // 2. Spring/JPAに対して「このクラスはDBテーブルとマッピングされるエンティティです」と指示
@Table(name = "houses") // 3. 対応するデータベースのテーブル名を "houses" に明示的に指定
@Data // 4. Lombokにより、全フィールドのGetter/Setter、toString、equals、hashCode等を自動生成
public class House {

    @Id // 5. このフィールドがテーブルの主キー（Primary Key）であることを指定
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 6. 主キーの値をDBのAuto Increment（自動連番）機能で生成
    @Column(name = "id") // 7. 対応するカラム名を "id" に指定
    private Integer id;

    @Column(name = "name") // 8. 民宿名を保持するカラム "name" とマッピング
    private String name;

    @Column(name = "image_name") // 9. 画像ファイル名を保持するカラム "image_name" とマッピング
    private String imageName;

    @Column(name = "description") // 10. 民宿の説明文を保持するカラム "description" とマッピング
    private String description;

    @Column(name = "price") // 11. 1泊あたりの宿泊料金を保持するカラム "price" とマッピング
    private Integer price;

    @Column(name = "capacity") // 12. 宿泊可能人数（定員）を保持するカラム "capacity" とマッピング
    private Integer capacity;

    @Column(name = "postal_code") // 13. 郵便番号を保持するカラム "postal_code" とマッピング
    private String postalCode;

    @Column(name = "address") // 14. 住所を保持するカラム "address" とマッピング
    private String address;

    @Column(name = "phone_number") // 15. 電話番号を保持するカラム "phone_number" とマッピング
    private String phoneNumber;
}