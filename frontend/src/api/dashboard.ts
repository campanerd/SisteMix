import { api } from './client';
import type { DashboardSummaryResponse } from '../types';

// GET /api/dashboard/summary
export async function getDashboardSummary(): Promise<DashboardSummaryResponse> {
  const { data } = await api.get<DashboardSummaryResponse>('/dashboard/summary');
  return data;
}
