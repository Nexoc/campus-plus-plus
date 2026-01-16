// src/modules/favourites/model/Favourite.ts

export interface Favourite {
  courseId: string
  courseTitle: string
  courseDescription: string | null
  courseEcts: number | null
  createdAt: string
}

export interface AddFavouriteRequest {
  courseId: string
}
