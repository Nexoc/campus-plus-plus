// src/modules/favourites/store/favourites.store.ts

import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { Favourite } from '../model/Favourite'
import type { StudyProgramFavourite } from '../model/StudyProgramFavourite'
import { favouritesApi } from '../api/favouritesApi'
import { logger } from '@/shared/utils/logger'

export const useFavouritesStore = defineStore('favourites', () => {
  // State
  const favourites = ref<Favourite[]>([])
  const studyProgramFavourites = ref<StudyProgramFavourite[]>([])
  const loading = ref(false)
  const loaded = ref(false)

  // Getters
  const favouriteIds = computed(() => 
    new Set(favourites.value.map(f => f.courseId))
  )

  const studyProgramFavouriteIds = computed(() =>
    new Set(studyProgramFavourites.value.map(f => f.studyProgramId))
  )

  const isFavourite = (courseId: string) => {
    return favouriteIds.value.has(courseId)
  }

  const isStudyProgramFavourite = (studyProgramId: string) => {
    return studyProgramFavouriteIds.value.has(studyProgramId)
  }

  // Actions
  async function loadFavourites() {
    if (loading.value) return
    
    try {
      loading.value = true
      const response = await favouritesApi.getAll()
      favourites.value = response.data
      loaded.value = true
      logger.log(`Loaded ${favourites.value.length} favourites`)
    } catch (error) {
      logger.error('Failed to load favourites', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  async function addFavourite(courseId: string) {
    try {
      await favouritesApi.add({ courseId })
      
      // Optimistic update - refresh list
      await loadFavourites()
      
      logger.log(`Added course ${courseId} to favourites`)
    } catch (error) {
      logger.error(`Failed to add favourite ${courseId}`, error)
      throw error
    }
  }

  async function removeFavourite(courseId: string) {
    try {
      await favouritesApi.remove(courseId)
      
      // Optimistic update - remove from local state
      favourites.value = favourites.value.filter(f => f.courseId !== courseId)
      
      logger.log(`Removed course ${courseId} from favourites`)
    } catch (error) {
      logger.error(`Failed to remove favourite ${courseId}`, error)
      throw error
    }
  }

  function clearFavourites() {
    favourites.value = []
    studyProgramFavourites.value = []
    loaded.value = false
  }

  // Study Program Favourites Actions
  async function loadStudyProgramFavourites() {
    if (loading.value) return
    
    try {
      loading.value = true
      const response = await favouritesApi.getAllStudyPrograms()
      studyProgramFavourites.value = response.data
      logger.log(`Loaded ${studyProgramFavourites.value.length} study program favourites`)
    } catch (error) {
      logger.error('Failed to load study program favourites', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  async function addStudyProgramFavourite(studyProgramId: string) {
    try {
      await favouritesApi.addStudyProgram({ studyProgramId })
      
      // Optimistic update - refresh list
      await loadStudyProgramFavourites()
      
      logger.log(`Added study program ${studyProgramId} to favourites`)
    } catch (error) {
      logger.error(`Failed to add study program favourite ${studyProgramId}`, error)
      throw error
    }
  }

  async function removeStudyProgramFavourite(studyProgramId: string) {
    try {
      await favouritesApi.removeStudyProgram(studyProgramId)
      
      // Optimistic update - remove from local state
      studyProgramFavourites.value = studyProgramFavourites.value.filter(f => f.studyProgramId !== studyProgramId)
      
      logger.log(`Removed study program ${studyProgramId} from favourites`)
    } catch (error) {
      logger.error(`Failed to remove study program favourite ${studyProgramId}`, error)
      throw error
    }
  }

  return {
    // State
    favourites,
    studyProgramFavourites,
    loading,
    loaded,
    
    // Getters
    favouriteIds,
    studyProgramFavouriteIds,
    isFavourite,
    isStudyProgramFavourite,
    
    // Actions
    loadFavourites,
    addFavourite,
    removeFavourite,
    loadStudyProgramFavourites,
    addStudyProgramFavourite,
    removeStudyProgramFavourite,
    clearFavourites,
  }
})
