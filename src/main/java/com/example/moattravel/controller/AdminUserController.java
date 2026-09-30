package com.example.moattravel.controller; // 管理者のための会員管理コントローラー

import org.springframework.data.domain.Page; // ページごとにデータを分割して扱うための機能を読み込む
import org.springframework.data.domain.Pageable; // ページ番号や表示件数の情報を扱うための仕組みを読み込む
import org.springframework.data.domain.Sort.Direction; // データの並び順（昇順・降順）を指定する仕組みを読み込む
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

import com.example.moattravel.entity.User; // 会員のデータベース情報を表す仕組みを読み込む
import com.example.moattravel.form.UserEditForm; // 会員編集時の入力内容を一時的に保管する仕組みを読み込む
import com.example.moattravel.repository.UserRepository; // 会員のデータをデータベースから探したり保存したりする仕組みを読み込む
import com.example.moattravel.service.UserService; // 会員に関する様々な処理のルールが集まった仕組みを読み込む

@Controller // このクラスがWebの画面やリクエストを制御する役割を持つことを伝える
@RequestMapping("/admin/users") // この中にある処理はすべて「/admin/users」というアドレスから始まるようにする
public class AdminUserController { // 管理者用の会員管理をまとめたクラスの定義開始

    private final UserRepository userRepository; // データベース操作を行う仕組みを入れる変数を用意する
    private final UserService userService; // 会員の処理を行う仕組みを入れる変数を用意する

    public AdminUserController(UserRepository userRepository, UserService userService) { // 必要な仕組みを自動で受け取るためのコンストラクタ
        this.userRepository = userRepository; // 受け取ったデータベース操作の仕組みをクラス内で使えるようにセットする
        this.userService = userService; // 受け取った会員処理の仕組みをクラス内で使えるようにセットする
    }

    @GetMapping // 「/admin/users」へのアクセスがあったときにこのメソッドを動かす
    public String index(@RequestParam(name = "keyword", required = false) String keyword, // 検索キーワードが指定されていれば受け取る
                        @PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.ASC) Pageable pageable, // 一覧のページ番号や並び順の設定を指定する
                        Model model) { // 画面にデータを送るための箱を受け取る
        Page<User> userPage; // ページ分割された会員のデータを保存する変数を用意する

        if (keyword != null && !keyword.isEmpty()) { // 検索の言葉が空ではない場合
            userPage = userRepository.findByNameLikeOrFuriganaLike("%" + keyword + "%", "%" + keyword + "%", pageable); // 名前やフリガナからキーワードに似ているデータを探す
        } else { // 検索の言葉がない場合
            userPage = userRepository.findAll(pageable); // すべての会員データをページごとに取得する
        }

        model.addAttribute("userPage", userPage); // 取得したデータのページ情報を画面に渡す
        model.addAttribute("keyword", keyword); // 検索に使った言葉を画面に渡す

        return "admin/users/index"; // 会員一覧画面のファイルを表示するように指示する
    }

    @GetMapping("/{id}") // 「/admin/users/数字」というアクセスがあったときにこのメソッドを動かす
    public String show(@PathVariable(name = "id") Integer id, Model model) { // アドレスに含まれる番号と画面にデータを送る箱を受け取る
        User user = userRepository.findById(id).orElse(null); // 指定された番号の会員データをデータベースから探して取得する
        
        if (user == null) { // 指定された番号の会員データが見つからなかった場合
            return "redirect:/admin/users"; // 一覧画面へ強制的に移動させる
        }

        model.addAttribute("user", user); // 見つかった会員データを画面に渡す

        return "admin/users/show"; // 会員詳細画面のファイルを表示するように指示する
    }

    @GetMapping("/{id}/edit") // 「/admin/users/数字/edit」へのアクセスがあったときにこのメソッドを動かす
    public String edit(@PathVariable(name = "id") Integer id, Model model) { // アドレスに含まれる番号と画面にデータを送る箱を受け取る
        User user = userRepository.findById(id).orElse(null); // 編集する対象の会員データをデータベースから探す
        if (user == null) { // 対象の会員データが見つからなかった場合
            return "redirect:/admin/users"; // 一覧画面へ強制的に移動させる
        }

        UserEditForm userEditForm = new UserEditForm( // データベースから取得したデータを編集用の箱に移し替える
            user.getId(), // 会員の番号をセットする
            user.getName(), // 会員の名前をセットする
            user.getFurigana(), // 会員のフリガナをセットする
            user.getPostalCode(), // 郵便番号をセットする
            user.getAddress(), // 住所をセットする
            user.getPhoneNumber(), // 電話番号をセットする
            user.getEmail() // メールアドレスをセットする
        );

        model.addAttribute("userEditForm", userEditForm); // 編集用のデータを画面に渡す

        return "admin/users/edit"; // 会員編集画面のファイルを表示するように指示する
    }

    @PostMapping("/{id}/update") // 「/admin/users/数字/update」へのデータの送信があったときにこのメソッドを動かす
    public String update(@PathVariable(name = "id") Integer id, // アドレスに含まれる番号を受け取る
                         @ModelAttribute @Validated UserEditForm userEditForm, // 編集された入力データをチェックして受け取る
                         BindingResult bindingResult, // 入力チェックの結果を受け取る
                         RedirectAttributes redirectAttributes) { // 移動先の画面にメッセージを渡す仕組みを受け取る
        
        if (bindingResult.hasErrors()) { // 入力内容にエラーがある場合
            return "admin/users/edit"; // 編集画面に戻す
        }

        userService.update(userEditForm); // 会員データの更新処理を実行する
        redirectAttributes.addFlashAttribute("successMessage", "会員情報を編集しました。"); // 更新成功のメッセージを一時保存する

        return "redirect:/admin/users/" + id; // 編集した会員の詳細画面に移動する
    }

    @PostMapping("/{id}/delete") // 「/admin/users/数字/delete」へのデータの送信があったときにこのメソッドを動かす
    public String delete(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes) { // アドレスに含まれる番号とメッセージを渡す仕組みを受け取る
        userService.deleteUser(id); // 指定された番号の会員データを削除する処理を実行する
        
        redirectAttributes.addFlashAttribute("successMessage", "会員を削除しました。"); // 削除成功のメッセージを一時保存する

        return "redirect:/admin/users"; // 削除が終わったら一覧画面に移動する
    }
} // 管理者用の会員管理コントローラークラスの終わり