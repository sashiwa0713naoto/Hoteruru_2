package com.example.moattravel.controller; // 管理者のための予約管理コントローラー

import org.springframework.data.domain.Page; // ページごとにデータを分割して扱うための機能
import org.springframework.data.domain.Pageable; // ページ番号や表示件数の情報を扱うための仕組み
import org.springframework.data.web.PageableDefault; // ページの初期設定を行うための目印
import org.springframework.stereotype.Controller; // このクラスがWebからの窓口であることを示す目印
import org.springframework.ui.Model; // 画面にデータを渡すための箱
import org.springframework.web.bind.annotation.GetMapping; // 画面を表示するためのリクエストを受け取る目印
import org.springframework.web.bind.annotation.RequestMapping; // 共通のアドレスを設定する目印

import com.example.moattravel.entity.Reservation; // 予約のデータベース情報
import com.example.moattravel.service.ReservationService; // 予約に関する様々な処理のルール

@Controller // このクラスがWebの画面やリクエストを制御する役割を持つことを伝える
@RequestMapping("/admin/reservations") // この中にある処理はすべて「/admin/reservations」というアドレスから始まるようにする
public class AdminReservationController { // 管理者用の予約管理をまとめたクラスの定義開始
    private final ReservationService reservationService; // 予約の処理を行う仕組みを入れる変数を用意する
    public AdminReservationController(ReservationService reservationService) { // 必要な仕組みを自動で受け取るためのコンストラクタ
        this.reservationService = reservationService; // 受け取った予約処理の仕組みをクラス内で使えるようにセットする
    }
    
    @GetMapping // 「/admin/reservations」へのアクセスがあったときにこのメソッドを動かす
    public String index(@PageableDefault(page = 0, size = 10, sort = "id") Pageable pageable, Model model) { // 一覧のページ設定と画面にデータを送る箱を受け取る
        Page<Reservation> reservationPage = reservationService.findAllReservations(pageable); // ページごとに予約データを取得する
        
        model.addAttribute("reservationPage", reservationPage); // 取得した予約データを画面に渡す
        
        return "admin/reservations/index"; // 予約一覧画面のファイルを表示するように指示する
    }
} 