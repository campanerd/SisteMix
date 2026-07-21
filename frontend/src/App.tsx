import { ThemeProvider, CssBaseline } from '@mui/material';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { LocalizationProvider } from '@mui/x-date-pickers/LocalizationProvider';
import { AdapterDayjs } from '@mui/x-date-pickers/AdapterDayjs';
import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom';
import type { ReactNode } from 'react';
import 'dayjs/locale/pt-br';
import { theme } from './theme/theme';
import { getToken } from './api/client';
import { Layout } from './components/Layout';
import { Login } from './pages/Login';
import { AcompanhamentoParcelas } from './pages/AcompanhamentoParcelas';
import { EmConstrucao } from './pages/EmConstrucao';

const queryClient = new QueryClient();

function RequireAuth({ children }: { children: ReactNode }) {
  if (!getToken()) {
    return <Navigate to="/login" replace />;
  }
  return children;
}

export default function App() {
  return (
    <QueryClientProvider client={queryClient}>
      <ThemeProvider theme={theme}>
        <CssBaseline />
        <LocalizationProvider dateAdapter={AdapterDayjs} adapterLocale="pt-br">
          <BrowserRouter>
            <Routes>
              <Route path="/login" element={<Login />} />
              <Route
                element={
                  <RequireAuth>
                    <Layout />
                  </RequireAuth>
                }
              >
                <Route path="/" element={<Navigate to="/acompanhamento" replace />} />
                <Route path="/pedidos" element={<AcompanhamentoParcelas />} />
                <Route path="/acompanhamento" element={<EmConstrucao titulo="Acompanhamento" />} />
                <Route path="/clientes" element={<EmConstrucao titulo="Clientes" />} />
                <Route path="/vendedores" element={<EmConstrucao titulo="Vendedores" />} />
                <Route path="/dashboard" element={<EmConstrucao titulo="Dashboard" />} />
              </Route>
            </Routes>
          </BrowserRouter>
        </LocalizationProvider>
      </ThemeProvider>
    </QueryClientProvider>
  );
}
