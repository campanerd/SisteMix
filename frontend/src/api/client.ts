import axios from 'axios';

// Instância única do axios usada por toda a aplicação.
// A URL base vem do .env (VITE_API_URL), com fallback para localhost.
export const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL ?? 'http://localhost:8080',
});
