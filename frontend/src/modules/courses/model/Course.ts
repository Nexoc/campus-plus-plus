export type RichBlock =
  | {
      type: 'text'
      content: string
    }
  | {
      type: 'list'
      items: string[]
      ordered?: boolean
    }

export interface Course {
  courseId?: string
  title: string
  description: string
  ects: number
  abbreviation: string
  language: string
  sws?: number
  semester?: number
  kind?: string
  detailsHtml?: string
  content?: RichBlock[]
  learningOutcomes?: RichBlock[]
  teachingMethod?: RichBlock[]
  examMethod?: RichBlock[]
  literature?: RichBlock[]
  teachingLanguage?: RichBlock[]
  sourceUrl?: string
}
