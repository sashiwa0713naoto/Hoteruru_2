package com.example.moattravel.security; // 1. このクラスが属するパッケージ（セキュリティ関連）を指定

import java.util.Collection; // 権限リストなどを保持するためのCollectionインターフェースをインポート

// Spring Securityが提供する認証ユーザー用インターフェース類をインポート
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

// 操作対象のUserエンティティをインポート
import com.example.moattravel.entity.User;

public class UserDetailsImpl implements UserDetails { // 2. Spring Securityの認証ユーザー情報として機能させるクラス
    private final User user; // アプリ独自のユーザーエンティティ
    private final Collection<GrantedAuthority> authorities; // ユーザーに付与された権限（ROLE_GENERAL, ROLE_ADMINなど）のリスト

    // 3. コンストラクタ（Userエンティティと権限リストを受け取り保持する）
    public UserDetailsImpl(User user, Collection<GrantedAuthority> authorities) {
        this.user = user;
        this.authorities = authorities;
    }

    // 4. 独自のゲッター（コントローラーやThymeleaf画面から元のUserエンティティを取得できるようにする）
    public User getUser() {
        return user;
    }

    // 5. ユーザーが持っている権限コレクションを返す（Spring Securityがアクセス制御に利用）
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    // 6. ユーザーの暗号化済みパスワードを返す
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    // 7. ログインIDとして使用する識別子（今回はメールアドレス）を返す
    @Override
    public String getUsername() {
        return user.getEmail();
    }

    // 8. アカウントの有効期限が切れていないかを返す（true = 期限切れでない）
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // 9. アカウントがロックされていないかを返す（true = ロックされていない）
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    // 10. パスワードなどの資格情報の有効期限が切れていないかを返す（true = 期限切れでない）
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    // 11. ユーザーアカウント自体が有効化されているかを返す（メール認証完了状態などのフラグと連動）
    @Override
    public boolean isEnabled() {
        return user.getEnabled();
    }
}