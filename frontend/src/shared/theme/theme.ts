const THEME_KEY = 'theme'

export type Theme = 'light' | 'dark'

export function getTheme(): Theme {
  return (localStorage.getItem(THEME_KEY) as Theme) || 'light'
}

export function setTheme(theme: Theme): void {
  document.documentElement.setAttribute('data-theme', theme)
  localStorage.setItem(THEME_KEY, theme)
}

export function toggleTheme(): Theme {
  const next: Theme = getTheme() === 'light' ? 'dark' : 'light'
  setTheme(next)
  return next
}

export function initTheme(): void {
  setTheme(getTheme())
}
