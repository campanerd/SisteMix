import { ThemeProvider, CssBaseline } from '@mui/material';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { LocalizationProvider } from '@mui/x-date-pickers/LocalizationProvider';
import { AdapterDayjs } from '@mui/x-date-pickers/AdapterDayjs';
import { BrowserRouter, Route, Routes } from 'react-router-dom';
import 'dayjs/locale/pt-br';
import { theme } from './theme/theme';
import { Layout } from './components/Layout';
import { AcompanhamentoParcelas } from './pages/AcompanhamentoParcelas';
import { EmConstrucao } from './pages/EmConstrucao';

const queryClient = new QueryClient();

export default function App() {
  return (
    <QueryClientProvider client={queryClient}>
      <ThemeProvider theme={theme}>
        <CssBaseline />
        <LocalizationProvider dateAdapter={AdapterDayjs} adapterLocale="pt-br">
          <BrowserRouter>
            <Routes>
              <Route element={<Layout />}>
                <Route path="/" element={<AcompanhamentoParcelas />} />
                <Route path="/pedidos" element={<EmConstrucao titulo="Pedidos" />} />
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
