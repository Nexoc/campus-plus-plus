import http from '@/app/api/http';
import type { Course } from '../model/Course';

export const coursesApi = {
  getAll(params?: { studyProgramId?: string; ects?: number }) {
    return http.get<Course[]>('/api/courses', { params })
  },

  getById(id: string) {
    return http.get<Course>(`/api/courses/${id}`)
  },

  create(course: Course) {
    return http.post('/api/courses', course)
  },

  update(id: string, course: Course) {
    return http.put(`/api/courses/${id}`, course)
  },

  remove(id: string) {
    return http.delete(`/api/courses/${id}`)
  },
}
