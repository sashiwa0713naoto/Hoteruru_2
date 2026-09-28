package com.example.moattravel.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration // 1. Spring Bootに「これは設定用のクラスですよ」と教えるアノテーション
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 2. アップロードした画像をブラウザから見られるようにする設定
        registry.addResourceHandler("/storage/**")
                .addResourceLocations("file:storage/"); 
    }
}