package com.example.moattravel.security; // 1. このクラスが属するパッケージ（セキュリティ関連）を指定

import java.util.ArrayList;
import java.util.Collection;

// Spring Securityが提供する権限管理・ユーザー検索用のクラスをインポート
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.moattravel.entity.User;
import com.example.moattravel.repository.UserRepository;

@Service // 2. Springのコンポーネントスキャン対象（サービス層のBean）として登録
public class UserDetailsServiceImple implements UserDetailsService { // 3. Spring Securityのユーザー検索インターフェースを実装

    private final UserRepository userRepository; // DI（依存性注入）用リポジトリ

    // 4. コンストラクタインジェクション（UserRepositoryを受け取る）
    public UserDetailsServiceImple(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // 5. ログイン時に入力されたメールアドレス（＝ユーザー名）を基にユーザー情報を検索・返却する核心メソッド
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        try {
            // DBからメールアドレスに一致するユーザーを取得
            User user = userRepository.findByEmail(email);
            
            // ユーザーに設定されているロール名（例: "ROLE_GENERAL", "ROLE_ADMIN"）を取得
            String userRoleName = user.getRole().getName();
            
            // Spring Securityが解釈できる権限リストオブジェクト（GrantedAuthority）を作成
            Collection<GrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority(userRoleName));
            
            // UserDetailsImplインスタンスにDBのUser情報と権限リストを渡して返却
            return new UserDetailsImpl(user, authorities);
        } catch (Exception e) {
            // ユーザーが見つからない（またはNullエラー等）場合、認証失敗用の例外を発行
            throw new UsernameNotFoundException("ユーザーが見つかりませんでした。");
        }
    }
}