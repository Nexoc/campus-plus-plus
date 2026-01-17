// src/modules/reports/api/reportsApi.ts

import http from '@/app/api/http'
import type { CreateReportRequest, Report } from '../model/Report'

export const reportsApi = {
  /**
   * Submit a new report for inappropriate content.
   * Requires authentication.
   * 
   * @param data Report submission data
   * @returns Created report
   */
  create(data: CreateReportRequest) {
    return http.post<Report>('/api/reports', data)
  }
}
