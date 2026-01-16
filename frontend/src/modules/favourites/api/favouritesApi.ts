// src/modules/favourites/api/favouritesApi.ts

import http from '@/app/api/http'
import type { Favourite, AddFavouriteRequest } from '../model/Favourite'
import type { StudyProgramFavourite, AddStudyProgramFavouriteRequest } from '../model/StudyProgramFavourite'

export const favouritesApi = {
  /**
   * Get all favourites for the authenticated user
   */
  getAll() {
    return http.get<Favourite[]>('/api/favourites')
  },

  /**
   * Add a course to favourites
   */
  add(request: AddFavouriteRequest) {
    return http.post('/api/favourites', request)
  },

  /**
   * Remove a course from favourites
   */
  remove(courseId: string) {
    return http.delete(`/api/favourites/${courseId}`)
  },

  /**
   * Get all study program favourites for the authenticated user
   */
  getAllStudyPrograms() {
    return http.get<StudyProgramFavourite[]>('/api/favourites/study-programs')
  },

  /**
   * Add a study program to favourites
   */
  addStudyProgram(request: AddStudyProgramFavouriteRequest) {
    return http.post('/api/favourites/study-programs', request)
  },

  /**
   * Remove a study program from favourites
   */
  removeStudyProgram(studyProgramId: string) {
    return http.delete(`/api/favourites/study-programs/${studyProgramId}`)
  },
}
