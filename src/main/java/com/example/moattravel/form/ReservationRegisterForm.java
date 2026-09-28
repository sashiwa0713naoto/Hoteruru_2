package com.example.moattravel.form; // 1. このクラスが属するパッケージ（フォルダ構成）を指定

// Lombok（Getter/Setter・全フィールドコンストラクタ自動生成用）のアノテーションをインポート
import lombok.AllArgsConstructor;
import lombok.Data;

@Data // 2. Lombokにより、全フィールドのGetter/Setter、toString、equals、hashCode等を自動生成
@AllArgsConstructor // 3. 全フィールドを引数に受けるコンストラクタを自動生成
public class ReservationRegisterForm {

    private Integer houseId; // 4. 予約対象の民宿ID（どの民宿を予約するか）

    private Integer userId; // 5. 予約を行うユーザーID（誰が予約するか）

    private String checkinDate; // 6. チェックイン日（YYYY-MM-DD形式の文字列）

    private String checkoutDate; // 7. チェックアウト日（YYYY-MM-DD形式の文字列）

    private Integer numberOfPeople; // 8. 宿泊人数

    private Integer amount; // 9. 算出された合計宿泊料金（支払金額）
}