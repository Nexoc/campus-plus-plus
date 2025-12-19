import { defineStore } from "pinia";

export const useAuthStore = defineStore("auth", {
  state: () => ({
    token: null as string | null
  }),
  actions: {
    setToken(token: string) {
      this.token = token;
    },
    logout() {
      this.token = null;
    }
  }
});
