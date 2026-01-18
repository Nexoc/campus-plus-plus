export interface WatchDto {
  id: string | null
  targetType: 'COURSE' | 'THREAD'
  targetId: string
  notificationsEnabled: boolean
  isWatching: boolean
}

export interface WatchRequest {
  targetType: 'COURSE' | 'THREAD'
  targetId: string
  notificationsEnabled: boolean
}
