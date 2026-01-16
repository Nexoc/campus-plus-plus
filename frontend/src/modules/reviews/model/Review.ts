// src/modules/reviews/model/Review.ts

export interface Review {
  reviewId?: string
  courseId: string
  userId: string
  userName?: string
  rating: number
  title: string
  content: string
  helpful: number
  notHelpful: number
  createdAt?: string
  updatedAt?: string
  isModerationFlagged?: boolean
  moderationReason?: string
}

export interface CreateReviewRequest {
  courseId: string
  rating: number
  title: string
  content: string
}

export interface UpdateReviewRequest {
  rating: number
  title: string
  content: string
}
