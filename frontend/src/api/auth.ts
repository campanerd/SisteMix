import { api } from './client';
import type { LoginRequest, LoginResponse } from '../types';

// POST /api/auth/login
export async function login(email: string, password: string): Promise<LoginResponse> {
  const request: LoginRequest = { email, password };
  const { data } = await api.post<LoginResponse>('/auth/login', request);
  return data;
}
