// Tipos espelhando os DTOs do backend (SisteMix API)

export type InstallmentStatus = 'PAID' | 'PENDING' | 'OVERDUE';

export type UserRole = 'ROLE_USER' | 'ROLE_ADMIN' | 'ROLE_DEV';

// ----- Spring Data Page (listagens paginadas) -----
export interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number; // página atual (zero-based)
  size: number;
}

// ----- Auth -----
export interface LoginRequest {
  email: string;
  password: string;
}

export interface LoginResponse {
  token: string;
}

// ----- User -----
export interface UserResponse {
  id: number;
  name: string;
  email: string;
}

export interface CreateUserRequest {
  name: string;
  email: string;
  password: string;
  role: UserRole;
}

export interface UpdateUserRequest {
  id: number;
  name?: string;
  email?: string;
  password?: string;
  role?: UserRole;
}

// ----- Client -----
export interface ClientSummary {
  id: number;
  name: string;
  phone: string | null;
  cpfCnpj: string | null;
}

export interface ClientResponse extends ClientSummary {
  email: string | null;
}

export interface CreateClientRequest {
  name: string;
  phone?: string;
  cpfCnpj?: string;
  email?: string;
}

export interface UpdateClientRequest {
  id: number;
  name?: string;
  phone?: string;
  email?: string;
}

// ----- Seller -----
export interface SellerSummary {
  id: number;
  name: string;
  cpf: string | null;
}

export interface SellerResponse extends SellerSummary {
  phone: string | null;
}

export interface CreateSellerRequest {
  name: string;
  cpf?: string;
  phone?: string;
}

export interface UpdateSellerRequest {
  id: number;
  name?: string;
  phone?: string;
}

// ----- Order -----
export interface OrderSummary {
  id: number;
  orderNumber: string;
  clientName: string;
  sellerName: string;
  totalAmount: number;
  totalInstallments: number;
  orderDate: string; // ISO date "AAAA-MM-DD"
}

export interface OrderResponse {
  id: number;
  orderNumber: string;
  issueDate: string;
  orderDate: string;
  totalAmount: number;
  totalInstallments: number;
  notes: string | null;
  clientId: number;
  clientName: string;
  sellerId: number;
  sellerName: string;
}

export interface CreateOrderRequest {
  orderNumber: string;
  issueDate: string;
  orderDate: string;
  totalAmount: number;
  totalInstallments: number;
  notes?: string;
  clientId: number;
  sellerId: number;
}

export interface UpdateOrderRequest {
  id: number;
  issueDate?: string;
  orderDate?: string;
  totalAmount?: number;
  notes?: string;
  sellerId?: number;
}

// ----- Installment -----
export interface InstallmentSummary {
  id: number;
  installmentNumber: number;
  totalInstallments: number;
  amount: number;
  dueDate: string;
  status: InstallmentStatus;
  paymentDate: string | null;
  orderNumber: string;
  clientName: string;
}

export interface InstallmentResponse extends InstallmentSummary {
  orderId: number;
  sellerName: string;
}

export interface InstallmentFilters {
  status?: InstallmentStatus;
  dueDateFrom?: string;
  dueDateTo?: string;
  amountMin?: number;
  amountMax?: number;
}
