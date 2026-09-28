package com.example.moattravel.form; // 1. このクラスが属するパッケージ（フォルダ構成）を指定

// 日付（時間情報なし）を扱うJava標準クラスをインポート
import java.time.LocalDate;

// Jakarta Validation（入力値チェック・バリデーション用）のアノテーションをインポート
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

// Lombok（Getter/Setter自動生成用）のアノテーションをインポート
import lombok.Data;

@Data // 2. Lombokにより、全フィールドのGetter/Setter、toString、equals、hashCode等を自動生成
public class ReservationInputForm {

    @NotNull(message = "チェックイン日を入力してください。") // 3. 必須入力（日付が未選択・NULLであることを禁止）
    private LocalDate checkinDate;

    @NotNull(message = "チェックアウト日を入力してください。") // 4. 必須入力（日付が未選択・NULLであることを禁止）
    private LocalDate checkoutDate;

    @NotNull(message = "宿泊人数を入力してください。") // 5. 必須入力（NULL不可）
    @Min(value = 1, message = "宿泊人数は1人以上に設定してください。") // 6. 1人以上の整数値を要求
    private Integer numberOfPeople;
}