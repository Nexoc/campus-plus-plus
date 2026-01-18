import http from '@/app/api/http'
import type { ReactionCount } from '@/shared/model/Reaction'

const API_BASE = import.meta.env.VITE_API_BASE_URL || '/api'
const PUBLIC_API = `${API_BASE}/public`

export const reactionApi = {
  // Get reactions for a post
  async getPostReactions(postId: string): Promise<ReactionCount> {
    const response = await http.get(`${PUBLIC_API}/posts/${postId}/reactions`)
    return response.data
  },

  // Add reaction to a post
  async addPostReaction(postId: string): Promise<void> {
    await http.post(`${API_BASE}/posts/${postId}/reactions`, {})
  },

  // Remove reaction from a post
  async removePostReaction(postId: string): Promise<void> {
    await http.delete(`${API_BASE}/posts/${postId}/reactions`)
  },

  // Get reactions for a review
  async getReviewReactions(reviewId: string): Promise<ReactionCount> {
    const response = await http.get(`${PUBLIC_API}/reviews/${reviewId}/reactions`)
    return response.data
  },

  // Add reaction to a review
  async addReviewReaction(reviewId: string): Promise<void> {
    await http.post(`${API_BASE}/reviews/${reviewId}/reactions`, {})
  },

  // Remove reaction from a review
  async removeReviewReaction(reviewId: string): Promise<void> {
    await http.delete(`${API_BASE}/reviews/${reviewId}/reactions`)
  }
}
