package com.example.moattravel.security; //Spring Securityでの認証・認可処理において、ログインユーザーの資格情報（メールアドレス、パスワード）や権限、アカウント状態を管理するためのUserDetails実装クラス。

import java.util.Collection; // 権限リストなどを保持するためのCollectionインターフェースをインポート

// Spring Securityが提供する認証ユーザー用インターフェース類をインポート
import org.springframework.security.core.GrantedAuthority; // ユーザーに付与される権限を表すインターフェース
import org.springframework.security.core.userdetails.UserDetails; // Spring Securityが認証・認可で使用するユーザー情報を表すインターフェース

// 操作対象のUserエンティティをインポート
import com.example.moattravel.entity.User; // ユーザー情報を保持するエンティティクラス

public class UserDetailsImpl implements UserDetails { // Spring Securityの認証ユーザー情報として機能させるクラス
    private final User user; // アプリ独自のユーザーエンティティ
    private final Collection<GrantedAuthority> authorities; // ユーザーに付与された権限（ROLE_GENERAL, ROLE_ADMINなど）のリスト

    // コンストラクタ（Userエンティティと権限リストを受け取り保持する）
    public UserDetailsImpl(User user, Collection<GrantedAuthority> authorities) {
        this.user = user;
        this.authorities = authorities;
    }

    // 独自のゲッター（コントローラーやThymeleaf画面から元のUserエンティティを取得できるようにする）
    public User getUser() {
        return user;
    }

    // ユーザーが持っている権限コレクションを返す（Spring Securityがアクセス制御に利用）
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    // ユーザーの暗号化済みパスワードを返す
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    // ログインIDとして使用する識別子（今回はメールアドレス）を返す
    @Override
    public String getUsername() {
        return user.getEmail();
    }

    // アカウントの有効期限が切れていないかを返す（true = 期限切れでない）
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // アカウントがロックされていないかを返す（true = ロックされていない）
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    // パスワードなどの資格情報の有効期限が切れていないかを返す（true = 期限切れでない）
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    // ユーザーアカウント自体が有効化されているかを返す（メール認証完了状態などのフラグと連動）
    @Override
    public boolean isEnabled() {
        return user.getEnabled();
    }
}