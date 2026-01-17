import http from '@/app/api/http'
import type { StudyProgram } from '../model/StudyProgram'

export const studyProgramsApi = {
  getAll(params?: { page?: number; size?: number; sort?: string }) {
    return http.get<{ content: StudyProgram[]; totalElements: number }>('/api/public/study-programs', { params })
  },

  getById(id: string) {
    return http.get<StudyProgram>(`/api/public/study-programs/${id}`)
  },

  getDetails(id: string) {
    return http.get(`/api/public/study-programs/${id}/details`)
  },

  create(program: StudyProgram) {
    return http.post('/api/study-programs', program)
  },

  update(id: string, program: StudyProgram) {
    return http.put(`/api/study-programs/${id}`, program)
  },

  remove(id: string) {
    return http.delete(`/api/study-programs/${id}`)
  },
}
