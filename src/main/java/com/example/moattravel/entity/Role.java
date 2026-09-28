package com.example.moattravel.entity; // 1. このクラスが属するパッケージ（フォルダ構成）を指定

// JPA（データベースとJavaオブジェクトのマッピング用）のクラスをインポート
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

// Lombok（Getter/Setter等の自動生成用）のクラスをインポート
import lombok.Data;

@Entity // 2. Spring/JPAに対して「このクラスはDBテーブルとマッピングされるエンティティです」と指示
@Table(name = "roles") // 3. 対応するデータベースのテーブル名を "roles" に明示的に指定
@Data // 4. Lombokにより、全フィールドのGetter/Setter、toString、equals、hashCode等を自動生成
public class Role {

    @Id // 5. このフィールドがテーブルの主キー（Primary Key）であることを指定
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 6. 主キーの値をDBのAuto Increment（自動連番）機能で生成
    @Column(name = "id") // 7. 対応するカラム名を "id" に指定
    private Integer id;

    @Column(name = "name") // 8. 役割名（ロール名）を保持するカラム "name" とマッピング
    private String name;
}