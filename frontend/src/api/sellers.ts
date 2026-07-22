import { api } from './client';
import type { Page, SellerSummary } from '../types';

// GET /api/sellers (lista os primeiros N, ordenados por nome, pro autocomplete de Novo Pedido)
export async function listSellers(size = 200): Promise<Page<SellerSummary>> {
  const { data } = await api.get<Page<SellerSummary>>('/sellers', { params: { size } });
  return data;
}
