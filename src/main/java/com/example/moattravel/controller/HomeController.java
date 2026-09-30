package com.example.moattravel.controller; // トップページの表示制御（ホーム画面の表示）を行うコントローラークラス。

// Spring MVC関連のクラスをインポート
import org.springframework.stereotype.Controller; // このクラスがSpring MVCのコントローラー（Webリクエストを処理する役割）であることを示すアノテーションを読み込む
import org.springframework.web.bind.annotation.GetMapping; // HTTPのGETメソッド（データの取得・画面表示）のリクエストをマッピングするためのアノテーションを読み込む

@Controller // Spring Bootに「このクラスはWebリクエストを処理するコントローラーです」と認識させる
public class HomeController {

    // トップページ（インデックス画面）表示処理（GET /）
    @GetMapping("/") // GETリクエスト（/）を受け付ける
    public String index() {
        return "index"; // index.html を表示
    }   
}