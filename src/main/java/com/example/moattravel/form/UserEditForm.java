package com.example.moattravel.form; //会員情報編集画面から送信された入力データ（ユーザーID、氏名、フリガナ、郵便番号、住所、電話番号、メールアドレス）を受け取り、Jakarta Validationによるバリデーションチェックを行うためのフォームクラス。

// Jakarta Validation（入力値チェック・バリデーション用）のアノテーションをインポート
import jakarta.validation.constraints.NotBlank; // 文字列がnullや空文字、空白のみでないことを検証するためのアノテーション
import jakarta.validation.constraints.NotNull; // 値がnullでないことを検証するためのアノテーション

// Lombok（Getter/Setter・全フィールドコンストラクタ自動生成用）のアノテーションをインポート
import lombok.AllArgsConstructor; // 全フィールドを引数に持つコンストラクタを自動生成するアノテーション
import lombok.Data; // ゲッター、セッター、toString、equals、hashCodeなどを自動生成するアノテーション

@Data // Lombokにより、全フィールドのGetter/Setter、toString、equals、hashCode等を自動生成
@AllArgsConstructor // 全フィールドを引数に受けるコンストラクタを自動生成
public class UserEditForm {

    @NotNull // 更新対象のユーザーID（どのユーザーを更新するか識別するため必須）
    private Integer id;

    @NotBlank(message = "氏名を入力してください。") // 必須入力・空文字不可（NULL、空文字、スペースのみを禁止）
    private String name;

    @NotBlank(message = "フリガナを入力してください。") // 必須入力（フリガナ）
    private String furigana;

    @NotBlank(message = "郵便番号を入力してください。") // 必須入力（郵便番号）
    private String postalCode;

    @NotBlank(message = "住所を入力してください。") // 必須入力（住所）
    private String address;

    @NotBlank(message = "電話番号を入力してください。") // 必須入力（電話番号）
    private String phoneNumber;

    @NotBlank(message = "メールアドレスを入力してください。") // 必須入力（メールアドレス）
    private String email;
}