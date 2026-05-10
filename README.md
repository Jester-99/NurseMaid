# Nursemaid (智慧保姆) Android App

[![Android](https://img.shields.io/badge/Platform-Android-brightgreen.svg)](https://developer.android.com)
[![License](https://img.shields.io/badge/License-Proprietary-red.svg)](http://www.sinopulsar.com)

**Nursemaid** 是一款專為嬰幼兒照護設計的 Android 應用程式，由 **華星科技股份有限公司 (SinoPulsar Technology Inc.)** 開發。本應用程式需配合專屬的藍牙感測元件（Tag）使用，旨在提供即時的生理健康監測與預警功能。

## 🌟 主要功能 (Key Features)

- **即時溫度監測**：精準測量寶寶體溫，提供高溫與低溫警示。
- **呼吸頻率分析**：監測呼吸次數，當呼吸過於急促或緩慢時發出警報。
- **踢被通知**：偵測寶寶是否踢被，預防著涼。
- **吐奶偵測**：即時感應吐奶狀況並發出通知。
- **多樣化連線模式**：
  - **藍牙近端模式**：直接透過藍牙掃描並連結感測器。
  - **雲端推播模式**：登入帳號後，可透過 Wi-Fi 或行動網路接收遠端推播通知（結合 JPush 服務）。
- **生理數據圖表**：提供溫度曲線圖，方便家長追蹤長期健康趨勢。
- **設備管理**：支援多個感測器註冊與管理。

## 🛠 硬體需求 (Hardware Requirements)

本應用程式必須配合以下硬體方可正常運作：
1. **華星科技智慧保姆感測器 (Smart Baby Sensor Tag)**。
2. **藍牙模組 (Bluetooth Module)**。
3. 支援藍牙 4.0 (BLE) 以上的 Android 手機。

欲購買相關硬體設備，請至 [華星科技官方網站](http://www.sinopulsar.com) 訂購。

## 📱 軟體需求 (Software Requirements)

- **Android 版本**：建議 Android 5.0 (API 21) 或更高版本。
- **開發工具**：Android Studio / Gradle。
- **依賴庫**：
  - JPush SDK (用於遠端推播)
  - SQLite (本地資料儲存)

## 🚀 快速上手 (Getting Started)

1. **安裝 App**：編譯並安裝 APK 到您的 Android 設備。
2. **開啟電源**：將感測元件插入好後，開啟 Tag 電源。
3. **掃描設備**：在 App 中點擊「掃描」，搜尋附近的智慧保姆感測器。
4. **註冊/登入**：若需使用遠端推播功能，請註冊帳號並連結 Wi-Fi。
5. **開始監測**：連結成功後即可在主畫面看到溫度與呼吸數據。

## ⚠️ 安全性說明 (Security Note)

為了保護開發者資訊與應用程式安全，以下敏感檔案已在 Git 追蹤中排除：
- `*.jks` (Android 簽署金鑰)
- `keystore.properties`
- `local.properties`

如果您需要編譯 Release 版本，請自行準備簽署金鑰並配置環境變數。

## 📜 免責聲明 (Disclaimer)

本程式所量測的溫度與呼吸次數僅供參考。若有任何醫療疑慮，請務必諮詢專業醫護人員進行進一步的測溫與確認。

---
**開發商**: 華星科技股份有限公司 (SinoPulsar Technology Inc.)  
**官網**: [http://www.sinopulsar.com](http://www.sinopulsar.com)  
**版本**: v1.203
