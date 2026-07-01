import { createTheme } from '@mui/material/styles';

// Tema base da aplicação. Cores podem ser ajustadas depois.
export const theme = createTheme({
  palette: {
    mode: 'light',
    primary: {
      main: '#1565c0',
    },
    background: {
      default: '#f4f6f8',
    },
  },
  shape: {
    borderRadius: 8,
  },
});
