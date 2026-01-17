// src/modules/moderation/model/Moderation.ts

/**
 * Moderation action types for resolving reports.
 */
export type ModerationAction = 'KEEP_VISIBLE' | 'EDIT' | 'DELETE'

/**
 * Request to resolve a report with a moderation action.
 */
export interface ResolveReportRequest {
  action: ModerationAction
  moderatorNotes?: string
  editedReviewText?: string  // Required for EDIT action
}

/**
 * Report with review summary for moderation view.
 */
export interface ModerationReport {
  reportId: string
  targetType: string
  targetId: string
  reason: string
  comment?: string
  status: string
  createdAt: string
  moderatorNotes?: string
  
  // Reporter user info
  userId?: string
  userName?: string
  
  // Review summary
  reviewId?: string
  rating?: number
  reviewText?: string
  courseId?: string
  courseName?: string
  reviewerNickname?: string
  moderationFlagged?: boolean
}

/**
 * Labels for moderation actions.
 */
export const ModerationActionLabels: Record<ModerationAction, string> = {
  KEEP_VISIBLE: 'Keep Visible',
  EDIT: 'Edit Review',
  DELETE: 'Delete Review'
}

/**
 * Labels for report reasons.
 */
export const ReportReasonLabels: Record<string, string> = {
  SPAM: 'Spam or advertising',
  OFFENSIVE: 'Offensive or harassing',
  INAPPROPRIATE_LANGUAGE: 'Inappropriate language',
  MISLEADING_INFORMATION: 'Misleading information',
  OTHER: 'Other violation'
}
