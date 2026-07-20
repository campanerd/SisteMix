import type { InstallmentFilters } from '../types';

// Filtro aplicado automaticamente ao abrir a tela de Acompanhamento de Parcelas.
// Placeholder simples — ajuste os critérios conforme a regra de negócio desejada.
export function filtrosPadraoParcelas(): InstallmentFilters {
  return {
    status: 'PAID',
  };
}
