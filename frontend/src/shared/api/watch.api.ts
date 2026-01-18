import http from '@/app/api/http'
import type { WatchDto, WatchRequest } from '@/shared/model/Watch'

const API_BASE = import.meta.env.VITE_API_BASE_URL || '/api'
const PUBLIC_API = `${API_BASE}/public`

export const watchApi = {
  // Watch a target
  async watch(request: WatchRequest): Promise<WatchDto> {
    const response = await http.post(`${API_BASE}/watch`, request)
    return response.data
  },

  // Unwatch a target
  async unwatch(request: Pick<WatchRequest, 'targetType' | 'targetId'>): Promise<void> {
    await http.delete(`${API_BASE}/watch`, { data: request })
  },

  // Get watch status for a target
  async getWatchStatus(targetType: 'COURSE' | 'THREAD', targetId: string): Promise<WatchDto> {
    const response = await http.get(`${PUBLIC_API}/watch/status`, {
      params: { targetType, targetId }
    })
    return response.data
  },

  // Get user's watch list
  async getWatchList(): Promise<WatchDto[]> {
    const response = await http.get(`${API_BASE}/watch`)
    return response.data
  }
}
