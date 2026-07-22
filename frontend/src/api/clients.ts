import { api } from './client';
import type { ClientSummary, Page } from '../types';

// GET /api/clients (lista os primeiros N, ordenados por nome, pro autocomplete de Novo Pedido)
export async function listClients(size = 200): Promise<Page<ClientSummary>> {
  const { data } = await api.get<Page<ClientSummary>>('/clients', { params: { size } });
  return data;
}
