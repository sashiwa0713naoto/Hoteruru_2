package com.example.moattravel.entity; // 1. このクラスが属するパッケージ（フォルダ構成）を指定

// 日時情報を扱うJava標準クラスをインポート
import java.sql.Timestamp;

// JPA（データベースとJavaオブジェクトのマッピング用）のクラスをインポート
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

// Lombok（Getter/Setter等の自動生成用）のクラスをインポート
import lombok.Data;

@Entity // 2. Spring/JPAに対して「このクラスはDBテーブルとマッピングされるエンティティです」と指示
@Table(name = "verification_tokens") // 3. 対応するデータベースのテーブル名を "verification_tokens" に明示的に指定
@Data // 4. Lombokにより、全フィールドのGetter/Setter、toString、equals、hashCode等を自動生成
public class VerificationToken {

    @Id // 5. このフィールドがテーブルの主キー（Primary Key）であることを指定
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 6. 主キーの値をDBのAuto Increment（自動連番）機能で生成
    @Column(name = "id") // 7. 対応するカラム名を "id" に指定
    private Integer id;

    @OneToOne // 8. 1対1のリレーション（1つの認証トークンが1人のユーザーに紐づく）を定義
    @JoinColumn(name = "user_id") // 9. 外部キーカラム名 "user_id" を指定して User エンティティと結合
    private User user;

    @Column(name = "token") // 10. メール認証用のランダムなトークン文字列（UUIDなど）を保持するカラム "token" とマッピング
    private String token;

    // 11. 作成日時。DB側の初期設定（DEFAULT CURRENT_TIMESTAMP）に任せるため、insert/update対象外にする
    @Column(name = "created_at", insertable = false, updatable = false)
    private Timestamp createdAt;

    // 12. 更新日時。DB側の自動更新機能に任せるため、insert/update対象外にする
    @Column(name = "updated_at", insertable = false, updatable = false)
    private Timestamp updatedAt;
}