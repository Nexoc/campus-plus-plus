import http from '@/app/api/http'

let csrfReady = false

export async function ensureCsrf(): Promise<void> {
  if (csrfReady) return

  console.log('ensureCsrf called, csrfReady =', csrfReady)

  await http.post('/auth/csrf', {}, { withCredentials: true })


  csrfReady = true
}
