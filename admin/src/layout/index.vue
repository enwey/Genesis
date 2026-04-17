<template>
  <el-container class="layout-container">
    <el-aside width="200px">
      <el-menu
        :default-active="route.path"
        router
        class="el-menu-vertical"
      >
        <el-menu-item index="/dashboard">
          <el-icon><Odometer /></el-icon>
          <span>控制面板</span>
        </el-menu-item>
        <el-menu-item index="/users">
          <el-icon><User /></el-icon>
          <span>用戶管理</span>
        </el-menu-item>
        <el-menu-item index="/assets">
          <el-icon><Picture /></el-icon>
          <span>資產審核</span>
        </el-menu-item>
        <el-menu-item index="/settings">
          <el-icon><Setting /></el-icon>
          <span>系統配置</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header>
        <div class="header-content">
          <span>Genesis Admin Panel</span>
          <el-button type="text" @click="handleLogout">退出登錄</el-button>
        </div>
      </el-header>
      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { useRoute, useRouter } from 'vue-router'
const route = useRoute()
const router = useRouter()

const handleLogout = () => {
  router.push('/login')
}
</script>

<style scoped>
.layout-container {
  height: 100vh;
}
.el-header {
  background-color: #fff;
  border-bottom: 1px solid #dcdfe6;
  line-height: 60px;
}
.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.el-aside {
  background-color: #304156;
}
.el-menu {
  border-right: none;
}
.el-menu-vertical {
  background-color: #304156;
}
:deep(.el-menu-item) {
  color: #bfcbd9;
}
:deep(.el-menu-item.is-active) {
  color: #409eff;
  background-color: #263445;
}
</style>
