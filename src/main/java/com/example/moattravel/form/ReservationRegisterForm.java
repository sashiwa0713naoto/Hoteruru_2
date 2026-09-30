package com.example.moattravel.form; //予約確認画面から決済処理やデータベースへの登録処理へ受け渡すために、民宿ID、ユーザーID、チェックイン日、チェックアウト日、宿泊人数、合計料金などの予約データを保持するためのフォームクラス。

// Lombok（Getter/Setter・全フィールドコンストラクタ自動生成用）のアノテーションをインポート
import lombok.AllArgsConstructor; // 全フィールドを引数に持つコンストラクタを自動生成するアノテーション
import lombok.Data; // ゲッター、セッター、toString、equals、hashCodeなどを自動生成するアノテーション

@Data // Lombokにより、全フィールドのGetter/Setter、toString、equals、hashCode等を自動生成
@AllArgsConstructor // 全フィールドを引数に受けるコンストラクタを自動生成
public class ReservationRegisterForm {

    private Integer houseId; // 予約対象の民宿ID（どの民宿を予約するか）

    private Integer userId; // 予約を行うユーザーID（誰が予約するか）

    private String checkinDate; // チェックイン日（YYYY-MM-DD形式の文字列）

    private String checkoutDate; // チェックアウト日（YYYY-MM-DD形式の文字列）

    private Integer numberOfPeople; // 宿泊人数

    private Integer amount; // 算出された合計宿泊料金（支払金額）
}