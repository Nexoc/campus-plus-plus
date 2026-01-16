// src/modules/favourites/model/StudyProgramFavourite.ts

export interface StudyProgramFavourite {
  studyProgramId: string
  studyProgramName: string
  studyProgramDescription?: string
  degree?: string
  semesters?: number
  totalEcts?: number
  createdAt: string
}

export interface AddStudyProgramFavouriteRequest {
  studyProgramId: string
}
