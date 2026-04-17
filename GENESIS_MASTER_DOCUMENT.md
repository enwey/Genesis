# 🎨 Genesis: The Blank Canvas 項目全棧說明文檔

> **版本**：v1.0.0-Stable (Production Ready)  
> **定位**：AIGC 驅動的多人創作社交平臺 (MMO-UGC)  
> **願景**：讓每一位用戶通過一句話（Prompt）創造 3D 世界，並在去中心化的集市中分享與獲利。

---

## 一、 項目架構 (Architecture)

本項目採用 **MAD (Modern Android Development)** 標準與 **微服務化後端** 設計，確保了系統的高性能、高併發與生產級穩定性。

### 1. 核心技術棧
*   **Android 客戶端**:
    *   **UI 框架**: Jetpack Compose (全聲明式 UI 佈局)。
    *   **3D 渲染**: `io.github.sceneview` (基於 Google Filament)，原生 PBR 物理渲染引擎。
    *   **依賴注入**: Hilt (Dagger)。
    *   **異步處理**: Kotlin Coroutines & Flow (響應式編程)。
    *   **網絡/緩存**: Retrofit + OkHttp + Room Database (離線優先策略)。
*   **FastAPI 服務端**:
    *   **語言/框架**: Python 3.11+ / FastAPI (基於 Starlette 的高性能異步框架)。
    *   **數據庫**: PostgreSQL + **pgvector** (支持 AI 3D 模型特徵向量搜索)。
    *   **緩存/消息**: Redis (用於 WebSocket 狀態管理、任務隊列與限流)。
    *   **認證**: JWT (JSON Web Token) 安全體系。
*   **Vue 3 Admin 管理後臺**:
    *   **框架**: Vue 3 + Vite + Element Plus。
    *   **功能**: 資產審核流水線、全服經濟監控、AI API 密鑰動態配置。

---

## 二、 核心業務邏輯 (Business Logic)

### 1. 創世循環 (Creation Loop)
1.  **提交 Prompt**：用戶在 Android 端輸入描述（如：“Low-Poly 風格的賽博朋克路燈”）。
2.  **前置過濾**：集成 OpenAI Moderation API 進行文本審核，攔截違規輸入。
3.  **Mana 消耗**：後端自動扣除用戶 **50 Mana**。
4.  **異步生成**：調用 Meshy/Luma API 進入異步任務隊列，避免前端等待。
5.  **實時通知**：生成完成後，服務端通過 **WebSocket** 實時推送 JSON 消息，觸發客戶端彈窗通知。

### 2. 經濟與交易系統
*   ** Mana (法力值)**：通過 Google Play 內購（IAP）獲取，是調用 AI 生成能力的唯一憑證。
*   **StarCoin (星幣)**：遊戲內流通貨幣，用於玩家間購買 3D 資產。
*   **平臺分潤**：集市交易中，創作者獲得 **90%** 的 StarCoin，平臺抽取 **10%** 交易稅。

### 3. 資產狀態機
*   `PENDING` (待審核)：AI 生成後，僅創作者可見。
*   `APPROVED` (已批准)：出現在大集市，面向全服用戶。
*   `REJECTED` (已駁回)：因合規問題被管理員攔截，不予展示。

---

## 三、 玩法介紹 (Gameplay)

1.  **創世 (Create)**：在創作面板揮灑靈感，觀察 3D 資產從雲端到本地的原生渲染效果。
2.  **空間交互**：支持雙指縮放、單指旋轉，全方位檢視 AI 生成的精密模型。
3.  **社交動態 (Social)**：在動態流中探索全球創作者的作品，支持點贊與收藏。
4.  **大集市 (The Bazaar)**：購買心儀的資產裝點自己的空間，或通過出售創意賺取收益。

---

## 四、 啟動「保姆級」教程 (Getting Started)

### 1. 環境要求
*   **Docker & Docker Compose** (用於一鍵部署後端環境)。
*   **Node.js** (v18+) (用於運行管理後臺)。
*   **Android Studio (Hedgehog+)** (用於開發客戶端)。

### 2. 第一步：部署後端 (Server)
1.  進入 `server` 目錄。
2.  配置環境變量：複製 `.env.example` 為 `.env` 並填入 API Key。
3.  啟動 Docker 容器：
    ```bash
    docker-compose up --build -d
    ```
4.  執行數據庫遷移：
    ```bash
    docker-compose exec api alembic upgrade head
    ```

### 3. 第二步：啟動管理後臺 (Admin)
1.  進入 `admin` 目錄。
2.  安裝依賴：`npm install`。
3.  運行開發服務：`npm run dev`。
4.  瀏覽器訪問 `http://localhost:3000` 即可進入登錄頁。

### 4. 第三步：運行 Android 客戶端
1.  在 Android Studio 中打開 `android` 目錄。
2.  等待 Gradle 編譯與依賴下載。
3.  點擊 **Run** 運行至模擬器（App 已預設配置指向 `10.0.2.2` 訪問本機後端）。

---

## 五、 生產級上線檢查清單 (Launch Checklist)

- [ ] **SSL 安全**：上線前必須將 `usesCleartextTraffic` 設為 `false` 並配置 HTTPS。
- [ ] **對象存儲**：將 `.glb` 文件存儲從本地 Mock 更新為 Cloudflare R2 或 AWS S3。
- [ ] **支付配置**：在 Google Play Console 創建 `mana_pack_1000` 商品 ID。
- [ ] **性能監控**：建議集成 Firebase Crashlytics 進行崩潰追蹤。
- [ ] **混淆加固**：使用 R8 對代碼進行混淆，防止 API 調用邏輯被反編譯。

---

**Genesis 項目組**  
*讓創意無界，讓世界新生。*
