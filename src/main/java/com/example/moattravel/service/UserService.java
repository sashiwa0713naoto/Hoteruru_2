package com.example.moattravel.service; // 会員登録や更新、パスワードの暗号化、アカウントの有効化や削除といった会員に関する処理をまとめて行うサービスクラス。

import org.springframework.security.crypto.password.PasswordEncoder; // パスワードを安全に暗号化（ハッシュ化）するための仕組み
import org.springframework.stereotype.Service; // Springにこのクラスがサービス層の部品であることを伝えるアノテーション
import org.springframework.transaction.annotation.Transactional; // 処理途中でエラーが起きたらデータベースの変更を自動で元に戻す仕組み

import com.example.moattravel.entity.Role; // 権限データをデータベースのテーブルと対応付けるためのクラス
import com.example.moattravel.entity.User; // ユーザーデータをデータベースのテーブルと対応付けるためのクラス
import com.example.moattravel.entity.VerificationToken; // メール認証用のトークンデータをデータベースのテーブルと対応付けるためのクラス
import com.example.moattravel.form.SignupForm; // 会員登録画面からの入力データを受け取るためのフォーム
import com.example.moattravel.form.UserEditForm; // 会員情報編集画面からの入力データを受け取るためのフォーム
import com.example.moattravel.repository.RoleRepository; // 権限データをデータベースから探すための仕組み
import com.example.moattravel.repository.UserRepository; // ユーザーデータのデータベース操作を行う仕組み
import com.example.moattravel.repository.VerificationTokenRepository; // メール認証用トークンのデータベース操作を行う仕組み

@Service // このクラスをSpringの部品（サービス）として登録する
public class UserService { // 会員に関するさまざまな処理やデータベース操作をまとめて実行するクラス

    private final UserRepository userRepository; // ユーザーデータをデータベースに保存・取得するための仕組み
    private final RoleRepository roleRepository; // 権限データをデータベースから探すための仕組み
    private final PasswordEncoder passwordEncoder; // パスワードを安全に暗号化するための仕組み
    private final VerificationTokenRepository verificationTokenRepository; // メール認証用トークンのデータベース操作を行う仕組み

    public UserService(UserRepository userRepository, 
                       RoleRepository roleRepository, 
                       PasswordEncoder passwordEncoder,
                       VerificationTokenRepository verificationTokenRepository) { // 必要なデータベース操作や暗号化の仕組みをまとめて受け取るコンストラクタ
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.verificationTokenRepository = verificationTokenRepository;
    }
    
    @Transactional // 処理の途中でエラーが発生した場合、データベースの変更をすべて自動で元に戻す
    public void enableUser(User user) { // ユーザーのアカウントを有効な状態（ログインできる状態）にするメソッド
        user.setEnabled(true); // アカウントを有効に設定する
        userRepository.save(user); // 変更をデータベースに保存する
    }
    
    @Transactional // 処理の途中でエラーが発生した場合、データベースの変更をすべて自動で元に戻す
    public User create(SignupForm signupForm) { // 新しい会員のデータをデータベースに登録するメソッド
        User user = new User(); // 新しいユーザーデータの入れ物を作る
        Role role = roleRepository.findByName("ROLE_GENERAL"); // 一般ユーザー向けの権限データを取得する

        user.setName(signupForm.getName()); // フォームから受け取った氏名をセットする
        user.setFurigana(signupForm.getFurigana()); // フォームから受け取ったフリガナをセットする
        user.setPostalCode(signupForm.getPostalCode()); // フォームから受け取った郵便番号をセットする
        user.setAddress(signupForm.getAddress()); // フォームから受け取った住所をセットする
        user.setPhoneNumber(signupForm.getPhoneNumber()); // フォームから受け取った電話番号をセットする
        user.setEmail(signupForm.getEmail()); // フォームから受け取ったメールアドレスをセットする
        
        user.setPassword(passwordEncoder.encode(signupForm.getPassword())); // パスワードを安全に暗号化してセットする
        user.setRole(role); // 取得した一般権限をセットする
        user.setEnabled(true); // アカウントを使える状態に設定する

        return userRepository.save(user); // 作成したユーザーデータをデータベースに保存して返す
    }

    public boolean isEmailRegistered(String email) { // 入力されたメールアドレスがすでに登録されているかどうかを調べるメソッド
        User user = userRepository.findByEmail(email); // メールアドレスをもとにユーザーを探す
        return user != null; // ユーザーが見つかった場合はすでに登録されている（true）と判定する
    }

    public boolean isSamePassword(String password, String passwordConfirmation) { // 入力されたパスワードと確認用パスワードが一致しているかを調べるメソッド
        return password.equals(passwordConfirmation); // 2つのパスワードが同じであればtrueを返す
    }

    @Transactional // 処理の途中でエラーが発生した場合、データベースの変更をすべて自動で元に戻す
    public void update(UserEditForm userEditForm) { // 会員の情報を更新するメソッド
        User user = userRepository.getReferenceById(userEditForm.getId()); // 更新対象のユーザーデータを効率よく取得する
        
        user.setName(userEditForm.getName()); // 新しい氏名をセットする
        user.setFurigana(userEditForm.getFurigana()); // 新しいフリガナをセットする
        user.setPostalCode(userEditForm.getPostalCode()); // 新しい郵便番号をセットする
        user.setAddress(userEditForm.getAddress()); // 新しい住所をセットする
        user.setPhoneNumber(userEditForm.getPhoneNumber()); // 新しい電話番号をセットする
        user.setEmail(userEditForm.getEmail()); // 新しいメールアドレスをセットする
        
        userRepository.save(user); // 変更をデータベースに保存して更新する
    }

    public boolean isEmailChanged(UserEditForm userEditForm) { // 会員情報の変更時に、メールアドレスがこれまでと変わったかどうかを判定するメソッド
        User currentUser = userRepository.getReferenceById(userEditForm.getId()); // 現在データベースに保存されているユーザー情報を取得する
        return !userEditForm.getEmail().equals(currentUser.getEmail()); // 入力されたメールアドレスが元のものと異なっていればtrueを返す
    }

    @Transactional // 処理の途中でエラーが発生した場合、データベースの変更をすべて自動で元に戻す
    public void deleteUser(Integer id) { // 会員データを削除するメソッド（関連する認証トークンもあわせて削除する）
        User user = userRepository.findById(id).orElse(null); // 削除するユーザーをIDで探す
        if (user != null) { // ユーザーが見つかった場合
            VerificationToken verificationToken = verificationTokenRepository.findByUser(user); // ユーザーに紐づくメール認証トークンを探す
            if (verificationToken != null) { // トークンが存在する場合
                verificationTokenRepository.delete(verificationToken); // 先に認証トークンを削除する
            }
            userRepository.delete(user); // ユーザー本体のデータを削除する
        }
    }
}