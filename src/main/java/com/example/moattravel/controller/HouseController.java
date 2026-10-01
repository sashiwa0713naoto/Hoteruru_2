package com.example.moattravel.controller; // 管理者画面や民宿関連のコントローラークラスなどをまとめるパッケージ

import org.springframework.data.domain.Page; // ページごとにデータを分割して扱うための機能
import org.springframework.data.domain.Pageable; // ページ番号や表示件数、並び替えのルールを受け取るための機能
import org.springframework.data.domain.Sort.Direction; // データの並び順（昇順や降順）を指定するための機能
import org.springframework.data.web.PageableDefault; // ページングの初期設定を指定するための機能
import org.springframework.stereotype.Controller; // このクラスがWebの画面やリクエストを制御する役割を持つことを伝える
import org.springframework.ui.Model; // 処理結果のデータをHTML画面に渡すための箱
import org.springframework.web.bind.annotation.GetMapping; // URLへのアクセス（GETメソッド）を受け付けるための指定
import org.springframework.web.bind.annotation.PathVariable; // URLに含まれるIDなどの値を受け取るための指定
import org.springframework.web.bind.annotation.RequestMapping; // このクラス内のアドレスの共通の開始位置を指定する
import org.springframework.web.bind.annotation.RequestParam; // URLのクエリパラメータを受け取るための指定

import com.example.moattravel.entity.House; // 民宿のデータベース情報を表すクラス
import com.example.moattravel.form.ReservationInputForm; // 予約入力用のフォームクラス
import com.example.moattravel.repository.HouseRepository; // 民宿データのデータベース操作を行うための機能

@Controller // このクラスがWebの画面やリクエストを制御する役割を持つことを伝える
@RequestMapping("/houses") // この中にある処理はすべて「/houses」というアドレスから始まるようにする
public class HouseController { // 民宿に関する操作をまとめたクラスの定義開始

    private final HouseRepository houseRepository; // データベース操作の機能を使うための変数を宣言する

    public HouseController(HouseRepository houseRepository) { // 必要な仕組みを自動で受け取るためのコンストラクタ
        this.houseRepository = houseRepository; // 受け取ったデータベース操作の仕組みをクラス内で使えるようにセットする
    }

    @GetMapping // 「/houses」へのアクセス（一覧・検索画面の表示）があったときにこのメソッドを動かす
    public String index(
            @RequestParam(name = "keyword", required = false) String keyword, // 検索キーワードを受け取る（なくてもOK）
            @RequestParam(name = "area", required = false) String area, // エリア名を受け取る（なくてもOK）
            @PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.DESC) Pageable pageable, // 一覧のページ設定と画面にデータを送る箱を受け取る
            Model model) { // 画面にデータを渡すための箱を受け取る
        
        Page<House> housePage; // 検索結果や全件のデータをページごとに格納する変数

        boolean hasKeyword = (keyword != null && !keyword.isEmpty()); // キーワードが入力されているかどうかを判定する
        boolean hasArea = (area != null && !area.isEmpty()); // エリアが選択されているかどうかを判定する

        if (hasKeyword && hasArea) { // キーワードとエリアの両方が指定されている場合の処理
            housePage = houseRepository.findByNameLikeOrPostalCodeLikeOrAddressLikeOrPhoneNumberLikeAndAddressLike(
                "%" + keyword + "%", "%" + keyword + "%", "%" + keyword + "%", "%" + keyword + "%", 
                "%" + area + "%", pageable
            );
        } else if (hasKeyword) { // キーワードのみが指定されている場合の処理
            housePage = houseRepository.findByNameLikeOrPostalCodeLikeOrAddressLikeOrPhoneNumberLike(
                "%" + keyword + "%", "%" + keyword + "%", "%" + keyword + "%", "%" + keyword + "%", 
                pageable
            );
        } else if (hasArea) { // エリアのみが指定されている場合の処理
            switch (area) {
                case "北海道・東北": // エリアが「北海道・東北」の場合
                    housePage = houseRepository.findByHokkaidoTohoku(
                        "%北海道%", "%青森%", "%岩手%", "%宮城%", "%秋田%", "%山形%", "%福島%", pageable);
                    break;
                case "関東": // エリアが「関東」の場合
                    housePage = houseRepository.findByKanto(
                        "%茨城%", "%栃木%", "%群馬%", "%埼玉%", "%千葉%", "%東京%", "%神奈川%", pageable);
                    break;
                case "中部": // エリアが「中部」の場合
                    housePage = houseRepository.findByChubu(
                        "%新潟%", "%富山%", "%石川%", "%福井%", "%山梨%", "%長野%", "%岐阜%", "%静岡%", "%愛知%", pageable);
                    break;
                case "近畿": // エリアが「近畿」の場合
                    housePage = houseRepository.findByKinki(
                        "%三重%", "%滋賀%", "%京都%", "%大阪%", "%兵庫%", "%奈良%", "%和歌山%", pageable);
                    break;
                case "中国": // エリアが「中国」の場合
                    housePage = houseRepository.findByChugoku(
                        "%鳥取%", "%島根%", "%岡山%", "%広島%", "%山口%", pageable);
                    break;
                case "四国": // エリアが「四国」の場合
                    housePage = houseRepository.findByShikoku(
                        "%徳島%", "%香川%", "%愛媛%", "%高知%", pageable);
                    break;
                case "九州・沖縄": // エリアが「九州・沖縄」の場合
                    housePage = houseRepository.findByKyushuOkinawa(
                        "%福岡%", "%佐賀%", "%長崎%", "%熊本%", "%大分%", "%宮崎%", "%鹿児島%", "%沖縄%", pageable);
                    break;
                default: // その他の都道府県などの場合
                    housePage = houseRepository.findByAddressLike("%" + area + "%", pageable);
                    break;
            }
        } else { // どちらも指定されていない場合の全件取得処理
            housePage = houseRepository.findAll(pageable);
        }

        model.addAttribute("housePage", housePage); // 取得した民宿のデータを画面に渡す
        model.addAttribute("keyword", keyword); // 入力されたキーワードを画面に渡す（入力欄に文字を残すため）
        model.addAttribute("area", area); // 選択されたエリアを画面に渡す
        
        return "houses/index"; // 民宿一覧画面のファイルを表示するように指示する
    }

    @GetMapping("/{id}") // 「/houses/数字」というアドレス（詳細画面へのアクセス）があったときにこのメソッドを動かす
    public String show(@PathVariable(name = "id") Integer id, Model model) { // URLから民宿のIDを受け取り、画面にデータを送る箱を用意する
        House house = houseRepository.getReferenceById(id); // 指定されたIDの民宿データをデータベースから取得する
        
        model.addAttribute("house", house); // 取得した民宿の詳細データを画面に渡す
        model.addAttribute("reservationInputForm", new ReservationInputForm()); // 予約入力用の空のフォームを画面に渡す
        
        return "houses/show"; // 民宿詳細画面のファイルを表示するように指示する
    }
}