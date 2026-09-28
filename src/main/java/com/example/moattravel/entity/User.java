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
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

// Lombok（Getter/Setter等の自動生成用）のクラスをインポート
import lombok.Data;

@Entity // 2. Spring/JPAに対して「このクラスはDBテーブルとマッピングされるエンティティです」と指示
@Table(name = "users") // 3. 対応するデータベースのテーブル名を "users" に明示的に指定
@Data // 4. Lombokにより、全フィールドのGetter/Setter、toString、equals、hashCode等を自動生成
public class User {

    @Id // 5. このフィールドがテーブルの主キー（Primary Key）であることを指定
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 6. 主キーの値をDBのAuto Increment（自動連番）機能で生成
    @Column(name = "id") // 7. 対応するカラム名を "id" に指定
    private Integer id;

    @Column(name = "name") // 8. 氏名を保持するカラム "name" とマッピング
    private String name;

    @Column(name = "furigana") // 9. フリガナを保持するカラム "furigana" とマッピング
    private String furigana;

    @Column(name = "postal_code") // 10. 郵便番号を保持するカラム "postal_code" とマッピング
    private String postalCode;

    @Column(name = "address") // 11. 住所を保持するカラム "address" とマッピング
    private String address;

    @Column(name = "phone_number") // 12. 電話番号を保持するカラム "phone_number" とマッピング
    private String phoneNumber;

    @Column(name = "email") // 13. メールアドレス（ログインID）を保持するカラム "email" とマッピング
    private String email;

    @Column(name = "password") // 14. 暗号化されたパスワードを保持するカラム "password" とマッピング
    private String password;

    @ManyToOne // 15. 多対1のリレーション（複数のユーザーが1つのロールを持つ）を定義
    @JoinColumn(name = "role_id") // 16. 外部キーカラム名 "role_id" を指定して Role エンティティと結合
    private Role role;

    @Column(name = "enabled") // 17. アカウントの有効/無効状態（メール認証完了フラグ）を保持するカラム "enabled" とマッピング
    private Boolean enabled;

    // 18. 作成日時。DB側の初期設定（DEFAULT CURRENT_TIMESTAMP）に任せるため、insert/update対象外にする
    @Column(name = "created_at", insertable = false, updatable = false)
    private Timestamp createdAt;

    // 19. 更新日時。DB側の自動更新機能に任せるため、insert/update対象外にする
    @Column(name = "updated_at", insertable = false, updatable = false)
    private Timestamp updatedAt;
}