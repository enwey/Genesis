import axios from 'axios'

export function getAssets(params) {
  return axios.get('/api/v1/assets/', { params })
}

export function updateAssetStatus(id, status) {
  return axios.put(`/api/v1/assets/${id}/status`, { status })
}
