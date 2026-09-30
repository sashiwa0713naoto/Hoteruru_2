package com.example.moattravel.controller; //一般ユーザー向けの民宿一覧表示、詳細表示、キーワード・エリア検索などの機能（Webリクエスト）を統括・制御するコントローラークラス。

// Spring Data Pageableおよびソート機能関連のクラスをインポート
import org.springframework.data.domain.Page; // 検索結果や全件データをページ分割（ページネーション）して扱うためのクラスを読み込む
import org.springframework.data.domain.Pageable; // ページ番号、1ページあたりの表示件数、ソート条件などを保持するインターフェースを読み込む
import org.springframework.data.domain.Sort.Direction; // ソートの方向（昇順・降順）を指定するためのクラスを読み込む
import org.springframework.data.web.PageableDefault; // コントローラーの引数でページネーションのデフォルト設定（初期ページや表示件数など）を指定するためのアノテーションを読み込む
// Spring MVC（コントローラー・Web機能）関連のクラスをインポート
import org.springframework.stereotype.Controller; // このクラスがSpring MVCのコントローラー（Webリクエストを処理する役割）であることを示すアノテーションを読み込む
import org.springframework.ui.Model; // コントローラーからView（HTMLテンプレート）へデータを渡すためのコンテナクラスを読み込む
import org.springframework.web.bind.annotation.GetMapping; // HTTPのGETメソッド（データの取得・画面表示）のリクエストをマッピングするためのアノテーションを読み込む
import org.springframework.web.bind.annotation.PathVariable; // URLのパスの一部（例: /houses/{id} の数値など）をメソッドの引数として受け取るためのアノテーションを読み込む
import org.springframework.web.bind.annotation.RequestMapping; // コントローラー全体やメソッドの共通URLプレフィックスを設定するためのアノテーションを読み込む
import org.springframework.web.bind.annotation.RequestParam; // URLのクエリパラメータ（例: ?keyword=...）の値を受け取るためのアノテーションを読み込む

// アプリケーション内のEntity・Form・Repositoryをインポート
import com.example.moattravel.entity.House; // データベースのhousesテーブルと1対1で対応するエンティティクラスを読み込む
import com.example.moattravel.form.ReservationInputForm; // 予約入力時のフォームデータを保持するクラスを読み込む
import com.example.moattravel.repository.HouseRepository; // データベースに対する民宿データの検索・取得などの操作を行うリポジトリを読み込む

@Controller // Spring Bootに「このクラスはWebリクエストを処理するコントローラーです」と認識させる
@RequestMapping("/houses") // このクラス内のすべてのメソッドの基準URLを "/houses" に設定
public class HouseController {

    // 民宿データの操作を行うリポジトリ（フィールド）を宣言
    private final HouseRepository houseRepository;

    // コンストラクタインジェクション（Springが自動で依存インスタンスを注入する）
    public HouseController(HouseRepository houseRepository) {
        this.houseRepository = houseRepository;
    }

    // 一般ユーザー向け 民宿一覧・検索ページ表示（GET /houses）
    @GetMapping // GETリクエスト（/houses）を受け付ける
    public String index(
            @RequestParam(name = "keyword", required = false) String keyword, // 検索キーワードを取得（任意）
            @RequestParam(name = "area", required = false) String area,        // 選択されたエリア/都道府県を取得（任意）
            @PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.DESC) Pageable pageable, // ページネーション・ソート初期設定（IDの降順、10件）
            Model model) {
        
        Page<House> housePage;

        // パラメータの有無（空文字チェック含む）を判定するフラグを作成
        boolean hasKeyword = (keyword != null && !keyword.isEmpty());
        boolean hasArea = (area != null && !area.isEmpty());

        // 検索条件（キーワード・エリア）の組み合わせによって呼び出すクエリを分岐
        if (hasKeyword && hasArea) {
            // キーワードとエリアの両方が指定されている場合
            housePage = houseRepository.findByNameLikeOrPostalCodeLikeOrAddressLikeOrPhoneNumberLikeAndAddressLike(
                "%" + keyword + "%", "%" + keyword + "%", "%" + keyword + "%", "%" + keyword + "%", 
                "%" + area + "%", pageable
            );
        } else if (hasKeyword) {
            // キーワードのみが指定されている場合（名称・郵便番号・住所・電話番号で曖昧検索）
            housePage = houseRepository.findByNameLikeOrPostalCodeLikeOrAddressLikeOrPhoneNumberLike(
                "%" + keyword + "%", "%" + keyword + "%", "%" + keyword + "%", "%" + keyword + "%", 
                pageable
            );
        } else if (hasArea) {
            // エリアのみが指定されている場合（地方区分ごとに含まれる都道府県を検索）
            switch (area) {
                case "北海道・東北":
                    housePage = houseRepository.findByHokkaidoTohoku(
                        "%北海道%", "%青森%", "%岩手%", "%宮城%", "%秋田%", "%山形%", "%福島%", pageable);
                    break;
                case "関東":
                    housePage = houseRepository.findByKanto(
                        "%茨城%", "%栃木%", "%群馬%", "%埼玉%", "%千葉%", "%東京%", "%神奈川%", pageable);
                    break;
                case "中部":
                    housePage = houseRepository.findByChubu(
                        "%新潟%", "%富山%", "%石川%", "%福井%", "%山梨%", "%長野%", "%岐阜%", "%静岡%", "%愛知%", pageable);
                    break;
                case "近畿":
                    housePage = houseRepository.findByKinki(
                        "%三重%", "%滋賀%", "%京都%", "%大阪%", "%兵庫%", "%奈良%", "%和歌山%", pageable);
                    break;
                case "中国":
                    housePage = houseRepository.findByChugoku(
                        "%鳥取%", "%島根%", "%岡山%", "%広島%", "%山口%", pageable);
                    break;
                case "四国":
                    housePage = houseRepository.findByShikoku(
                        "%徳島%", "%香川%", "%愛媛%", "%高知%", pageable);
                    break;
                case "九州・沖縄":
                    housePage = houseRepository.findByKyushuOkinawa(
                        "%福岡%", "%佐賀%", "%長崎%", "%熊本%", "%大分%", "%宮崎%", "%鹿児島%", "%沖縄%", pageable);
                    break;
                default:
                    // 指定されたエリア名（例: 県名など）で住所を部分一致検索
                    housePage = houseRepository.findByAddressLike("%" + area + "%", pageable);
                    break;
            }
        } else {
            // 条件指定がない場合：全件を取得（ページネーション適用）
            housePage = houseRepository.findAll(pageable);
        }

        // View（HTML）へ渡す検索結果・条件データをModelに追加
        model.addAttribute("housePage", housePage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("area", area);
        
        return "houses/index"; // 表示するHTMLテンプレート（houses/index.html）を返す
    }

    // 一般ユーザー向け 民宿詳細ページ表示（GET /houses/{id}）
    @GetMapping("/{id}") // GETリクエスト（/houses/{id}）を受け付ける
    public String show(@PathVariable(name = "id") Integer id, Model model) { // URLパスから民宿IDを取得
        // 指定されたIDのエンティティ参照を取得
        House house = houseRepository.getReferenceById(id);
        
        // 画面表示用データと、予約入力用のフォームオブジェクトをModelに追加
        model.addAttribute("house", house);
        model.addAttribute("reservationInputForm", new ReservationInputForm());
        
        return "houses/show"; // 表示するHTMLテンプレート（houses/show.html）を返す
    }
}