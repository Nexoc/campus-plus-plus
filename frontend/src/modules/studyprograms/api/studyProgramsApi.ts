import http from '@/app/api/http'
import type { StudyProgram } from '../model/StudyProgram'

export const studyProgramsApi = {
  getAll() {
    return http.get<StudyProgram[]>('/api/public/study-programs')
  },

  getById(id: string) {
    return http.get<StudyProgram>(`/api/public/study-programs/${id}`)
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
