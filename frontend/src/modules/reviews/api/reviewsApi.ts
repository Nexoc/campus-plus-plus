// src/modules/reviews/api/reviewsApi.ts

import http from '@/app/api/http'
import type { Review, CreateReviewRequest, UpdateReviewRequest } from '../model/Review'

export const reviewsApi = {
  // Public endpoints (read-only)
  getByCourse(courseId: string) {
    return http.get<Review[]>(`/api/public/courses/${courseId}/reviews`)
  },

  getById(reviewId: string) {
    return http.get<Review>(`/api/public/reviews/${reviewId}`)
  },

  // Protected endpoints (authenticated users)
  create(courseId: string, data: CreateReviewRequest) {
    return http.post<Review>('/api/reviews', { ...data, courseId })
  },

  update(reviewId: string, data: UpdateReviewRequest) {
    return http.put<Review>(`/api/reviews/${reviewId}`, data)
  },

  delete(reviewId: string) {
    return http.delete(`/api/reviews/${reviewId}`)
  },
}
