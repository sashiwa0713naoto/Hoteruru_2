package com.example.moattravel.form; // 1. このクラスが属するパッケージ（フォルダ構成）を指定

// Jakarta Validation（入力値チェック・バリデーション用）のアノテーションをインポート
import jakarta.validation.constraints.NotBlank;

// Hibernate Validator（文字列長チェック用）のアノテーションをインポート
import org.hibernate.validator.constraints.Length;

// Lombok（Getter/Setter自動生成用）のアノテーションをインポート
import lombok.Data;

@Data // 2. Lombokにより、全フィールドのGetter/Setter、toString、equals、hashCode等を自動生成
public class SignupForm {

    @NotBlank(message = "氏名を入力してください。") // 3. 必須入力・空文字不可（NULL、空文字、スペースのみを禁止）
    private String name;

    @NotBlank(message = "フリガナを入力してください。") // 4. 必須入力（フリガナ）
    private String furigana;

    @NotBlank(message = "郵便番号を入力してください。") // 5. 必須入力（郵便番号）
    private String postalCode;

    @NotBlank(message = "住所を入力してください。") // 6. 必須入力（住所）
    private String address;

    @NotBlank(message = "電話番号を入力してください。") // 7. 必須入力（電話番号）
    private String phoneNumber;

    @NotBlank(message = "メールアドレスを入力してください。") // 8. 必須入力（メールアドレス）
    private String email;

    @NotBlank(message = "パスワードを入力してください。") // 9. 必須入力（パスワード）
    @Length(min = 8, message = "パスワードは8文字以上で入力してください。") // 10. 最小文字数チェック（8文字以上）
    private String password;

    @NotBlank(message = "確認用パスワードを入力してください。") // 11. 必須入力（確認用パスワード）
    private String passwordConfirmation;

    private Integer roleId; // 12. ユーザー権限（ロール）のID（通常は会員登録時にデフォルト値がセットされる）
}