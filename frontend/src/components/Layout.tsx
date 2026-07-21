import { AppBar, Box, Drawer, IconButton, List, ListItemButton, ListItemIcon, ListItemText, Toolbar, Tooltip, Typography } from '@mui/material';
import ReceiptLongIcon from '@mui/icons-material/ReceiptLong';
import ShoppingCartIcon from '@mui/icons-material/ShoppingCart';
import PeopleIcon from '@mui/icons-material/People';
import BadgeIcon from '@mui/icons-material/Badge';
import DashboardIcon from '@mui/icons-material/Dashboard';
import LogoutIcon from '@mui/icons-material/Logout';
import { NavLink, Outlet, useNavigate } from 'react-router-dom';
import type { ReactNode } from 'react';
import { clearToken } from '../api/client';

const LARGURA_MENU = 240;

interface ItemMenu {
  rotulo: string;
  caminho: string;
  icone: ReactNode;
}

const itens: ItemMenu[] = [
  { rotulo: 'Acompanhamento', caminho: '/acompanhamento', icone: <ReceiptLongIcon /> },
  { rotulo: 'Pedidos', caminho: '/pedidos', icone: <ShoppingCartIcon /> },
  { rotulo: 'Clientes', caminho: '/clientes', icone: <PeopleIcon /> },
  { rotulo: 'Vendedores', caminho: '/vendedores', icone: <BadgeIcon /> },
  { rotulo: 'Dashboard', caminho: '/dashboard', icone: <DashboardIcon /> },
];

export function Layout() {
  const navigate = useNavigate();

  function handleLogout() {
    clearToken();
    navigate('/login', { replace: true });
  }

  return (
    <Box sx={{ display: 'flex' }}>
      <AppBar position="fixed" sx={{ zIndex: (theme) => theme.zIndex.drawer + 1 }}>
        <Toolbar>
          <Typography variant="h6" noWrap component="div" sx={{ flexGrow: 1 }}>
            SisteMix
          </Typography>
          <Tooltip title="Sair">
            <IconButton color="inherit" onClick={handleLogout}>
              <LogoutIcon />
            </IconButton>
          </Tooltip>
        </Toolbar>
      </AppBar>

      <Drawer
        variant="permanent"
        sx={{
          width: LARGURA_MENU,
          flexShrink: 0,
          [`& .MuiDrawer-paper`]: { width: LARGURA_MENU, boxSizing: 'border-box' },
        }}
      >
        <Toolbar />
        <Box sx={{ overflow: 'auto' }}>
          <List>
            {itens.map((item) => (
              <ListItemButton
                key={item.caminho}
                component={NavLink}
                to={item.caminho}
                sx={{
                  '&.active': {
                    bgcolor: 'action.selected',
                    borderRight: 3,
                    borderColor: 'primary.main',
                  },
                }}
              >
                <ListItemIcon>{item.icone}</ListItemIcon>
                <ListItemText primary={item.rotulo} />
              </ListItemButton>
            ))}
          </List>
        </Box>
      </Drawer>

      <Box component="main" sx={{ flexGrow: 1, p: 3 }}>
        <Toolbar />
        <Outlet />
      </Box>
    </Box>
  );
}
