package com.example.moattravel.controller; // 1. このクラスが属するパッケージ（フォルダ構成）を指定

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
// Spring MVC（コントローラー・Web機能）関連のクラスをインポート
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

// アプリケーション内のEntity・Serviceをインポート
import com.example.moattravel.entity.Reservation;
import com.example.moattravel.service.ReservationService;


@Controller // 2. Spring Bootに「このクラスはWebリクエストを処理するコントローラーです」と認識させる
@RequestMapping("/admin/reservations") // 3. このクラス内のすべてのメソッドの基準URLを "/admin/reservations" に設定
public class AdminReservationController {

    // 4. 予約に関するビジネスロジック処理を行う依存サービス（フィールド）を宣言
    private final ReservationService reservationService;

    // 5. コンストラクタインジェクション（Springが自動で依存インスタンスを注入する）
    public AdminReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    /**
     * 管理者用 予約一覧ページ表示（GET /admin/reservations）
     */
    @GetMapping
    public String index(@PageableDefault(page = 0, size = 10, sort = "id") Pageable pageable, Model model) {
        // pageableを渡して Page<Reservation> を取得する
    	Page<Reservation> reservationPage = reservationService.findAllReservations(pageable);
        
        // HTML側で参照している変数名に合わせてモデルに追加
        model.addAttribute("reservationPage", reservationPage);
        
        return "admin/reservations/index";
    }
}