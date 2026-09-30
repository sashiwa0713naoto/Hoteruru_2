package com.example.moattravel.form; //民宿情報の編集画面から送信された入力データを受け取り、Jakarta Validationによるバリデーションチェックを行うためのフォームクラス。

// Jakarta Validation（入力値チェック・バリデーション用）のアノテーションをインポート
import jakarta.validation.constraints.Min; // 数値が指定された最小値以上であることを検証するためのアノテーション
import jakarta.validation.constraints.NotBlank; // 文字列がnullや空文字、空白のみでないことを検証するためのアノテーション
import jakarta.validation.constraints.NotNull; // 値がnullでないことを検証するためのアノテーション

// ファイルアップロード用のクラスをインポート
import org.springframework.web.multipart.MultipartFile; // アップロードされたファイルデータを扱うためのクラス

// Lombok（Getter/Setter・コンストラクタ自動生成用）のアノテーションをインポート
import lombok.AllArgsConstructor; // 全フィールドを引数に持つコンストラクタを自動生成するアノテーション
import lombok.Data; // ゲッター、セッター、toString、equals、hashCodeなどを自動生成するアノテーション
import lombok.NoArgsConstructor; // 引数なしのデフォルトコンストラクタを自動生成するアノテーション

@Data // Lombokにより、全フィールドのGetter/Setter、toString、equals、hashCode等を自動生成
@AllArgsConstructor // 全フィールドを引数に受けるコンストラクタを自動生成
@NoArgsConstructor // 引数なしのデフォルトコンストラクタを自動生成
public class HouseEditForm {

    @NotNull // 更新対象の民宿ID（更新処理のキーとなるため必須）
    private Integer id;

    @NotBlank(message = "民宿名を入力してください。") // 必須入力・空文字不可（NULL、空文字、スペースのみを禁止）
    private String name;

    private String imageName; // 既存の画像ファイル名を保持するフィールド（変更しない場合に使用）

    private MultipartFile imageFile; // 送信された新しい画像ファイルを受け取るフィールド（未選択も許可）

    @NotBlank(message = "説明を入力してください。") // 必須入力（説明文）
    private String description;

    @NotNull(message = "宿泊料金を入力してください。") // 必須入力（NULL不可）
    @Min(value = 1, message = "宿泊料金は1円以上に設定してください。") // 1以上の整数値を要求
    private Integer price;

    @NotNull(message = "定員を入力してください。") // 必須入力（NULL不可）
    @Min(value = 1, message = "定員は1人以上に設定してください。") // 1以上の整数値を要求
    private Integer capacity;

    @NotBlank(message = "郵便番号を入力してください。") // 必須入力（郵便番号）
    private String postalCode;

    @NotBlank(message = "住所を入力してください。") // 必須入力（住所）
    private String address;

    @NotBlank(message = "電話番号を入力してください。") // 必須入力（電話番号）
    private String phoneNumber;
}