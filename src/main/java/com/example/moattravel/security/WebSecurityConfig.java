package com.example.moattravel.security; //サイト全体のセキュリティ（ログイン、アクセス権限、パスワードの暗号化など）をまとめて設定するためのクラス。

import org.springframework.context.annotation.Bean; // メソッドの戻り値をSpringの管理下（部品）に登録するためのアノテーション
import org.springframework.context.annotation.Configuration; // Springに「設定クラス」であることを伝えるためのアノテーション
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity; // メソッドごとの細かいアクセス制限（権限チェック）を有効にするアノテーション
import org.springframework.security.config.annotation.web.builders.HttpSecurity; // Webページのセキュリティルールを組み立てるためのクラス
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity; // Webセキュリティ機能全体を有効にするためのアノテーション
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; // パスワードを安全に暗号化するための仕組みを提供するクラス
import org.springframework.security.crypto.password.PasswordEncoder; // パスワード暗号化の共通ルール（インターフェース）
import org.springframework.security.web.SecurityFilterChain; // 画面ごとのアクセス許可やログイン画面の動きをまとめる仕組み

@Configuration // Springに「設定クラス」として認識させるためのアノテーション
@EnableWebSecurity // Webサイト全体のセキュリティ（ログインやアクセス制限）を有効にする
@EnableMethodSecurity // メソッドごとの細かい権限チェックを有効にする
public class WebSecurityConfig { // サイト全体のセキュリティルールをまとめて設定するクラス

    @Bean // この設定内容をSpringに登録して反映させる
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception { // どのページに誰がアクセスできるか、ログイン・ログアウトの動きを設定するメソッド
        http
            .authorizeHttpRequests((requests) -> requests
                .requestMatchers("/css/**", "/images/**", "/js/**", "/storage/**", "/", "/signup/**", "/houses", "/houses/{id}", "/stripe/webhook").permitAll() // 誰でも自由にアクセスできるページ
                .requestMatchers("/admin/**").hasRole("ADMIN") // 管理者だけにアクセスを許可するページ
                .anyRequest().authenticated() // 上記以外のすべてのページは、ログインが必須
            )
            .formLogin((form) -> form
                .loginPage("/login") // ユーザーが自分で用意したログイン画面のURL
                .loginProcessingUrl("/login") // ログインフォームを送信したときに認証処理を行うURL
                .defaultSuccessUrl("/?loggedIn") // ログインに成功したあとに移動するページ
                .failureUrl("/login?error") // ログインに失敗したあとに移動するページ
                .permitAll() // ログイン画面や送信先へは誰でもアクセスできるようにする
            )
            .logout((logout) -> logout
                .logoutSuccessUrl("/?loggedOut") // ログアウトしたあとに移動するページ
                .permitAll() // ログアウトは誰でも実行できるようにする
            );

        return http.build(); // ここまでで組み立てたセキュリティ設定を完了させて適用する
    }

    @Bean // パスワード暗号化の仕組みをSpringに登録する
    public PasswordEncoder passwordEncoder() { // パスワードを安全に暗号化（ハッシュ化）するための仕組みを作るメソッド
        return new BCryptPasswordEncoder(); // パスワードの暗号化方式として「BCrypt」という安全な方式を指定する
    }
}