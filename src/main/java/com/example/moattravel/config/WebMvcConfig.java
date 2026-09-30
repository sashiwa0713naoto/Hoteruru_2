package com.example.moattravel.config; // アプリケーションの各種設定クラスをまとめるパッケージ

import org.springframework.context.annotation.Configuration; // Springに「設定クラス」であることを伝えるための機能（@Configuration）を読み込む
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry; // 静的リソースの公開場所を設定するためのクラス（ResourceHandlerRegistry）を読み込む
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer; // Spring MVCの設定をカスタマイズするためのインターフェース（WebMvcConfigurer）を読み込む

@Configuration // Spring Bootに対して「このクラスはアプリケーションの設定用クラスです」と指示するアノテーション
public class WebMvcConfig implements WebMvcConfigurer { // Spring MVCのデフォルト設定をカスタマイズするためのインターフェースを実装するクラス

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) { // 静的リソースや外部フォルダの公開場所を追加・変更するためのメソッド
        // アップロードした画像などのファイルを、ブラウザからURL経由で見られるようにするための設定
        registry.addResourceHandler("/storage/**") // 「/storage/」で始まるURLへのアクセスを検知する設定
                .addResourceLocations("file:storage/"); // アクセスがあった際に、保存先であるローカルの「storage/」フォルダからファイルを読み込んで表示・提供する設定
    }
}