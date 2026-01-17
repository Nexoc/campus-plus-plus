import http from '@/app/api/http';
import type { Course } from '../model/Course';

export interface PagedResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}

export const coursesApi = {
  getAll(params?: { studyProgramId?: string; ects?: number; page?: number; size?: number; sort?: string }) {
    return http.get<PagedResponse<Course>>('/api/public/courses', { params })
  },

  getById(id: string) {
    return http.get<Course>(`/api/public/courses/${id}`)
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
