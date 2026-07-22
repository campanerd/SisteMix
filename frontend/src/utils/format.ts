import type { InstallmentStatus } from '../types';

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

// Percentual de uma parte em relação ao total (0 quando o total é zero, evita divisão por zero)
export function formatarPercentual(parte: number, total: number): string {
  if (total <= 0) return '0%';
  return `${((parte / total) * 100).toFixed(1).replace('.', ',')}%`;
}

// Rótulo legível para cada status (a API fala inglês, a tela fala português)
export const rotuloStatus: Record<InstallmentStatus, string> = {
  PAID: 'Pago',
  PENDING: 'Pendente',
  OVERDUE: 'Em atraso',
};

// Cor do MUI usada nos chips de status
export const corStatus: Record<InstallmentStatus, 'success' | 'warning' | 'error'> = {
  PAID: 'success',
  PENDING: 'warning',
  OVERDUE: 'error',
};
