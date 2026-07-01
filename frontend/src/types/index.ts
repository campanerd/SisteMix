// Tipos espelhando os DTOs do backend (SisteMix API)

export type StatusParcela = 'PAGO' | 'PENDENTE' | 'EM_ATRASO';

// ----- Spring Data Page (listagens paginadas) -----
export interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number; // página atual (zero-based)
  size: number;
}

// ----- Cliente -----
export interface ClienteListagem {
  id: number;
  nome: string;
  telefone: string | null;
  cpfCnpj: string | null;
}

export interface ClienteDetalhe extends ClienteListagem {
  email: string | null;
}

export interface ClienteCadastro {
  nome: string;
  telefone?: string;
  cpfCnpj?: string;
  email?: string;
}

export interface ClienteAtualizacao {
  id: number;
  nome?: string;
  telefone?: string;
  email?: string;
}

// ----- Vendedor -----
export interface VendedorListagem {
  id: number;
  nome: string;
  cpf: string | null;
  telefone: string | null;
}

export interface VendedorCadastro {
  nome: string;
  cpf?: string;
  telefone?: string;
}

export interface VendedorAtualizacao {
  id: number;
  nome?: string;
  telefone?: string;
}

// ----- Pedido -----
export interface PedidoListagem {
  id: number;
  numeroPedido: string;
  nomeCliente: string;
  nomeVendedor: string;
  valorTotal: number;
  totalParcelas: number;
  dataPedido: string; // ISO date "AAAA-MM-DD"
}

export interface PedidoDetalhe {
  id: number;
  numeroPedido: string;
  dataEmissao: string;
  dataPedido: string;
  valorTotal: number;
  totalParcelas: number;
  observacao: string | null;
  idCliente: number;
  nomeCliente: string;
  idVendedor: number;
  nomeVendedor: string;
}

export interface PedidoCadastro {
  numeroPedido: string;
  dataEmissao: string;
  dataPedido: string;
  valorTotal: number;
  totalParcelas: number;
  observacao?: string;
  idCliente: number;
  idVendedor: number;
}

export interface PedidoAtualizacao {
  id: number;
  dataEmissao?: string;
  dataPedido?: string;
  valorTotal?: number;
  observacao?: string;
  idVendedor?: number;
}

// ----- Parcela -----
export interface ParcelaListagem {
  id: number;
  numeroParcela: number;
  totalParcelas: number;
  valor: number;
  vencimento: string;
  status: StatusParcela;
  dataPagamento: string | null;
  numeroPedido: string;
  nomeCliente: string;
}

export interface ParcelaDetalhe {
  id: number;
  numeroParcela: number;
  totalParcelas: number;
  valor: number;
  vencimento: string;
  status: StatusParcela;
  dataPagamento: string | null;
  idPedido: number;
  numeroPedido: string;
  nomeCliente: string;
  nomeVendedor: string;
}

export interface ParcelaFiltros {
  status?: StatusParcela;
  vencimentoInicio?: string;
  vencimentoFim?: string;
  valorMin?: number;
  valorMax?: number;
}
