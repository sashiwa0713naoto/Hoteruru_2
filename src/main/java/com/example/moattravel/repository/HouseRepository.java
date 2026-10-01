package com.example.moattravel.repository; // データベースから民宿のデータを検索・取得する機能（リポジトリ）をまとめる場所

import org.springframework.data.domain.Page; // ページごとにデータを分割する機能
import org.springframework.data.domain.Pageable; // ページ番号や表示件数を受け取る機能
import org.springframework.data.jpa.repository.JpaRepository; // 基本的なデータベース操作を自動で行う機能
import org.springframework.data.jpa.repository.Query; // 独自のクエリを記述する機能
import org.springframework.data.repository.query.Param; // クエリの変数に値を安全に渡す機能

import com.example.moattravel.entity.House; // 民宿のデータベース情報を表すクラス

// 民宿データのデータベース操作（検索や取得）をまとめたインターフェース
public interface HouseRepository extends JpaRepository<House, Integer> { // 民宿データを操作する仕組みを定義する
    
    // 民宿名または住所によるキーワード検索を行う
    Page<House> findByNameLikeOrAddressLike(String nameKeyword, String addressKeyword, Pageable pageable);

    // 民宿名・郵便番号・住所・電話番号のすべてを対象にキーワード検索を行う
    Page<House> findByNameLikeOrPostalCodeLikeOrAddressLikeOrPhoneNumberLike(
        String nameKeyword, // 民宿名のキーワードを受け取る
        String postalCodeKeyword, // 郵便番号のキーワードを受け取る
        String addressKeyword, // 住所のキーワードを受け取る
        String phoneNumberKeyword, // 電話番号のキーワードを受け取る
        Pageable pageable // ページ設定を受け取る
    );

    // 住所の部分一致でエリア・都道府県検索を行う
    Page<House> findByAddressLike(String addressKeyword, Pageable pageable);

    // 指定した価格以下の民宿を抽出する
    Page<House> findByPriceLessThanEqual(Integer price, Pageable pageable);

    // 新着順（作成日時の降順）で民宿を取得する
    Page<House> findAllByOrderByCreatedAtDesc(Pageable pageable);

    // エリア（地方）ごとに複数の都道府県を一括で検索するクエリ群

    // 北海道・東北エリアの都道府県を一括検索する
    @Query("SELECT h FROM House h WHERE h.address LIKE :a1 OR h.address LIKE :a2 OR h.address LIKE :a3 OR h.address LIKE :a4 OR h.address LIKE :a5 OR h.address LIKE :a6 OR h.address LIKE :a7")
    Page<House> findByHokkaidoTohoku(@Param("a1") String a1, @Param("a2") String a2, @Param("a3") String a3, @Param("a4") String a4, @Param("a5") String a5, @Param("a6") String a6, @Param("a7") String a7, Pageable pageable);

    // 関東エリアの都道府県を一括検索する
    @Query("SELECT h FROM House h WHERE h.address LIKE :a1 OR h.address LIKE :a2 OR h.address LIKE :a3 OR h.address LIKE :a4 OR h.address LIKE :a5 OR h.address LIKE :a6 OR h.address LIKE :a7")
    Page<House> findByKanto(@Param("a1") String a1, @Param("a2") String a2, @Param("a3") String a3, @Param("a4") String a4, @Param("a5") String a5, @Param("a6") String a6, @Param("a7") String a7, Pageable pageable);

    // 中部エリアの都道府県を一括検索する
    @Query("SELECT h FROM House h WHERE h.address LIKE :a1 OR h.address LIKE :a2 OR h.address LIKE :a3 OR h.address LIKE :a4 OR h.address LIKE :a5 OR h.address LIKE :a6 OR h.address LIKE :a7 OR h.address LIKE :a8 OR h.address LIKE :a9")
    Page<House> findByChubu(@Param("a1") String a1, @Param("a2") String a2, @Param("a3") String a3, @Param("a4") String a4, @Param("a5") String a5, @Param("a6") String a6, @Param("a7") String a7, @Param("a8") String a8, @Param("a9") String a9, Pageable pageable);

    // 近畿エリアの都道府県を一括検索する
    @Query("SELECT h FROM House h WHERE h.address LIKE :a1 OR h.address LIKE :a2 OR h.address LIKE :a3 OR h.address LIKE :a4 OR h.address LIKE :a5 OR h.address LIKE :a6 OR h.address LIKE :a7")
    Page<House> findByKinki(@Param("a1") String a1, @Param("a2") String a2, @Param("a3") String a3, @Param("a4") String a4, @Param("a5") String a5, @Param("a6") String a6, @Param("a7") String a7, Pageable pageable);

    // 中国エリアの都道府県を一括検索する
    @Query("SELECT h FROM House h WHERE h.address LIKE :a1 OR h.address LIKE :a2 OR h.address LIKE :a3 OR h.address LIKE :a4 OR h.address LIKE :a5")
    Page<House> findByChugoku(@Param("a1") String a1, @Param("a2") String a2, @Param("a3") String a3, @Param("a4") String a4, @Param("a5") String a5, Pageable pageable);

    // 四国エリアの都道府県を一括検索する
    @Query("SELECT h FROM House h WHERE h.address LIKE :a1 OR h.address LIKE :a2 OR h.address LIKE :a3 OR h.address LIKE :a4")
    Page<House> findByShikoku(@Param("a1") String a1, @Param("a2") String a2, @Param("a3") String a3, @Param("a4") String a4, Pageable pageable);

    // 九州・沖縄エリアの都道府県を一括検索する
    @Query("SELECT h FROM House h WHERE h.address LIKE :a1 OR h.address LIKE :a2 OR h.address LIKE :a3 OR h.address LIKE :a4 OR h.address LIKE :a5 OR h.address LIKE :a6 OR h.address LIKE :a7 OR h.address LIKE :a8")
    Page<House> findByKyushuOkinawa(@Param("a1") String a1, @Param("a2") String a2, @Param("a3") String a3, @Param("a4") String a4, @Param("a5") String a5, @Param("a6") String a6, @Param("a7") String a7, @Param("a8") String a8, Pageable pageable);

    // キーワード検索とエリア絞り込みを同時に行う複合検索メソッド
    Page<House> findByNameLikeOrPostalCodeLikeOrAddressLikeOrPhoneNumberLikeAndAddressLike(
            String nameKeyword, // 民宿名キーワードを受け取る
            String postalCodeKeyword, // 郵便番号キーワードを受け取る
            String addressKeyword, // 住所キーワードを受け取る
            String phoneNumberKeyword, // 電話番号キーワードを受け取る
            String areaAddress, // エリアの絞り込みキーワードを受け取る
            Pageable pageable // ページ設定を受け取る
    );
}