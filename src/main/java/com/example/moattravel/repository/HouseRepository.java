package com.example.moattravel.repository; // 1. このインターフェースが属するパッケージ（フォルダ構成）を指定

// Spring Data JPA（ページネーション・JPQLクエリ・リポジトリ）のクラスをインポート
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.moattravel.entity.House;

public interface HouseRepository extends JpaRepository<House, Integer> { // 2. Houseエンティティ（主キーInteger）に対するDB操作インターフェース
    
    // 3. キーワード検索（民宿名・郵便番号・住所・電話番号のいずれかにマッチ＋ページネーション対応）
    Page<House> findByNameLikeOrPostalCodeLikeOrAddressLikeOrPhoneNumberLike(
        String nameKeyword, 
        String postalCodeKeyword, 
        String addressKeyword, 
        String phoneNumberKeyword, 
        Pageable pageable
    );

    // 4. 単一のエリア・都道府県検索（住所の部分一致＋ページネーション対応）
    Page<House> findByAddressLike(String addressKeyword, Pageable pageable);

    // ▼ エリア（地方）ごとの複数都道府県を一括検索するカスタムJPQLクエリ

    // 5. 北海道・東北地方（7道県：北海道・青森・岩手・宮城・秋田・山形・福島）の検索
    @Query("SELECT h FROM House h WHERE h.address LIKE :a1 OR h.address LIKE :a2 OR h.address LIKE :a3 OR h.address LIKE :a4 OR h.address LIKE :a5 OR h.address LIKE :a6 OR h.address LIKE :a7")
    Page<House> findByHokkaidoTohoku(@Param("a1") String a1, @Param("a2") String a2, @Param("a3") String a3, @Param("a4") String a4, @Param("a5") String a5, @Param("a6") String a6, @Param("a7") String a7, Pageable pageable);

    // 6. 関東地方（7都県：茨城・栃木・群馬・埼玉・千葉・東京・神奈川）の検索
    @Query("SELECT h FROM House h WHERE h.address LIKE :a1 OR h.address LIKE :a2 OR h.address LIKE :a3 OR h.address LIKE :a4 OR h.address LIKE :a5 OR h.address LIKE :a6 OR h.address LIKE :a7")
    Page<House> findByKanto(@Param("a1") String a1, @Param("a2") String a2, @Param("a3") String a3, @Param("a4") String a4, @Param("a5") String a5, @Param("a6") String a6, @Param("a7") String a7, Pageable pageable);

    // 7. 中部地方（9県：新潟・富山・石川・福井・山梨・長野・岐阜・静岡・愛知）の検索
    @Query("SELECT h FROM House h WHERE h.address LIKE :a1 OR h.address LIKE :a2 OR h.address LIKE :a3 OR h.address LIKE :a4 OR h.address LIKE :a5 OR h.address LIKE :a6 OR h.address LIKE :a7 OR h.address LIKE :a8 OR h.address LIKE :a9")
    Page<House> findByChubu(@Param("a1") String a1, @Param("a2") String a2, @Param("a3") String a3, @Param("a4") String a4, @Param("a5") String a5, @Param("a6") String a6, @Param("a7") String a7, @Param("a8") String a8, @Param("a9") String a9, Pageable pageable);

    // 8. 近畿地方（7府県：三重・滋賀・京都・大阪・兵庫・奈良・和歌山）の検索
    @Query("SELECT h FROM House h WHERE h.address LIKE :a1 OR h.address LIKE :a2 OR h.address LIKE :a3 OR h.address LIKE :a4 OR h.address LIKE :a5 OR h.address LIKE :a6 OR h.address LIKE :a7")
    Page<House> findByKinki(@Param("a1") String a1, @Param("a2") String a2, @Param("a3") String a3, @Param("a4") String a4, @Param("a5") String a5, @Param("a6") String a6, @Param("a7") String a7, Pageable pageable);

    // 9. 中国地方（5県：鳥取・島根・岡山・広島・山口）の検索
    @Query("SELECT h FROM House h WHERE h.address LIKE :a1 OR h.address LIKE :a2 OR h.address LIKE :a3 OR h.address LIKE :a4 OR h.address LIKE :a5")
    Page<House> findByChugoku(@Param("a1") String a1, @Param("a2") String a2, @Param("a3") String a3, @Param("a4") String a4, @Param("a5") String a5, Pageable pageable);

    // 10. 四国地方（4県：徳島・香川・愛媛・高知）の検索
    @Query("SELECT h FROM House h WHERE h.address LIKE :a1 OR h.address LIKE :a2 OR h.address LIKE :a3 OR h.address LIKE :a4")
    Page<House> findByShikoku(@Param("a1") String a1, @Param("a2") String a2, @Param("a3") String a3, @Param("a4") String a4, Pageable pageable);

    // 11. 九州・沖縄地方（8県：福岡・佐賀・長崎・熊本・大分・宮崎・鹿児島・沖縄）の検索
    @Query("SELECT h FROM House h WHERE h.address LIKE :a1 OR h.address LIKE :a2 OR h.address LIKE :a3 OR h.address LIKE :a4 OR h.address LIKE :a5 OR h.address LIKE :a6 OR h.address LIKE :a7 OR h.address LIKE :a8")
    Page<House> findByKyushuOkinawa(@Param("a1") String a1, @Param("a2") String a2, @Param("a3") String a3, @Param("a4") String a4, @Param("a5") String a5, @Param("a6") String a6, @Param("a7") String a7, @Param("a8") String a8, Pageable pageable);

    // 12. フリーキーワード ＆ エリア（住所）の組み合わせ検索
    Page<House> findByNameLikeOrPostalCodeLikeOrAddressLikeOrPhoneNumberLikeAndAddressLike(
        String n, String p, String a, String ph, String addressKeyword, Pageable pageable
    );
}