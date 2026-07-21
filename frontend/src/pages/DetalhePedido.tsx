import {
  Alert,
  Box,
  Button,
  Chip,
  CircularProgress,
  Divider,
  Grid,
  Paper,
  Typography,
} from '@mui/material';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import { DataGrid } from '@mui/x-data-grid';
import type { GridColDef } from '@mui/x-data-grid';
import { useQuery } from '@tanstack/react-query';
import { useNavigate, useParams } from 'react-router-dom';
import { getOrder, getOrderHistory } from '../api/orders';
import { listInstallmentsByOrder } from '../api/installments';
import type { InstallmentSummary } from '../types';
import { corStatus, formatarData, formatarMoeda, rotuloStatus } from '../utils/format';

export function DetalhePedido() {
  const { id } = useParams<{ id: string }>();
  const orderId = Number(id);
  const navigate = useNavigate();

  const order = useQuery({
    queryKey: ['order', orderId],
    queryFn: () => getOrder(orderId),
  });

  const installments = useQuery({
    queryKey: ['order-installments', orderId],
    queryFn: () => listInstallmentsByOrder(orderId),
  });

  const history = useQuery({
    queryKey: ['order-history', orderId],
    queryFn: () => getOrderHistory(orderId),
  });

  const colunas: GridColDef<InstallmentSummary>[] = [
    {
      field: 'parcela',
      headerName: 'Parcela',
      width: 100,
      sortable: false,
      valueGetter: (_value, row) => `${row.installmentNumber}/${row.totalInstallments}`,
    },
    {
      field: 'amount',
      headerName: 'Valor',
      width: 130,
      renderCell: (params) => formatarMoeda(params.row.amount),
    },
    {
      field: 'dueDate',
      headerName: 'Vencimento',
      width: 130,
      renderCell: (params) => formatarData(params.row.dueDate),
    },
    {
      field: 'status',
      headerName: 'Status',
      width: 130,
      renderCell: (params) => (
        <Chip size="small" label={rotuloStatus[params.row.status]} color={corStatus[params.row.status]} />
      ),
    },
    {
      field: 'paymentDate',
      headerName: 'Pagamento',
      width: 130,
      renderCell: (params) => formatarData(params.row.paymentDate),
    },
  ];

  if (order.isLoading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', mt: 4 }}>
        <CircularProgress />
      </Box>
    );
  }

  if (order.isError || !order.data) {
    return <Alert severity="error">Erro ao carregar o pedido.</Alert>;
  }

  const pedido = order.data;

  return (
    <Box>
      <Button startIcon={<ArrowBackIcon />} onClick={() => navigate('/pedidos')} sx={{ mb: 2 }}>
        Voltar
      </Button>

      <Typography variant="h5" sx={{ mb: 2 }}>
        Pedido {pedido.orderNumber}
      </Typography>

      <Paper sx={{ p: 3, mb: 3 }}>
        <Grid container spacing={2}>
          <Grid size={{ xs: 12, sm: 6, md: 3 }}>
            <Typography variant="caption" color="text.secondary">Cliente</Typography>
            <Typography>{pedido.clientName}</Typography>
          </Grid>
          <Grid size={{ xs: 12, sm: 6, md: 3 }}>
            <Typography variant="caption" color="text.secondary">Vendedor</Typography>
            <Typography>{pedido.sellerName}</Typography>
          </Grid>
          <Grid size={{ xs: 12, sm: 6, md: 3 }}>
            <Typography variant="caption" color="text.secondary">Data do pedido</Typography>
            <Typography>{formatarData(pedido.orderDate)}</Typography>
          </Grid>
          <Grid size={{ xs: 12, sm: 6, md: 3 }}>
            <Typography variant="caption" color="text.secondary">Data de emissão</Typography>
            <Typography>{formatarData(pedido.issueDate)}</Typography>
          </Grid>
          <Grid size={{ xs: 12, sm: 6, md: 3 }}>
            <Typography variant="caption" color="text.secondary">Valor total</Typography>
            <Typography>{formatarMoeda(pedido.totalAmount)}</Typography>
          </Grid>
          <Grid size={{ xs: 12, sm: 6, md: 3 }}>
            <Typography variant="caption" color="text.secondary">Parcelas</Typography>
            <Typography>{pedido.totalInstallments}</Typography>
          </Grid>
          <Grid size={{ xs: 12, sm: 6, md: 3 }}>
            <Typography variant="caption" color="text.secondary">Criado por</Typography>
            <Typography>{pedido.createdByName}</Typography>
          </Grid>
          {pedido.notes && (
            <Grid size={12}>
              <Typography variant="caption" color="text.secondary">Observações</Typography>
              <Typography>{pedido.notes}</Typography>
            </Grid>
          )}
        </Grid>
      </Paper>

      <Typography variant="h6" sx={{ mb: 1 }}>Parcelas</Typography>
      <Paper sx={{ height: 400, mb: 3 }}>
        <DataGrid
          rows={installments.data ?? []}
          columns={colunas}
          loading={installments.isLoading}
          disableRowSelectionOnClick
          localeText={{ noRowsLabel: 'Nenhuma parcela encontrada' }}
        />
      </Paper>

      <Typography variant="h6" sx={{ mb: 1 }}>Histórico de alterações</Typography>
      <Paper sx={{ p: 2 }}>
        {history.isLoading && <Typography color="text.secondary">Carregando...</Typography>}
        {!history.isLoading && (history.data?.length ?? 0) === 0 && (
          <Typography color="text.secondary">Nenhuma alteração registrada.</Typography>
        )}
        {history.data?.map((registro, index) => (
          <Box key={registro.id}>
            {index > 0 && <Divider sx={{ my: 1.5 }} />}
            <Typography variant="body2">
              <strong>{registro.action === 'DELETE' ? 'Excluído' : 'Atualizado'}</strong> por{' '}
              {registro.changedByName} em {new Date(registro.changedAt).toLocaleString('pt-BR')}
            </Typography>
            {registro.changes.length > 0 && (
              <Box component="ul" sx={{ mt: 0.5, mb: 0 }}>
                {registro.changes.map((mudanca) => (
                  <li key={mudanca.field}>
                    <Typography variant="body2" color="text.secondary">
                      {mudanca.field}: {mudanca.from ?? '-'} → {mudanca.to ?? '-'}
                    </Typography>
                  </li>
                ))}
              </Box>
            )}
          </Box>
        ))}
      </Paper>
    </Box>
  );
}
