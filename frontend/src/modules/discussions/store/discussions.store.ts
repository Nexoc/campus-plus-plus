// src/modules/discussions/store/discussions.store.ts

import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { Thread, Post, Comment } from '../model/Discussion'
import type { 
  CreateThreadRequest, 
  CreatePostRequest, 
  CreateCommentRequest, 
  UpdateThreadRequest,
  UpdatePostRequest, 
  UpdateCommentRequest 
} from '../api/discussionsApi'
import { discussionsApi } from '../api/discussionsApi'
import { logger } from '@/shared/utils/logger'

export const useDiscussionsStore = defineStore('discussions', () => {
  // State
  const currentCourseId = ref<string | null>(null)
  const threads = ref<Thread[]>([])
  const threadDetail = ref<Thread | null>(null)
  const posts = ref<Post[]>([])
  const postDetail = ref<Post | null>(null)
  const comments = ref<Comment[]>([])

  const loading = ref(false)
  const error = ref<string | null>(null)

  // ============================================================
  // THREAD ACTIONS
  // ============================================================

  async function loadThreads(courseId: string) {
    loading.value = true
    error.value = null
    try {
      currentCourseId.value = courseId
      const response = await discussionsApi.getThreadsByCourse(courseId)
      threads.value = response.data
      console.log('DEBUG: Raw response data:', response.data)
      console.log('DEBUG: First thread:', response.data[0])
      console.log('DEBUG: First thread ID:', response.data[0]?.id)
      logger.log(`Loaded ${threads.value.length} threads for course ${courseId}`)
    } catch (err: any) {
      error.value = err.response?.data?.message || 'Failed to load threads'
      logger.error('Failed to load threads', err)
    } finally {
      loading.value = false
    }
  }

  async function loadThreadDetail(threadId: string) {
    console.log('DEBUG Store: loadThreadDetail called with:', threadId)
    console.log('DEBUG Store: threadId type:', typeof threadId)
    loading.value = true
    error.value = null
    try {
      console.log('DEBUG Store: Calling API getThreadById with:', threadId)
      const response = await discussionsApi.getThreadById(threadId)
      console.log('DEBUG Store: API response:', response)
      threadDetail.value = response.data
      logger.log(`Loaded thread ${threadId}`)
    } catch (err: any) {
      console.error('DEBUG Store: API error:', err)
      error.value = err.response?.data?.message || 'Failed to load thread'
      logger.error('Failed to load thread', err)
    } finally {
      loading.value = false
    }
  }

  async function createThread(courseId: string, data: CreateThreadRequest) {
    error.value = null
    try {
      const response = await discussionsApi.createThread(courseId, data)
      threads.value.unshift(response.data)
      logger.log(`Created thread in course ${courseId}`)
      return response.data
    } catch (err: any) {
      error.value = err.response?.data?.message || 'Failed to create thread'
      logger.error('Failed to create thread', err)
      throw error.value
    }
  }

  async function updateThread(threadId: string, data: UpdateThreadRequest) {
    error.value = null
    try {
      const response = await discussionsApi.updateThread(threadId, data)
      
      // Update in local state
      const index = threads.value.findIndex(t => t.id === threadId)
      if (index !== -1) {
        threads.value[index] = response.data
      }
      
      if (threadDetail.value?.id === threadId) {
        threadDetail.value = response.data
      }
      
      logger.log(`Updated thread ${threadId}`)
      return response.data
    } catch (err: any) {
      error.value = err.response?.data?.message || 'Failed to update thread'
      logger.error('Failed to update thread', err)
      throw error.value
    }
  }

  async function deleteThread(threadId: string) {
    error.value = null
    try {
      await discussionsApi.deleteThread(threadId)
      
      // Remove from local state
      threads.value = threads.value.filter(t => t.id !== threadId)
      if (threadDetail.value?.id === threadId) {
        threadDetail.value = null
      }
      
      logger.log(`Deleted thread ${threadId}`)
    } catch (err: any) {
      error.value = err.response?.data?.message || 'Failed to delete thread'
      logger.error('Failed to delete thread', err)
      throw error.value
    }
  }

  // ============================================================
  // POST ACTIONS
  // ============================================================

  async function loadPosts(threadId: string) {
    loading.value = true
    error.value = null
    try {
      const response = await discussionsApi.getPostsByThread(threadId)
      posts.value = response.data
      logger.log(`Loaded ${posts.value.length} posts for thread ${threadId}`)
    } catch (err: any) {
      error.value = err.response?.data?.message || 'Failed to load posts'
      logger.error('Failed to load posts', err)
    } finally {
      loading.value = false
    }
  }

  async function loadPostDetail(postId: string) {
    error.value = null
    try {
      const response = await discussionsApi.getPostById(postId)
      postDetail.value = response.data
      logger.log(`Loaded post ${postId}`)
    } catch (err: any) {
      error.value = err.response?.data?.message || 'Failed to load post'
      logger.error('Failed to load post', err)
    }
  }

  async function createPost(threadId: string, data: CreatePostRequest) {
    error.value = null
    try {
      const response = await discussionsApi.createPost(threadId, data)
      posts.value.push(response.data)
      logger.log(`Created post in thread ${threadId}`)
      return response.data
    } catch (err: any) {
      error.value = err.response?.data?.message || 'Failed to create post'
      logger.error('Failed to create post', err)
      throw error.value
    }
  }

  async function updatePost(postId: string, data: UpdatePostRequest) {
    error.value = null
    try {
      const response = await discussionsApi.updatePost(postId, data)
      
      // Update in local state
      const index = posts.value.findIndex(p => p.id === postId)
      if (index !== -1) {
        posts.value[index] = response.data
      }
      
      if (postDetail.value?.id === postId) {
        postDetail.value = response.data
      }
      
      logger.log(`Updated post ${postId}`)
      return response.data
    } catch (err: any) {
      error.value = err.response?.data?.message || 'Failed to update post'
      logger.error('Failed to update post', err)
      throw error.value
    }
  }

  async function deletePost(postId: string) {
    error.value = null
    try {
      await discussionsApi.deletePost(postId)
      
      // Remove from local state
      posts.value = posts.value.filter(p => p.id !== postId)
      if (postDetail.value?.id === postId) {
        postDetail.value = null
      }
      
      logger.log(`Deleted post ${postId}`)
    } catch (err: any) {
      error.value = err.response?.data?.message || 'Failed to delete post'
      logger.error('Failed to delete post', err)
      throw error.value
    }
  }

  // ============================================================
  // COMMENT ACTIONS
  // ============================================================

  async function loadComments(postId: string) {
    error.value = null
    try {
      const response = await discussionsApi.getCommentsByPost(postId)
      comments.value = response.data
      logger.log(`Loaded ${comments.value.length} comments for post ${postId}`)
    } catch (err: any) {
      error.value = err.response?.data?.message || 'Failed to load comments'
      logger.error('Failed to load comments', err)
    }
  }

  async function createComment(postId: string, data: CreateCommentRequest) {
    error.value = null
    try {
      const response = await discussionsApi.createComment(postId, data)
      comments.value.push(response.data)
      logger.log(`Created comment on post ${postId}`)
      return response.data
    } catch (err: any) {
      error.value = err.response?.data?.message || 'Failed to create comment'
      logger.error('Failed to create comment', err)
      throw error.value
    }
  }

  async function updateComment(commentId: string, data: UpdateCommentRequest) {
    error.value = null
    try {
      const response = await discussionsApi.updateComment(commentId, data)
      
      // Update in local state
      const index = comments.value.findIndex(c => c.id === commentId)
      if (index !== -1) {
        comments.value[index] = response.data
      }
      
      logger.log(`Updated comment ${commentId}`)
      return response.data
    } catch (err: any) {
      error.value = err.response?.data?.message || 'Failed to update comment'
      logger.error('Failed to update comment', err)
      throw error.value
    }
  }

  async function deleteComment(commentId: string) {
    error.value = null
    try {
      await discussionsApi.deleteComment(commentId)
      
      // Remove from local state
      comments.value = comments.value.filter(c => c.id !== commentId)
      
      logger.log(`Deleted comment ${commentId}`)
    } catch (err: any) {
      error.value = err.response?.data?.message || 'Failed to delete comment'
      logger.error('Failed to delete comment', err)
      throw error.value
    }
  }

  // ============================================================
  // CLEANUP
  // ============================================================

  function clearThreads() {
    threads.value = []
    threadDetail.value = null
  }

  function clearPosts() {
    posts.value = []
    postDetail.value = null
  }

  function clearComments() {
    comments.value = []
  }

  function clearAll() {
    clearThreads()
    clearPosts()
    clearComments()
    error.value = null
  }

  return {
    // State
    currentCourseId,
    threads,
    threadDetail,
    posts,
    postDetail,
    comments,
    loading,
    error,

    // Thread actions
    loadThreads,
    loadThreadDetail,
    createThread,
    updateThread,
    deleteThread,

    // Post actions
    loadPosts,
    loadPostDetail,
    createPost,
    updatePost,
    deletePost,

    // Comment actions
    loadComments,
    createComment,
    updateComment,
    deleteComment,

    // Cleanup
    clearThreads,
    clearPosts,
    clearComments,
    clearAll,
  }
})
