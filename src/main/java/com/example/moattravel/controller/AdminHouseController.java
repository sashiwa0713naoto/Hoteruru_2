package com.example.moattravel.controller; // 管理者のための民宿管理コントローラー

import org.springframework.data.domain.Page; // ページごとにデータを分割して扱うための機能を読み込む
import org.springframework.data.domain.Pageable; // ページ番号や表示件数の情報を扱うための仕組みを読み込む
import org.springframework.data.web.PageableDefault; // ページの初期設定を行うための目印を読み込む
import org.springframework.stereotype.Controller; // このクラスがWebからの窓口であることを示す目印を読み込む
import org.springframework.ui.Model; // 画面にデータを渡すための箱を読み込む
import org.springframework.validation.BindingResult; // 入力内容に間違いがないかチェックした結果を受け取る仕組みを読み込む
import org.springframework.validation.annotation.Validated; // 入力チェックの実行を指示する目印を読み込む
import org.springframework.web.bind.annotation.GetMapping; // 画面を表示するためのリクエストを受け取る目印を読み込む
import org.springframework.web.bind.annotation.ModelAttribute; // 送られてきたフォームのデータをオブジェクトにまとめる目印を読み込む
import org.springframework.web.bind.annotation.PathVariable; // アドレスの一部にある番号などを受け取る目印を読み込む
import org.springframework.web.bind.annotation.PostMapping; // データを保存・更新するためのリクエストを受け取る目印を読み込む
import org.springframework.web.bind.annotation.RequestMapping; // 共通のアドレスを設定する目印を読み込む
import org.springframework.web.bind.annotation.RequestParam; // アドレスの後ろにつくパラメータを受け取る目印を読み込む
import org.springframework.web.servlet.mvc.support.RedirectAttributes; // 別の画面に一時的なメッセージを渡す仕組みを読み込む

import com.example.moattravel.entity.House; // 民宿のデータベース情報を表す仕組みを読み込む
import com.example.moattravel.form.HouseEditForm; // 民宿編集時の入力内容を一時的に保管する仕組みを読み込む
import com.example.moattravel.form.HouseRegisterForm; // 民宿登録時の入力内容を一時的に保管する仕組みを読み込む
import com.example.moattravel.repository.HouseRepository; // 民宿のデータをデータベースから探したり保存したりする仕組みを読み込む
import com.example.moattravel.service.HouseService; // 民宿に関する様々な処理のルールが集まった仕組みを読み込む

@Controller // このクラスがWebの画面やリクエストを制御する役割を持つことを伝える
@RequestMapping("/admin/houses") // この中にある処理はすべて「/admin/houses」というアドレスから始まるようにする
public class AdminHouseController { // 管理者用の民宿管理をまとめたクラスの定義開始

    private final HouseRepository houseRepository; // データベース操作を行う仕組みを入れる変数を用意する
    private final HouseService houseService; // 民宿の処理を行う仕組みを入れる変数を用意する

    public AdminHouseController(HouseRepository houseRepository, HouseService houseService) { // 必要な仕組みを自動で受け取るためのコンストラクタ
        this.houseRepository = houseRepository; // 受け取ったデータベース操作の仕組みをクラス内で使えるようにセットする
        this.houseService = houseService; // 受け取った民宿処理の仕組みをクラス内で使えるようにセットする
    }

    @GetMapping // 「/admin/houses」へのアクセス（画面表示の依頼）があったときにこのメソッドを動かす
    public String index(Model model,  // 画面にデータを送るための箱を受け取る
                         @PageableDefault(page = 0, size = 10) Pageable pageable, // 一覧のページ番号と1ページあたりの表示件数を指定する
                         @RequestParam(name = "keyword", required = false) String keyword) { // 検索キーワードが指定されていれば受け取る
        Page<House> housePage; // ページ分割された民宿のデータを保存する変数を用意する
        
        if (keyword != null && !keyword.isEmpty()) { // 検索の言葉が空ではない場合
            housePage = houseRepository.findByNameLikeOrPostalCodeLikeOrAddressLikeOrPhoneNumberLike( // キーワードに似ているデータをデータベースから探す
                "%" + keyword + "%",  // 名前から探すための条件を指定する
                "%" + keyword + "%",  // 郵便番号から探すための条件を指定する
                "%" + keyword + "%",  // 住所から探すための条件を指定する
                "%" + keyword + "%",  // 電話番号から探すための条件を指定する
                pageable // ページ分割のルールを渡す
            );
        } else { // 検索の言葉がない場合
            housePage = houseRepository.findAll(pageable); // すべてのデータをページごとに取得する
        }

        model.addAttribute("housePage", housePage); // 取得したデータのページ情報を画面に渡す
        model.addAttribute("keyword", keyword); // 検索に使った言葉を画面に渡す

        return "admin/houses/index"; // 民宿一覧画面のファイルを表示するように指示する
    }

