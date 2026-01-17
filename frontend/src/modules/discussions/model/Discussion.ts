// src/modules/discussions/model/Discussion.ts

export interface Thread {
  id: string
  courseId: string
  title: string
  content?: string
  createdBy: string
  createdByName?: string
  createdAt: string
  postCount?: number
}

export interface Post {
  id: string
  threadId: string
  userId: string
  userName?: string
  content: string
  createdAt: string
  updatedAt: string
  commentCount?: number
}

export interface Comment {
  id: string
  postId: string
  userId: string
  userName?: string
  content: string
  createdAt: string
  updatedAt: string
}
