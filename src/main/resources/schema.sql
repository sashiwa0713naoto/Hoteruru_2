CREATE TABLE IF NOT EXISTS houses (
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, -- ID（自動で1, 2, 3...と採番される重複不可の識別番号）
    name VARCHAR(50) NOT NULL,                    -- 物件の名前（空欄不可、最大50文字）
    image_name VARCHAR(255),                      -- 画像のファイル名やパス（空欄可、最大255文字）
    description VARCHAR(255) NOT NULL,            -- 物件の説明文（空欄不可、最大255文字）
    price INT NULL,                               -- 価格・料金（未設定でも登録可能）
    capacity INT NOT NULL,                        -- 定員・収容人数（空欄不可）
    postal_code VARCHAR(50) NOT NULL,             -- 郵便番号（空欄不可）
    phone_number VARCHAR(50) NOT NULL,            -- 電話番号（空欄不可）
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, -- データが作成された日時（自動記録）
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP -- データが更新された日時（自動記録・変更時に自動更新）
);