<template>
  <div class="assets-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>資產審核隊列</span>
          <el-radio-group v-model="statusFilter" size="small" @change="fetchData">
            <el-radio-button label="">全部</el-radio-button>
            <el-radio-button label="pending">待審核</el-radio-button>
            <el-radio-button label="approved">已通過</el-radio-button>
            <el-radio-button label="rejected">已拒絕</el-radio-button>
          </el-radio-group>
        </div>
      </template>

      <el-table :data="tableData" v-loading="loading" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column label="預覽圖" width="120">
          <template #default="scope">
            <el-image 
              :src="scope.row.preview_url" 
              :preview-src-list="[scope.row.preview_url]"
              fit="cover"
              style="width: 80px; height: 80px; border-radius: 4px"
            />
          </template>
        </el-table-column>
        <el-table-column prop="prompt" label="提示詞" />
        <el-table-column prop="status" label="狀態" width="100">
          <template #default="scope">
            <el-tag :type="statusType(scope.row.status)">{{ scope.row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="scope">
            <div v-if="scope.row.status === 'pending'">
              <el-button size="small" type="success" @click="handleApprove(scope.row)">通過</el-button>
              <el-button size="small" type="danger" @click="handleReject(scope.row)">拒絕</el-button>
            </div>
            <el-button v-else size="small" type="info" disabled>已處理</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getAssets, updateAssetStatus } from '@/api/asset'
import { ElMessage } from 'element-plus'

const tableData = ref([])
const loading = ref(false)
const statusFilter = ref('pending')

const fetchData = async () => {
  loading.ref = true
  try {
    const res = await getAssets({ status: statusFilter.value })
    tableData.value = res.data
  } catch (error) {
    ElMessage.error('獲取數據失敗')
  } finally {
    loading.value = false
  }
}

const handleApprove = async (row) => {
  try {
    await updateAssetStatus(row.id, 'approved')
    ElMessage.success('資產已批准')
    fetchData()
  } catch (error) {
    ElMessage.error('操作失敗')
  }
}

const handleReject = async (row) => {
  try {
    await updateAssetStatus(row.id, 'rejected')
    ElMessage.warning('資產已拒絕')
    fetchData()
  } catch (error) {
    ElMessage.error('操作失敗')
  }
}

const statusType = (status) => {
  const map = { pending: 'warning', approved: 'success', rejected: 'danger' }
  return map[status] || 'info'
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
