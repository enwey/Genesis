# Genesis AIGC MMO Project

This project consists of an Android client, a FastAPI backend server, and a Vue 3 admin dashboard.

## 1. Backend Server (FastAPI)

### Prerequisites
- Docker and Docker Compose

### Start the Server
```bash
cd server
docker-compose up --build
```
The API will be available at `http://localhost:8000`.
Swagger documentation: `http://localhost:8000/docs`.

### Database Migrations (Run inside container)
```bash
docker-compose exec api alembic revision --autogenerate -m "Initial migration"
docker-compose exec api alembic upgrade head
```

## 2. Admin Dashboard (Vue 3 + Element Plus)

### Prerequisites
- Node.js (v18+)
- npm or pnpm

### Start the Dashboard
```bash
cd admin
npm install
npm run dev
```
The dashboard will be available at `http://localhost:3000`.

## 3. Android Client (Kotlin + Jetpack Compose)

Open the `android` directory in Android Studio to build and run the application.

### Key Features
- **AIGC Integration**: Text-to-3D generation.
- **Native 3D Rendering**: Using SceneView (Filament).
- **MMO Engine**: WebSocket real-time synchronization.
- **Economy**: Google Play Billing (IAP) for Mana.
- **Social**: Like, Favorite, and Feed system.
