import { api } from './client';
import type {
  InstallmentFilters,
  InstallmentResponse,
  InstallmentStatus,
  InstallmentSummary,
} from '../types';

// GET /api/installments (com filtros opcionais via query params)
export async function listInstallments(
  filters: InstallmentFilters = {},
): Promise<InstallmentSummary[]> {
  const { data } = await api.get<InstallmentSummary[]>('/installments', { params: filters });
  return data;
}

// GET /api/installments/{id}
export async function getInstallment(id: number): Promise<InstallmentResponse> {
  const { data } = await api.get<InstallmentResponse>(`/installments/${id}`);
  return data;
}

// GET /api/installments/order/{orderId}
export async function listInstallmentsByOrder(orderId: number): Promise<InstallmentSummary[]> {
  const { data } = await api.get<InstallmentSummary[]>(`/installments/order/${orderId}`);
  return data;
}

// PATCH /api/installments/{id}/status
export async function updateInstallmentStatus(
  id: number,
  status: InstallmentStatus,
): Promise<InstallmentResponse> {
  const { data } = await api.patch<InstallmentResponse>(`/installments/${id}/status`, { status });
  return data;
}