    @GetMapping("/{id}") // 「/admin/houses/数字」というアクセスがあったときにこのメソッドを動かす
    public String show(@PathVariable(name = "id") Integer id, Model model) { // アドレスに含まれる番号と画面にデータを送る箱を受け取る
        House house = houseRepository.findById(id).orElse(null); // 指定された番号の民宿データをデータベースから探して取得する
        
        if (house == null) { // 指定された番号の民宿データが見つからなかった場合
            return "redirect:/admin/houses"; // 一覧画面へ強制的に移動させる
        }
        
        model.addAttribute("house", house); // 見つかった民宿データを画面に渡す

        return "admin/houses/show"; // 民宿詳細画面のファイルを表示するように指示する
    }

    @GetMapping("/register") // 「/admin/houses/register」へのアクセスがあったときにこのメソッドを動かす
    public String register(Model model) { // 画面にデータを送るための箱を受け取る
        model.addAttribute("houseRegisterForm", new HouseRegisterForm()); // 新規登録用の空っぽの箱を用意して画面に渡す
        return "admin/houses/register"; // 民宿登録画面のファイルを表示するように指示する
    }

    @PostMapping("/create") // 「/admin/houses/create」へのデータの送信があったときにこのメソッドを動かす
    public String create(@ModelAttribute @Validated HouseRegisterForm houseRegisterForm,  // 送られてきた入力データをチェックして受け取る
                         BindingResult bindingResult,  // 入力チェックでエラーがなかったかどうかの結果を受け取る(bindingResultはバリデーションの結果やデータバインディングのエラーを保持)
                         RedirectAttributes redirectAttributes,  // 移動先の画面にメッセージを渡すための仕組みを受け取る
                         Model model) { // 画面にデータを送るための箱を受け取る
        
        if (bindingResult.hasErrors()) { // 入力内容に間違いや空欄などのエラーがある場合
            return "admin/houses/register"; // 登録画面に戻す
        }

        houseService.create(houseRegisterForm); // 新しい民宿データを登録する処理を実行する
        redirectAttributes.addFlashAttribute("successMessage", "民宿を登録しました。"); // 登録成功のメッセージを一時保存する

        return "redirect:/admin/houses"; // 登録が終わったら一覧画面に移動する
    }

    @GetMapping("/{id}/edit") // 「/admin/houses/数字/edit」へのアクセスがあったときにこのメソッドを動かす
    public String edit(@PathVariable(name = "id") Integer id, Model model) { // アドレスに含まれる番号と画面にデータを送る箱を受け取る
        House house = houseRepository.findById(id).orElse(null); // 編集する対象の民宿データをデータベースから探す
        if (house == null) { // 対象の民宿データが見つからなかった場合
            return "redirect:/admin/houses"; // 一覧画面へ強制的に移動させる
        }

        HouseEditForm houseEditForm = new HouseEditForm( // データベースから取得したデータを編集用の箱に移し替える
            house.getId(),  // 民宿の番号
            house.getName(),  // 民宿の名前
            house.getImageName(),  // 画像の名前
            null,  // 新しい画像ファイル用の初期値
            house.getDescription(),  // 説明文
            house.getPrice(),  // 宿泊料金
            house.getCapacity(),  // 定員
            house.getPostalCode(),  // 郵便番号
            house.getAddress(),  // 住所
            house.getPhoneNumber() // 電話番号
        );
        
        model.addAttribute("houseEditForm", houseEditForm); // 編集用のデータを画面に渡す

        return "admin/houses/edit"; // 民宿編集画面のファイルを表示するように指示する
    }

    @PostMapping("/{id}/update") // 「/admin/houses/数字/update」へのデータの送信があったときにこのメソッドを動かす
    public String update(@PathVariable(name = "id") Integer id,  // アドレスに含まれる番号を受け取る
                         @ModelAttribute @Validated HouseEditForm houseEditForm,  // 編集された入力データをチェックして受け取る
                         BindingResult bindingResult,  // 入力チェックの結果を受け取る
                         RedirectAttributes redirectAttributes,  // 移動先の画面にメッセージを渡す仕組みを受け取る
                         Model model) { // 画面にデータを送るための箱を受け取る
        
        if (bindingResult.hasErrors()) { // 入力内容にエラーがある場合(bindingResultはバリデーションの結果やデータバインディングのエラーを保持)
            return "admin/houses/edit"; // 編集画面に戻す
        }

        houseService.update(houseEditForm); // 民宿データの更新処理を実行する
        redirectAttributes.addFlashAttribute("successMessage", "民宿情報を編集しました。"); // 更新成功のメッセージを一時保存する

        return "redirect:/admin/houses"; // 更新が終わったら一覧画面に移動する
    }

    @PostMapping("/{id}/delete") // 「/admin/houses/数字/delete」へのデータの送信があったときにこのメソッドを動かす
    public String delete(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes) { // アドレスに含まれる番号とメッセージを渡す仕組みを受け取る
        houseRepository.deleteById(id); // 指定された番号の民宿データをデータベースから削除する
        
        redirectAttributes.addFlashAttribute("successMessage", "民宿を削除しました。"); // 削除成功のメッセージを一時保存する

        return "redirect:/admin/houses"; // 削除が終わったら一覧画面に移動する
    }
}