import axios from 'axios';

const TOKEN_KEY = 'sistemix_token';

// Guarda/recupera o JWT no localStorage (persiste entre recarregamentos da página)
export function getToken(): string | null {
  return localStorage.getItem(TOKEN_KEY);
}

export function saveToken(token: string): void {
  localStorage.setItem(TOKEN_KEY, token);
}

export function clearToken(): void {
  localStorage.removeItem(TOKEN_KEY);
}

// Instância única do axios usada por toda a aplicação.
// A URL base vem do .env (VITE_API_URL) e já inclui o prefixo /api.
export const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL ?? 'http://localhost:8080/api',
});

// Injeta o token JWT no header Authorization de toda requisição
api.interceptors.request.use((config) => {
  const token = getToken();
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// 401 = token ausente, inválido ou expirado: limpa a sessão e volta para o login
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401 && window.location.pathname !== '/login') {
      clearToken();
      window.location.href = '/login';
    }
    return Promise.reject(error);
  },
);
