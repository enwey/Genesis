<template>
  <div class="settings-container">
    <el-card>
      <template #header>系統關鍵參數配置</template>
      <el-form :model="form" label-width="150px">
        <el-form-item label="Meshy API Key">
          <el-input v-model="form.meshy_key" type="password" show-password />
        </el-form-item>
        <el-form-item label="OpenAI API Key">
          <el-input v-model="form.openai_key" type="password" show-password />
        </el-form-item>
        <el-divider />
        <el-form-item label="單次生成消耗 Mana">
          <el-input-number v-model="form.mana_cost" :min="1" />
        </el-form-item>
        <el-form-item label="集市交易稅率 (%)">
          <el-input-number v-model="form.tax_rate" :min="0" :max="100" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="onSubmit">保存全局配置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { reactive } from 'vue'
import { ElMessage } from 'element-plus'

const form = reactive({
  meshy_key: '',
  openai_key: '',
  mana_cost: 50,
  tax_rate: 10
})

const onSubmit = () => {
  // 這裡在生產環境會調用 /api/v1/settings 更新 Redis/DB 中的動態配置
  ElMessage.success('全局配置已更新，即時生效')
}
</script>
