<template>
  <div class="dashboard-container">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card shadow="hover">
          <template #header>在線用戶</template>
          <div class="card-value">1,284</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <template #header>今日 AI 生成</template>
          <div class="card-value">452</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <template #header>Mana 流水</template>
          <div class="card-value">85,000</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <template #header>今日營收</template>
          <div class="card-value">$420.00</div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<style scoped>
.card-value {
  font-size: 24px;
  font-weight: bold;
  text-align: center;
}
</style>
