package com.example.moattravel.entity; //データベースのrolesテーブルと1対1で対応し、ロールIDや役割名（ロール名）などの権限情報を保持するエンティティクラス。

// JPA（データベースとJavaオブジェクトのマッピング用）のクラスをインポート
import jakarta.persistence.Column; // フィールドとデータベースのカラムをマッピングするためのアノテーション
import jakarta.persistence.Entity; // このクラスがDBテーブルに対応するエンティティであることを示すアノテーション
import jakarta.persistence.GeneratedValue; // 主キーの自動生成戦略を指定するためのアノテーション
import jakarta.persistence.GenerationType; // 主キーの自動生成の種類（Auto Increment等）を指定するための列挙型
import jakarta.persistence.Id; // このフィールドがテーブルの主キーであることを示すアノテーション
import jakarta.persistence.Table; // 対応するデータベースのテーブル名を指定するためのアノテーション

// Lombok（Getter/Setter等の自動生成用）のクラスをインポート
import lombok.Data; // ゲッター、セッター、toString、equals、hashCodeなどを自動生成するアノテーション

@Entity // Spring/JPAに対して「このクラスはDBテーブルとマッピングされるエンティティです」と指示
@Table(name = "roles") // 対応するデータベースのテーブル名を "roles" に明示的に指定
@Data // Lombokにより、全フィールドのGetter/Setter、toString、equals、hashCode等を自動生成
public class Role {

    @Id // このフィールドがテーブルの主キー（Primary Key）であることを指定
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 主キーの値をDBのAuto Increment（自動連番）機能で生成
    @Column(name = "id") // 対応するカラム名を "id" に指定
    private Integer id;

    @Column(name = "name") // 役割名（ロール名）を保持するカラム "name" とマッピング
    private String name;
}