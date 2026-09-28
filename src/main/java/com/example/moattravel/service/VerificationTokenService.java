package com.example.moattravel.service;

import org.springframework.security.crypto.password.PasswordEncoder; // パスワードの暗号化（ハッシュ化）を行うクラス
import org.springframework.stereotype.Service; // サービスクラス（業務ロジック）であることをSpringに伝えるアノテーション
import org.springframework.transaction.annotation.Transactional; // データベース処理の整合性を保つ（失敗時に自動ロールバック）アノテーション

// データベースのテーブル（エンティティ）、画面から送られるフォームデータ、DB操作用リポジトリのインポート
import com.example.moattravel.entity.Role;
import com.example.moattravel.entity.User;
import com.example.moattravel.entity.VerificationToken;
import com.example.moattravel.form.SignupForm;
import com.example.moattravel.form.UserEditForm;
import com.example.moattravel.repository.RoleRepository;
import com.example.moattravel.repository.UserRepository;
import com.example.moattravel.repository.VerificationTokenRepository;

/**
 * 会員情報およびメール認証トークンに関する各種業務ロジック（登録・更新・検証・削除）を管理するサービスクラス
 */
@Service
public class VerificationTokenService {

    // データベース操作や暗号化処理を行うための部品（依存コンポーネント）の宣言
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final VerificationTokenRepository verificationTokenRepository;

    /**
     * コンストラクタインジェクション
     * 必要なリポジトリや暗号化モジュールをSpringが自動的に注入（セット）します。
     */
    public VerificationTokenService(UserRepository userRepository, 
                                   RoleRepository roleRepository, 
                                   PasswordEncoder passwordEncoder,
                                   VerificationTokenRepository verificationTokenRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.verificationTokenRepository = verificationTokenRepository;
    }

    /**
     * 【メール認証完了処理】
     * ユーザーのアカウント有効化フラグ（enabled）を true に更新します。
     * （ユーザーが本人のメールで認証リンクを押した時に実行されます）
     * 
     * @param user 有効化対象のユーザーエンティティ
     */
    @Transactional
    public void enableUser(User user) {
        user.setEnabled(true);
        userRepository.save(user); // データベースの状態を更新
    }

    /**
     * 【新規会員の登録処理】
     * 会員登録画面から送られたデータ（SignupForm）を受け取り、パスワードを安全に暗号化した上でDBに登録します。
     * 
     * @param signupForm ユーザーが入力した登録フォームデータ
     * @return データベースに保存された User エンティティ
     */
    @Transactional
    public User create(SignupForm signupForm) {
        User user = new User();
        // 一般ユーザー権限（ROLE_GENERAL）を取得して設定
        Role role = roleRepository.findByName("ROLE_GENERAL");

        // フォームからの入力をユーザーオブジェクトにセット
        user.setName(signupForm.getName());
        user.setFurigana(signupForm.getFurigana());
        user.setPostalCode(signupForm.getPostalCode());
        user.setAddress(signupForm.getAddress());
        user.setPhoneNumber(signupForm.getPhoneNumber());
        user.setEmail(signupForm.getEmail());

        // 生パスワードを暗号化（BCrypt等）して安全に保存
        user.setPassword(passwordEncoder.encode(signupForm.getPassword()));
        user.setRole(role);
        user.setEnabled(true); // 初期状態でアカウントを有効化

        return userRepository.save(user); // DBへ挿入
    }

    /**
     * 【メールアドレスの重複チェック】
     * 入力されたメールアドレスがすでに登録済みかどうかを確認します。
     * 
     * @param email チェック対象のメールアドレス
     * @return すでに登録済みの場合は true、未登録の場合は false
     */
    public boolean isEmailRegistered(String email) {
        User user = userRepository.findByEmail(email);
        return user != null;
    }

    /**
     * 【パスワード一致チェック】
     * 会員登録時などに入力された「パスワード」と「確認用パスワード」が合致しているか判定します。
     * 
     * @param password 入力されたパスワード
     * @param passwordConfirmation 確認用の入力パスワード
     * @return 一致していれば true
     */
    public boolean isSamePassword(String password, String passwordConfirmation) {
        return password.equals(passwordConfirmation);
    }

    /**
     * 【会員情報の更新処理】
     * ユーザーのプロフィール編集画面から送信されたデータをもとに、DB上の会員情報を更新します。
     * 
     * @param userEditForm 編集後のフォームデータ
     */
    @Transactional
    public void update(UserEditForm userEditForm) {
        // IDをもとに既存のユーザー情報を読み込み（参照プロキシを取得）
        User user = userRepository.getReferenceById(userEditForm.getId());

        user.setName(userEditForm.getName());
        user.setFurigana(userEditForm.getFurigana());
        user.setPostalCode(userEditForm.getPostalCode());
        user.setAddress(userEditForm.getAddress());
        user.setPhoneNumber(userEditForm.getPhoneNumber());
        user.setEmail(userEditForm.getEmail());

        userRepository.save(user); // 更新内容を保存
    }

    /**
     * 【メールアドレスの変更有無判定】
     * プロフィール更新時に、以前のメールアドレスから変更があったかを検証します。
     * 
     * @param userEditForm 編集用フォームデータ
     * @return 変更されている場合 true
     */
    public boolean isEmailChanged(UserEditForm userEditForm) {
        User currentUser = userRepository.getReferenceById(userEditForm.getId());
        return !userEditForm.getEmail().equals(currentUser.getEmail());
    }

    /**
     * 【会員アカウント削除処理】
     * ユーザーを削除する際、外部キー制約（エラー）を避けるために
     * 先に関連する認証用トークン（VerificationToken）を削除してから、ユーザー本体を削除します。
     * 
     * @param id 削除対象のユーザーID
     */
    @Transactional
    public void deleteUser(Integer id) {
        User user = userRepository.findById(id).orElse(null);
        if (user != null) {
            // ユーザーに紐づくトークンが存在する場合は先に削除
            VerificationToken verificationToken = verificationTokenRepository.findByUser(user);
            if (verificationToken != null) {
                verificationTokenRepository.delete(verificationToken);
            }
            // ユーザー本体を削除
            userRepository.delete(user);
        }
    }

    /**
     * 【トークン情報の検索】
     * メール内のURLに含まれるトークン文字列（例: ?token=xxxx）から、DB内のトークン情報を取得します。
     * 
     * @param token トークン文字列
     * @return DBに保存されている VerificationToken オブジェクト
     */
    public VerificationToken getVerificationToken(String token) {
        return verificationTokenRepository.findByToken(token);
    }

    /**
     * 【メール認証トークンの新規生成＆保存】
     * 会員登録時、ユーザーとランダム発行されたトークン文字列（UUID等）を紐づけてDBに保存します。
     * 
     * @param user 対象となるユーザー
     * @param token ランダム生成された認証トークン文字列
     */
    @Transactional
    public void create(User user, String token) {
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setUser(user);   // 対象ユーザーを紐づけ
        verificationToken.setToken(token); // トークン文字列を紐づけ

        verificationTokenRepository.save(verificationToken); // トークンテーブルに保存
    }
}