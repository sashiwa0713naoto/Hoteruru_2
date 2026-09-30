package com.example.moattravel.service; // 民宿情報の新規登録や更新、画像ファイルの保存、重複を防ぐためのファイル名生成といった処理をまとめて行うサービスクラス。

import java.io.IOException; // 入出力エラーを扱うためのクラス
import java.nio.file.Files; // ファイルやフォルダの作成・コピーを行うためのクラス
import java.nio.file.Path; // ファイルの場所（パス）を扱うためのクラス
import java.nio.file.Paths; // パスオブジェクトを簡単に作成するためのクラス
import java.nio.file.StandardCopyOption; // ファイルをコピーするときの上書き設定などを行うための定数
import java.util.UUID; // 重複しない一意なファイル名をランダムに生成するための仕組み

import org.springframework.stereotype.Service; // Springにこのクラスがサービス層の部品であることを伝えるアノテーション
import org.springframework.transaction.annotation.Transactional; // 処理途中でエラーが起きたらデータベースの変更を自動で元に戻す仕組み
import org.springframework.web.multipart.MultipartFile; // 画面からアップロードされた画像ファイルを受け取るためのクラス

import com.example.moattravel.entity.House; // 民宿データをデータベースのテーブルと対応付けるためのクラス
import com.example.moattravel.form.HouseEditForm; // 民宿編集画面からの入力データを受け取るためのフォーム
import com.example.moattravel.form.HouseRegisterForm; // 民宿新規登録画面からの入力データを受け取るためのフォーム
import com.example.moattravel.repository.HouseRepository; // 民宿データのデータベース操作を行う仕組み

@Service // このクラスをSpringの部品（サービス）として登録する
public class HouseService { // 民宿に関する処理（登録や更新など）をまとめて実行するクラス
    private final HouseRepository houseRepository; // 民宿データをデータベースに保存・取得するための仕組み

    public HouseService(HouseRepository houseRepository) { // データベース操作用の仕組みを受け取るコンストラクタ
        this.houseRepository = houseRepository;
    }

    @Transactional // 処理の途中でエラーが発生した場合、データベースの変更をすべて自動で元に戻す
    public void create(HouseRegisterForm houseRegisterForm) { // 民宿の新しいデータをデータベースに登録するメソッド
        House house = new House(); // 新しい民宿データの入れ物を作る
        MultipartFile imageFile = houseRegisterForm.getImageFile(); // フォームから画像ファイルを取り出す

        if (imageFile != null && !imageFile.isEmpty()) { // 画像ファイルが選択されてアップロードされている場合
            String imageName = imageFile.getOriginalFilename(); // アップロードされた元のファイル名を取得する
            String hashedImageName = generateNewFileName(imageName); // 重複を防ぐために新しいファイル名に変更する
            
            Path filePath = Paths.get("storage/" + hashedImageName); // 画像を保存するフォルダとファイル名を指定する

            try {
                Files.createDirectories(filePath.getParent()); // 保存先のフォルダが存在しない場合は自動で作成する
                Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING); // 画像ファイルを指定した場所に保存する
                house.setImageName(hashedImageName); // 保存した画像ファイルの名前を民宿データにセットする
            } catch (IOException e) {
                e.printStackTrace(); // エラーが発生した場合は詳細をコンソールに出力する
            }
        } else {
            house.setImageName(""); // 画像が選択されていない場合は空欄にする
        }

        house.setName(houseRegisterForm.getName()); // フォームから受け取った民宿名をセットする
        house.setDescription(houseRegisterForm.getDescription()); // フォームから受け取った説明文をセットする
        house.setPrice(houseRegisterForm.getPrice()); // フォームから受け取った宿泊料金をセットする
        house.setCapacity(houseRegisterForm.getCapacity()); // フォームから受け取った定員をセットする
        house.setPostalCode(houseRegisterForm.getPostalCode()); // フォームから受け取った郵便番号をセットする
        house.setAddress(houseRegisterForm.getAddress()); // フォームから受け取った住所をセットする
        house.setPhoneNumber(houseRegisterForm.getPhoneNumber()); // フォームから受け取った電話番号をセットする

        houseRepository.save(house); // 作成した民宿データをデータベースに保存する
    }

    @Transactional // 処理の途中でエラーが発生した場合、データベースの変更をすべて自動で元に戻す
    public void update(HouseEditForm houseEditForm) { // 既存の民宿データを更新するメソッド
        House house = houseRepository.findById(houseEditForm.getId()).orElse(null); // 編集対象の民宿データをIDで探す
        if (house == null) { // 該当する民宿データが見つからない場合
            return; // 処理をここで終了する
        }

        house.setName(houseEditForm.getName()); // 新しい民宿名をセットする
        house.setDescription(houseEditForm.getDescription()); // 新しい説明文をセットする
        house.setPrice(houseEditForm.getPrice()); // 新しい宿泊料金をセットする
        house.setCapacity(houseEditForm.getCapacity()); // 新しい定員をセットする
        house.setPostalCode(houseEditForm.getPostalCode()); // 新しい郵便番号をセットする
        house.setAddress(houseEditForm.getAddress()); // 新しい住所をセットする
        house.setPhoneNumber(houseEditForm.getPhoneNumber()); // 新しい電話番号をセットする

        MultipartFile imageFile = houseEditForm.getImageFile(); // フォームから新しい画像ファイルを取り出す
        if (imageFile != null && !imageFile.isEmpty()) { // 新しい画像ファイルがアップロードされている場合のみ実行
            String imageName = imageFile.getOriginalFilename(); // アップロードされた元のファイル名を取得する
            String hashedImageName = generateNewFileName(imageName); // 新しい一意なファイル名を生成する
            
            Path filePath = Paths.get("storage/" + hashedImageName); // 画像を保存するフォルダとファイル名を指定する

            try {
                Files.createDirectories(filePath.getParent()); // 保存先のフォルダが存在しない場合は自動で作成する
                Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING); // 新しい画像ファイルを保存する
                house.setImageName(hashedImageName); // 新しい画像ファイル名に更新する
            } catch (IOException e) {
                e.printStackTrace(); // エラーが発生した場合は詳細をコンソールに出力する
            }
        }

        houseRepository.save(house); // 変更をデータベースに保存して更新する
    }

    public String generateNewFileName(String fileName) { // ファイル名の重複を防ぐため、ランダムなIDを組み合わせて新しいファイル名を作るメソッド
        String uuid = UUID.randomUUID().toString(); // ランダムな文字列（UUID）を生成する
        int extensionIndex = fileName.lastIndexOf("."); // ファイル名から拡張子の位置を探す
        String extension = "";
        if (extensionIndex >= 0) {
            extension = fileName.substring(extensionIndex); // ファイルの拡張子（.jpgや.pngなど）を取り出す
        }
        return uuid + extension; // ランダムな文字列と拡張子をくっつけた新しいファイル名を返す
    }
}