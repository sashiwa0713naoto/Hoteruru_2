package com.example.moattravel.repository; //データベースのreservationsテーブルに対するデータ操作（CRUD操作、予約一覧の作成日時降順取得、特定ユーザーの予約一覧取得、ページネーション対応）を行うリポジトリインターフェース。

// Spring Data JPA（ページネーション・リポジトリ）のクラスをインポート
import org.springframework.data.domain.Page; // ページネーション結果（データ一覧や総ページ数など）を保持するクラス
import org.springframework.data.domain.Pageable; // ページ番号や1ページあたりの表示件数、ソート順を指示するためのインターフェース
import org.springframework.data.jpa.repository.JpaRepository; // 基本的なCRUD操作やページング機能を提供するSpring Data JPAのベースインターフェース

// アプリケーション内のエンティティをインポート
import com.example.moattravel.entity.Reservation; // 予約情報を保持するエンティティクラス
import com.example.moattravel.entity.User; // ユーザー情報を保持するエンティティクラス

public interface ReservationRepository extends JpaRepository<Reservation, Integer> {

    // ページング形式ですべての予約データを取得するクエリメソッド（作成日時の新しい順に並び替え）
    public Page<Reservation> findAllByOrderByCreatedAtDesc(Pageable pageable);

    // 特定のユーザーに紐づく予約データをページング形式で取得するクエリメソッド（作成日時の新しい順に並び替え）
    public Page<Reservation> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);
}