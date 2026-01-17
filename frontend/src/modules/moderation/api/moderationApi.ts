// src/modules/moderation/api/moderationApi.ts

import http from '@/app/api/http'
import type { ModerationReport, ResolveReportRequest } from '../model/Moderation'

/**
 * API client for moderation operations.
 * All endpoints require MODERATOR role.
 */
export const moderationApi = {
  /**
   * List reports by status.
   * FR-M-16: List all open reports.
   * 
   * @param status Report status (PENDING, RESOLVED, REJECTED)
   * @returns List of reports with review summaries
   */
  async getReports(status: string = 'PENDING'): Promise<ModerationReport[]> {
    const response = await http.get<ModerationReport[]>('/api/moderation/reports', {
      params: { status }
    })
    return response.data
  },

  /**
   * Resolve a report with a moderation action.
   * FR-M-17: Allow Moderators to change the status of a reported review.
   * 
   * @param reportId The ID of the report to resolve
   * @param request The moderation decision (action and optional notes)
   */
  async resolveReport(reportId: string, request: ResolveReportRequest): Promise<void> {
    await http.post(`/api/moderation/reports/${reportId}/resolve`, request)
  },

  /**
   * Get count of pending reports.
   * FR-S-4: Display open reports count badge.
   * 
   * @returns Number of pending reports
   */
  async getPendingCount(): Promise<number> {
    const response = await http.get<number>('/api/moderation/reports/count/pending')
    return response.data
  }
}
