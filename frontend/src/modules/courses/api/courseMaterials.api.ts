import http from '@/app/api/http'

export interface CourseMaterial {
  id: string
  title: string | null
  description: string | null
  originalFilename: string
  contentType: string
  sizeBytes: number
  createdAt: string
  uploaderId: string
}

/* ---------------- list ---------------- */

export async function listByCourse(courseId: string): Promise<CourseMaterial[]> {
  const res = await http.get<CourseMaterial[]>(
    `/api/courses/${courseId}/materials`
  )
  return res.data
}

/* ---------------- upload ---------------- */

export async function uploadMaterial(
  courseId: string,
  file: File,
  title?: string,
  description?: string
): Promise<CourseMaterial> {
  const formData = new FormData()
  formData.append('file', file)
  if (title) formData.append('title', title)
  if (description) formData.append('description', description)

  const res = await http.post<CourseMaterial>(
    `/api/courses/${courseId}/materials`,
    formData,
    {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    }
  )

  return res.data
}

/* ---------------- download ---------------- */

export async function downloadMaterial(materialId: string): Promise<Blob> {
  const res = await http.get(
    `/api/materials/${materialId}/download`,
    {
      responseType: 'blob',
    }
  )
  return res.data
}

/* ---------------- update ---------------- */

export async function updateMaterial(
  materialId: string,
  payload: {
    title?: string | null
    description?: string | null
  }
): Promise<CourseMaterial> {
  const res = await http.put<CourseMaterial>(
    `/api/materials/${materialId}`,
    payload
  )
  return res.data
}

/* ---------------- delete ---------------- */

export async function deleteMaterial(materialId: string): Promise<void> {
  await http.delete(
    `/api/materials/${materialId}`
  )
}
