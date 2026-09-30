package com.example.moattravel.form; //新規民宿の登録画面から送信された入力データを受け取り、Jakarta Validationによるバリデーションチェックを行うためのフォームクラス。

// Jakarta Validation（入力値チェック・バリデーション用）のアノテーションをインポート
import jakarta.validation.constraints.Min; // 数値が指定された最小値以上であることを検証するためのアノテーション
import jakarta.validation.constraints.NotBlank; // 文字列がnullや空文字、空白のみでないことを検証するためのアノテーション
import jakarta.validation.constraints.NotNull; // 値がnullでないことを検証するためのアノテーション

// ファイルアップロード用のクラスをインポート
import org.springframework.web.multipart.MultipartFile; // アップロードされたファイルデータを扱うためのクラス

// Lombok（Getter/Setter自動生成用）のアノテーションをインポート
import lombok.Data; // ゲッター、セッター、toString、equals、hashCodeなどを自動生成するアノテーション

@Data // Lombokにより、全フィールドのGetter/Setter、toString、equals、hashCode等を自動生成
public class HouseRegisterForm {

    @NotBlank(message = "民宿名を入力してください。") // 必須入力・空文字不可（NULL、空文字、スペースのみを禁止）
    private String name;

    private MultipartFile imageFile; // 送信された画像ファイルを受け取るフィールド（新規登録時は未選択も許可）

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