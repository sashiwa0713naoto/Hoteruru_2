package com.example.moattravel.repository; //データベースのrolesテーブルに対するデータ操作（CRUD操作、ロール名による検索など）を行うリポジトリインターフェース。

import org.springframework.data.jpa.repository.JpaRepository; // Spring Data JPAの基本リポジトリをインポート

import com.example.moattravel.entity.Role; // 操作対象のRole（権限・ロール）エンティティをインポート

public interface RoleRepository extends JpaRepository<Role, Integer> { // Roleエンティティ（主キーInteger）に対するDB操作インターフェース
    
    // ロール名（"ROLE_GENERAL", "ROLE_ADMIN" など）をキーにしてロール情報を取得するメソッド
    public Role findByName(String name);
}