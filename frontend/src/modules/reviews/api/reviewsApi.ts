// src/modules/reviews/api/reviewsApi.ts

import http from '@/app/api/http'
import type { Review, ReviewSummary, CreateReviewRequest, UpdateReviewRequest } from '../model/Review'

export const reviewsApi = {
  // Public endpoints (read-only)
  getByCourse(courseId: string, sort?: string) {
    return http.get<Review[]>(`/api/public/courses/${courseId}/reviews`, {
      params: sort ? { sort } : undefined
    })
  },

  getById(reviewId: string) {
    return http.get<Review>(`/api/public/reviews/${reviewId}`)
  },

  getSummary(courseId: string) {
    return http.get<ReviewSummary>(`/api/public/courses/${courseId}/reviews/summary`)
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

  // Moderation endpoints (ADMIN role only)
  moderationGetAll(filters?: { courseId?: string; flaggedOnly?: boolean }) {
    return http.get<Review[]>('/api/moderation/reviews', { params: filters })
  },

  moderationFlag(reviewId: string, reason: string) {
    return http.post(`/api/moderation/reviews/${reviewId}/flag`, { reason })
  },

  moderationUnflag(reviewId: string) {
    return http.post(`/api/moderation/reviews/${reviewId}/unflag`)
  },

  moderationDelete(reviewId: string) {
    return http.delete(`/api/moderation/reviews/${reviewId}`)
  },
}
