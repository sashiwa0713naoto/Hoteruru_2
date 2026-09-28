package com.example.moattravel.service; // 1. このクラスが属するパッケージ（サービス層）を指定

// 入出力処理（IO）やファイル操作・パス設定用クラスをインポート
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID; // 一意なファイル名生成に使用

// Spring Framework（サービス・トランザクション・ファイル受信）の機能をインポート
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

// 操作対象のエンティティ、フォーム（DTO）、リポジトリをインポート
import com.example.moattravel.entity.House;
import com.example.moattravel.form.HouseEditForm;
import com.example.moattravel.form.HouseRegisterForm;
import com.example.moattravel.repository.HouseRepository;

@Service // 2. このクラスを Spring のサービス層コンポーネント（Bean）として自動登録
public class HouseService {
    private final HouseRepository houseRepository; // DB操作用リポジトリ

    // 3. コンストラクタインジェクション（HouseRepositoryを自動注入）
    public HouseService(HouseRepository houseRepository) {
        this.houseRepository = houseRepository;
    }

    /**
     * 民宿情報の新規登録処理
     * @param houseRegisterForm 新規登録用フォームデータ
     */
    @Transactional // 4. メソッド全体をトランザクション管理（例外発生時は自動ロールバック）
    public void create(HouseRegisterForm houseRegisterForm) {
        House house = new House();
        MultipartFile imageFile = houseRegisterForm.getImageFile();

        // 5. 画像ファイルがアップロードされている場合の処理
        if (imageFile != null && !imageFile.isEmpty()) {
            String imageName = imageFile.getOriginalFilename();
            String hashedImageName = generateNewFileName(imageName); // UUIDを用いた一意なファイル名の生成
            
            // プロジェクト直下の storage/ フォルダを保存先に指定
            Path filePath = Paths.get("storage/" + hashedImageName);

            try {
                // 保存先ディレクトリが存在しない場合は自動作成
                Files.createDirectories(filePath.getParent());
                // ファイルのコピー・保存（同名ファイルが存在する場合は上書き）
                Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                house.setImageName(hashedImageName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            house.setImageName(""); // 画像未選択時は空文字を設定
        }

        // フォームの入力値を House エンティティにセット
        house.setName(houseRegisterForm.getName());
        house.setDescription(houseRegisterForm.getDescription());
        house.setPrice(houseRegisterForm.getPrice());
        house.setCapacity(houseRegisterForm.getCapacity());
        house.setPostalCode(houseRegisterForm.getPostalCode());
        house.setAddress(houseRegisterForm.getAddress());
        house.setPhoneNumber(houseRegisterForm.getPhoneNumber());

        // DBに新規登録保存
        houseRepository.save(house);
    }

    /**
     * 民宿情報の更新処理
     * @param houseEditForm 編集用フォームデータ
     */
    @Transactional // 6. 更新処理もトランザクション管理
    public void update(HouseEditForm houseEditForm) {
        // 更新対象の民宿データをIDで検索（存在しない場合は処理を中断）
        House house = houseRepository.findById(houseEditForm.getId()).orElse(null);
        if (house == null) {
            return;
        }

        // フォームの入力値でエンティティの基本情報を更新
        house.setName(houseEditForm.getName());
        house.setDescription(houseEditForm.getDescription());
        house.setPrice(houseEditForm.getPrice());
        house.setCapacity(houseEditForm.getCapacity());
        house.setPostalCode(houseEditForm.getPostalCode());
        house.setAddress(houseEditForm.getAddress());
        house.setPhoneNumber(houseEditForm.getPhoneNumber());

        // 7. 新しい画像ファイルがアップロードされた場合のみ画像を置き換え
        MultipartFile imageFile = houseEditForm.getImageFile();
        if (imageFile != null && !imageFile.isEmpty()) {
            String imageName = imageFile.getOriginalFilename();
            String hashedImageName = generateNewFileName(imageName);
            
            Path filePath = Paths.get("storage/" + hashedImageName);

            try {
                Files.createDirectories(filePath.getParent());
                Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                house.setImageName(hashedImageName); // 新しい画像ファイル名に更新
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // DBのレコードを更新保存
        houseRepository.save(house);
    }

    /**
     * ファイル名の重複を防ぐため、UUID を付与した一意なファイル名を生成するヘルパーメソッド
     * @param fileName 元のファイル名（例: "sample.jpg"）
     * @return リネーム後のファイル名（例: "550e8400-e29b-41d4-a716-446655440000.jpg"）
     */
    public String generateNewFileName(String fileName) {
        String uuid = UUID.randomUUID().toString();
        int extensionIndex = fileName.lastIndexOf(".");
        String extension = "";
        if (extensionIndex >= 0) {
            extension = fileName.substring(extensionIndex); // 拡張子（.jpg や .png など）を抽出
        }
        return uuid + extension;
    }
}