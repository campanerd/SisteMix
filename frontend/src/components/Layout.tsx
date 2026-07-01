import { AppBar, Box, Drawer, List, ListItemButton, ListItemIcon, ListItemText, Toolbar, Typography } from '@mui/material';
import ReceiptLongIcon from '@mui/icons-material/ReceiptLong';
import ShoppingCartIcon from '@mui/icons-material/ShoppingCart';
import PeopleIcon from '@mui/icons-material/People';
import BadgeIcon from '@mui/icons-material/Badge';
import DashboardIcon from '@mui/icons-material/Dashboard';
import { NavLink, Outlet } from 'react-router-dom';
import type { ReactNode } from 'react';

const LARGURA_MENU = 240;

interface ItemMenu {
  rotulo: string;
  caminho: string;
  icone: ReactNode;
}

const itens: ItemMenu[] = [
  { rotulo: 'Acompanhamento', caminho: '/', icone: <ReceiptLongIcon /> },
  { rotulo: 'Pedidos', caminho: '/pedidos', icone: <ShoppingCartIcon /> },
  { rotulo: 'Clientes', caminho: '/clientes', icone: <PeopleIcon /> },
  { rotulo: 'Vendedores', caminho: '/vendedores', icone: <BadgeIcon /> },
  { rotulo: 'Dashboard', caminho: '/dashboard', icone: <DashboardIcon /> },
];

export function Layout() {
  return (
    <Box sx={{ display: 'flex' }}>
      <AppBar position="fixed" sx={{ zIndex: (theme) => theme.zIndex.drawer + 1 }}>
        <Toolbar>
          <Typography variant="h6" noWrap component="div">
            SisteMix
          </Typography>
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
                end={item.caminho === '/'}
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
