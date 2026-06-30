import { api } from './client';
import type {
  ParcelaDetalhe,
  ParcelaFiltros,
  ParcelaListagem,
  StatusParcela,
} from '../types';

// GET /parcelas (com filtros opcionais via query params)
export async function listarParcelas(filtros: ParcelaFiltros = {}): Promise<ParcelaListagem[]> {
  const { data } = await api.get<ParcelaListagem[]>('/parcelas', { params: filtros });
  return data;
}

// GET /parcelas/{id}
export async function detalharParcela(id: number): Promise<ParcelaDetalhe> {
  const { data } = await api.get<ParcelaDetalhe>(`/parcelas/${id}`);
  return data;
}

// GET /parcelas/pedido/{idPedido}
export async function listarParcelasPorPedido(idPedido: number): Promise<ParcelaListagem[]> {
  const { data } = await api.get<ParcelaListagem[]>(`/parcelas/pedido/${idPedido}`);
  return data;
}

// PATCH /parcelas/{id}/status
export async function atualizarStatusParcela(
  id: number,
  status: StatusParcela,
): Promise<ParcelaDetalhe> {
  const { data } = await api.patch<ParcelaDetalhe>(`/parcelas/${id}/status`, { status });
  return data;
}
