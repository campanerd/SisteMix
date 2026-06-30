import type { StatusParcela } from '../types';

// Formata número como moeda brasileira (R$ 1.234,56)
export function formatarMoeda(valor: number): string {
  return valor.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });
}

// Converte data ISO ("2026-02-15") para formato brasileiro (15/02/2026)
export function formatarData(iso: string | null): string {
  if (!iso) return '-';
  const [ano, mes, dia] = iso.split('-');
  return `${dia}/${mes}/${ano}`;
}

// Rótulo legível para cada status
export const rotuloStatus: Record<StatusParcela, string> = {
  PAGO: 'Pago',
  PENDENTE: 'Pendente',
  EM_ATRASO: 'Em atraso',
};

// Cor do MUI usada nos chips de status
export const corStatus: Record<StatusParcela, 'success' | 'warning' | 'error'> = {
  PAGO: 'success',
  PENDENTE: 'warning',
  EM_ATRASO: 'error',
};
