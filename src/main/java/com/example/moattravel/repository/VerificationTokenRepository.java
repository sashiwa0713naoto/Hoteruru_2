package com.example.moattravel.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.moattravel.entity.User; // ★これをインポートに追加
import com.example.moattravel.entity.VerificationToken;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Integer> {
    public VerificationToken findByToken(String token);
    
    // ★追加：ユーザーに紐づくトークンを検索するメソッド
    public VerificationToken findByUser(User user);
}