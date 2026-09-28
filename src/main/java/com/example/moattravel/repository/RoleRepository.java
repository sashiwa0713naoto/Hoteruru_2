package com.example.moattravel.repository; // 1. このインターフェースが属するパッケージ（フォルダ構成）を指定

import org.springframework.data.jpa.repository.JpaRepository; // Spring Data JPAの基本リポジトリをインポート

import com.example.moattravel.entity.Role; // 操作対象のRole（権限・ロール）エンティティをインポート

public interface RoleRepository extends JpaRepository<Role, Integer> { // 2. Roleエンティティ（主キーInteger）に対するDB操作インターフェース
    
    // 3. ロール名（"ROLE_GENERAL", "ROLE_ADMIN" など）をキーにしてロール情報を取得するメソッド
    public Role findByName(String name);

}