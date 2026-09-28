package com.example.moattravel.service; // 1. このクラスが属するパッケージ（サービス層）を指定

import org.springframework.security.crypto.password.PasswordEncoder; // パスワードハッシュ化インターフェース
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// エンティティ、フォーム、リポジトリのインポート
import com.example.moattravel.entity.Role;
import com.example.moattravel.entity.User;
import com.example.moattravel.entity.VerificationToken;
import com.example.moattravel.form.SignupForm;
import com.example.moattravel.form.UserEditForm;
import com.example.moattravel.repository.RoleRepository;
import com.example.moattravel.repository.UserRepository;
import com.example.moattravel.repository.VerificationTokenRepository;

@Service // 2. このクラスを Spring のサービス層コンポーネント（Bean）として自動登録
public class UserService {

    // 依存するリポジトリおよびパスワードエンコーダーの宣言
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final VerificationTokenRepository verificationTokenRepository;

    // 3. コンストラクタインジェクション（4つの依存関係を自動注入）
    public UserService(UserRepository userRepository, 
                       RoleRepository roleRepository, 
                       PasswordEncoder passwordEncoder,
                       VerificationTokenRepository verificationTokenRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.verificationTokenRepository = verificationTokenRepository;
    }
    
    /**
     * 4. メール認証完了時などにユーザーアカウントを有効化するメソッド
     * @param user 対象ユーザーエンティティ
     */
    @Transactional
    public void enableUser(User user) {
        user.setEnabled(true);
        userRepository.save(user);
    }
    
    /**
     * 5. 会員新規登録処理
     * @param signupForm 登録用フォームデータ
     * @return 登録された User エンティティ
     */
    @Transactional // 6. 登録処理をトランザクション管理
    public User create(SignupForm signupForm) {
        User user = new User();
        Role role = roleRepository.findByName("ROLE_GENERAL"); // デフォルトで一般ユーザー権限を取得

        user.setName(signupForm.getName());
        user.setFurigana(signupForm.getFurigana());
        user.setPostalCode(signupForm.getPostalCode());
        user.setAddress(signupForm.getAddress());
        user.setPhoneNumber(signupForm.getPhoneNumber());
        user.setEmail(signupForm.getEmail());
        
        // 7. 生パスワードを BCrypt 等で暗号化（ハッシュ化）してセット
        user.setPassword(passwordEncoder.encode(signupForm.getPassword()));
        user.setRole(role);
        user.setEnabled(true); // アカウントを有効状態に設定

        return userRepository.save(user);
    }

    /**
     * 8. メールアドレスの登録有無チェック（新規登録時の重複チェック用）
     * @param email チェック対象のメールアドレス
     * @return 登録済みの場合 true
     */
    public boolean isEmailRegistered(String email) {
        User user = userRepository.findByEmail(email);
        return user != null;
    }

    /**
     * 9. パスワードとパスワード（確認用）の一致チェック
     * @param password 入力パスワード
     * @param passwordConfirmation 確認用入力パスワード
     * @return 一致している場合 true
     */
    public boolean isSamePassword(String password, String passwordConfirmation) {
        return password.equals(passwordConfirmation);
    }

    /**
     * 10. 管理者または本人による会員情報の更新処理
     * @param userEditForm 編集用フォームデータ
     */
    @Transactional
    public void update(UserEditForm userEditForm) {
        // 不要なSELECTを発行せず参照プロキシを取得
        User user = userRepository.getReferenceById(userEditForm.getId());
        
        user.setName(userEditForm.getName());
        user.setFurigana(userEditForm.getFurigana());
        user.setPostalCode(userEditForm.getPostalCode());
        user.setAddress(userEditForm.getAddress());
        user.setPhoneNumber(userEditForm.getPhoneNumber());
        user.setEmail(userEditForm.getEmail());
        
        userRepository.save(user);
    }

    /**
     * 11. 会員情報更新時にメールアドレスが変更されたかどうかの判定
     * @param userEditForm 編集フォーム
     * @return 変更されている場合 true
     */
    public boolean isEmailChanged(UserEditForm userEditForm) {
        User currentUser = userRepository.getReferenceById(userEditForm.getId());
        return !userEditForm.getEmail().equals(currentUser.getEmail());
    }

    /**
     * 12. 会員削除処理
     * 外部キー制約違反（FKエラー）を回避するため、紐づくメール認証トークンを先に削除してからユーザー本体を削除
     * @param id 削除対象ユーザーのID
     */
    @Transactional
    public void deleteUser(Integer id) {
        User user = userRepository.findById(id).orElse(null);
        if (user != null) {
            // ユーザーに紐づく VerificationToken（認証用トークン）が存在すれば先に削除
            VerificationToken verificationToken = verificationTokenRepository.findByUser(user);
            if (verificationToken != null) {
                verificationTokenRepository.delete(verificationToken);
            }
            // ユーザー本体を削除
            userRepository.delete(user);
        }
    }
}