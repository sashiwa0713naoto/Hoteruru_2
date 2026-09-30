package com.example.moattravel.repository; //データベースのverification_tokensテーブルに対するデータ操作（CRUD操作、トークン文字列やユーザーによるメール認証用トークンの検索）を行うリポジトリインターフェース。

import org.springframework.data.jpa.repository.JpaRepository; // Spring Data JPAの基本リポジトリをインポート

import com.example.moattravel.entity.User; // 紐づくユーザーエンティティをインポート
import com.example.moattravel.entity.VerificationToken; // 操作対象のVerificationToken（メール認証用トークン）エンティティをインポート

public interface VerificationRepository extends JpaRepository<VerificationToken, Integer> { // VerificationTokenエンティティ（主キーInteger）に対するDB操作インターフェース
    
    // メールリンク内のトークン文字列から対象の検証トークンレコードを取得するメソッド
    public VerificationToken findByToken(String token);
    
    // 特定のユーザー（User）に紐づいている検証トークンを取得するメソッド
    public VerificationToken findByUser(User user);
}