// src/modules/reviews/model/Review.ts

export interface Review {
  reviewId?: string
  courseId: string
  userId: string
  userName?: string
  rating: number
  difficulty?: number
  workload?: number
  satisfaction?: number
  priorRequirements?: string
  examInfo?: string
  text?: string
  createdAt?: string
  updatedAt?: string
}

export interface ReviewSummary {
  averageRating: number | null
  reviewCount: number
}

export interface CreateReviewRequest {
  courseId: string
  rating: number
  difficulty?: number
  workload?: number
  satisfaction?: number
  priorRequirements?: string
  examInfo?: string
  text?: string
}

export interface UpdateReviewRequest {
  rating?: number
  difficulty?: number
  workload?: number
  satisfaction?: number
  priorRequirements?: string
  examInfo?: string
  text?: string
}
