package com.example.moattravel.form; //民宿詳細画面などから送信された宿泊予約の入力データ（チェックイン日、チェックアウト日、宿泊人数）を受け取り、Jakarta Validationによるバリデーションチェックを行うためのフォームクラス。

// 日付（時間情報なし）を扱うJava標準クラスをインポート
import java.time.LocalDate; // 年月日のみを扱う日付クラス

// Jakarta Validation（入力値チェック・バリデーション用）のアノテーションをインポート
import jakarta.validation.constraints.Min; // 数値が指定された最小値以上であることを検証するためのアノテーション
import jakarta.validation.constraints.NotNull; // 値がnullでないことを検証するためのアノテーション

// Lombok（Getter/Setter自動生成用）のアノテーションをインポート
import lombok.Data; // ゲッター、セッター、toString、equals、hashCodeなどを自動生成するアノテーション

@Data // Lombokにより、全フィールドのGetter/Setter、toString、equals、hashCode等を自動生成
public class ReservationInputForm {

    @NotNull(message = "チェックイン日を入力してください。") // 必須入力（日付が未選択・NULLであることを禁止）
    private LocalDate checkinDate;

    @NotNull(message = "チェックアウト日を入力してください。") // 必須入力（日付が未選択・NULLであることを禁止）
    private LocalDate checkoutDate;

    @NotNull(message = "宿泊人数を入力してください。") // 必須入力（NULL不可）
    @Min(value = 1, message = "宿泊人数は1人以上に設定してください。") // 1人以上の整数値を要求
    private Integer numberOfPeople;
}