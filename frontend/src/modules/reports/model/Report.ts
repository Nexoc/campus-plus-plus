// src/modules/reports/model/Report.ts

export interface Report {
  id?: string
  targetType: string
  targetId: string
  userId?: string
  reason: string
  comment?: string
  status?: string
  createdAt?: string
  resolvedAt?: string
  moderatorNotes?: string
}

export type ReportReason = 'SPAM' | 'OFFENSIVE' | 'INAPPROPRIATE_LANGUAGE' | 'MISLEADING_INFORMATION' | 'OTHER'

export interface CreateReportRequest {
  targetType: string
  targetId: string
  reason: ReportReason
  comment?: string
}

export const ReportReasonLabels: Record<ReportReason, string> = {
  'SPAM': 'Spam or advertising',
  'OFFENSIVE': 'Offensive or harassing',
  'INAPPROPRIATE_LANGUAGE': 'Inappropriate language',
  'MISLEADING_INFORMATION': 'Misleading or false information',
  'OTHER': 'Other (please explain in comment)'
}
