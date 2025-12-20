// src/shared/composables/useFormErrors.ts
//
// Composable for handling form validation errors.
// Designed to map backend validation errors
// to field-level error messages in the UI.
//
// Typical backend error format:
//
// {
//   "fieldErrors": {
//     "email": "Invalid email address",
//     "password": "Password must be at least 8 characters"
//   }
// }
//
// This composable:
// - stores field errors in a reactive object
// - provides helper functions to set and clear errors
// - is completely UI-agnostic
//
// IMPORTANT:
// - No HTTP logic
// - No business logic
// - No framework-specific assumptions (besides Vue reactivity)

import { reactive } from 'vue'

// --------------------------------------------------
// Types
// --------------------------------------------------

/**
 * Represents a map of field names to error messages.
 *
 * Example:
 * {
 *   email: "Invalid email",
 *   password: "Too short"
 * }
 */
export type FieldErrors = Record<string, string>

// --------------------------------------------------
// Composable
// --------------------------------------------------

export function useFormErrors() {
  // --------------------------------------------------
  // Reactive error container
  // --------------------------------------------------
  //
  // Using reactive() instead of ref({})
  // allows easy property access in templates:
  //   fieldErrors.email
  //
  const fieldErrors = reactive<FieldErrors>({})

  // --------------------------------------------------
  // Set errors from backend response
  // --------------------------------------------------
  //
  // This function replaces all existing errors
  // with the provided ones.
  //
  const setErrors = (errors: FieldErrors): void => {
    clearErrors()

    for (const key in errors) {
      fieldErrors[key] = errors[key]
    }
  }

  // --------------------------------------------------
  // Clear all field errors
  // --------------------------------------------------
  //
  // Called:
  // - before form submission
  // - when user retries input
  //
  const clearErrors = (): void => {
    for (const key in fieldErrors) {
      delete fieldErrors[key]
    }
  }

  // --------------------------------------------------
  // Public API
  // --------------------------------------------------
  return {
    fieldErrors,
    setErrors,
    clearErrors,
  }
}
