import type { InstallmentFilters } from '../types';

// Filtro aplicado automaticamente ao abrir a tela de Acompanhamento de Parcelas.
// Vazio de propósito: sem filtro nenhum, o backend retorna a próxima parcela
// não paga de cada pedido (agregação via query nativa), em vez da lista completa.
export function filtrosPadraoParcelas(): InstallmentFilters {
  return {};
}
