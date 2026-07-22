import { api } from './client';
import type { CreateOrderRequest, OrderHistoryResponse, OrderResponse } from '../types';

// POST /api/orders
export async function createOrder(data: CreateOrderRequest): Promise<OrderResponse> {
  const { data: response } = await api.post<OrderResponse>('/orders', data);
  return response;
}

// GET /api/orders/{id}
export async function getOrder(id: number): Promise<OrderResponse> {
  const { data } = await api.get<OrderResponse>(`/orders/${id}`);
  return data;
}

// GET /api/orders/{id}/history
export async function getOrderHistory(id: number): Promise<OrderHistoryResponse[]> {
  const { data } = await api.get<OrderHistoryResponse[]>(`/orders/${id}/history`);
  return data;
}
