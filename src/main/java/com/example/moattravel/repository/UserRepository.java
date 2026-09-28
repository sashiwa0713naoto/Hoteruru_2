package com.example.moattravel.repository; // 1. このインターフェースが属するパッケージ（フォルダ構成）を指定

// Spring Data JPA（ページネーション・リポジトリ）のクラスをインポート
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

// 操作対象のUser（ユーザー・会員情報）エンティティをインポート
import com.example.moattravel.entity.User;

public interface UserRepository extends JpaRepository<User, Integer> { // 2. Userエンティティ（主キーInteger）に対するDB操作インターフェース

    // 3. メールアドレス（ログインID）に一致するユーザー情報を取得するメソッド（認証・重複検証用）
    public User findByEmail(String email);

    // 4. 氏名またはフリガナキーワードでの部分一致検索（管理者用のユーザー一覧画面＋ページネーション対応）
    public Page<User> findByNameLikeOrFuriganaLike(String nameKeyword, String furiganaKeyword, Pageable pageable);
}