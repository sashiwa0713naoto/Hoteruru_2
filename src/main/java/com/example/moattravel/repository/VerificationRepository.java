package com.example.moattravel.repository; // 1. このインターフェースが属するパッケージ（フォルダ構成）を指定

import org.springframework.data.jpa.repository.JpaRepository; // Spring Data JPAの基本リポジトリをインポート

import com.example.moattravel.entity.User; // 紐づくユーザーエンティティをインポート
import com.example.moattravel.entity.VerificationToken; // 操作対象のVerificationToken（メール認証用トークン）エンティティをインポート

public interface VerificationRepository extends JpaRepository<VerificationToken, Integer> { // 2. VerificationTokenエンティティ（主キーInteger）に対するDB操作インターフェース
    
    // 3. メールリンク内のトークン文字列から対象の検証トークンレコードを取得するメソッド
    public VerificationToken findByToken(String token);
    
    // 4. 特定のユーザー（User）に紐づいている検証トークンを取得するメソッド
    public VerificationToken findByUser(User user);
}