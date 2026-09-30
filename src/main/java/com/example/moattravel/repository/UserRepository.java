package com.example.moattravel.repository; //データベースのusersテーブルに対するデータ操作（CRUD操作、メールアドレスによるユーザー検索、氏名やフリガナの部分一致検索、ページネーション対応）を行うリポジトリインターフェース。

// Spring Data JPA（ページネーション・リポジトリ）のクラスをインポート
import org.springframework.data.domain.Page; // ページネーション結果（データ一覧や総ページ数など）を保持するクラス
import org.springframework.data.domain.Pageable; // ページ番号や1ページあたりの表示件数、ソート順を指示するためのインターフェース
import org.springframework.data.jpa.repository.JpaRepository; // 基本的なCRUD操作やページング機能を提供するSpring Data JPAのベースインターフェース

// 操作対象のUser（ユーザー・会員情報）エンティティをインポート
import com.example.moattravel.entity.User; // ユーザー情報を保持するエンティティクラス

public interface UserRepository extends JpaRepository<User, Integer> { // Userエンティティ（主キーInteger）に対するDB操作インターフェース

    // メールアドレス（ログインID）に一致するユーザー情報を取得するメソッド（認証・重複検証用）
    public User findByEmail(String email);

    // 氏名またはフリガナキーワードでの部分一致検索（管理者用のユーザー一覧画面＋ページネーション対応）
    public Page<User> findByNameLikeOrFuriganaLike(String nameKeyword, String furiganaKeyword, Pageable pageable);
}