import { useState } from 'react';
import {
  Alert,
  Box,
  Button,
  Chip,
  MenuItem,
  Paper,
  Snackbar,
  TextField,
  Typography,
} from '@mui/material';
import { DataGrid, GridActionsCellItem } from '@mui/x-data-grid';
import type { GridColDef, GridRowParams } from '@mui/x-data-grid';
import { DatePicker } from '@mui/x-date-pickers/DatePicker';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import HourglassEmptyIcon from '@mui/icons-material/HourglassEmpty';
import WarningIcon from '@mui/icons-material/Warning';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { useNavigate } from 'react-router-dom';
import type { Dayjs } from 'dayjs';
import { listInstallments, updateInstallmentStatus } from '../api/installments';
import type { InstallmentFilters, InstallmentStatus, InstallmentSummary } from '../types';
import { corStatus, formatarData, formatarMoeda, rotuloStatus } from '../utils/format';
import { filtrosPadraoParcelas } from '../utils/filtros';
import { NovoPedidoDialog } from './NovoPedidoDialog';

export function AcompanhamentoParcelas() {
  const queryClient = useQueryClient();
  const navigate = useNavigate();

  // Estado do formulário de filtros
  const [status, setStatus] = useState<InstallmentStatus | ''>('');
  const [vencInicio, setVencInicio] = useState<Dayjs | null>(null);
  const [vencFim, setVencFim] = useState<Dayjs | null>(null);
  const [valorMin, setValorMin] = useState('');
  const [valorMax, setValorMax] = useState('');

  // Filtros efetivamente aplicados (disparam a busca)
  const [filtros, setFiltros] = useState<InstallmentFilters>(filtrosPadraoParcelas());

  const [aviso, setAviso] = useState<{ tipo: 'success' | 'error'; texto: string } | null>(null);
  const [novoPedidoAberto, setNovoPedidoAberto] = useState(false);

  const { data, isLoading, isError } = useQuery({
    queryKey: ['installments', filtros],
    queryFn: () => listInstallments(filtros),
  });

  const mutation = useMutation({
    mutationFn: ({ id, novoStatus }: { id: number; novoStatus: InstallmentStatus }) =>
      updateInstallmentStatus(id, novoStatus),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['installments'] });
      setAviso({ tipo: 'success', texto: 'Status atualizado com sucesso.' });
    },
    onError: () => {
      setAviso({ tipo: 'error', texto: 'Erro ao atualizar o status.' });
    },
  });

  function aplicarFiltros() {
    // Clicar em "Filtrar" é sempre uma escolha explícita do usuários
    const novos: InstallmentFilters = { showAll: true };
    if (status) novos.status = status;
    if (vencInicio) novos.dueDateFrom = vencInicio.format('YYYY-MM-DD');
    if (vencFim) novos.dueDateTo = vencFim.format('YYYY-MM-DD');
    if (valorMin) novos.amountMin = Number(valorMin);
    if (valorMax) novos.amountMax = Number(valorMax);
    setFiltros(novos);
  }

  function abrirPedido(params: GridRowParams<InstallmentSummary>) {
    navigate(`/pedidos/${params.row.orderId}`);
  }

  function limparFiltros() {
    setStatus('');
    setVencInicio(null);
    setVencFim(null);
    setValorMin('');
    setValorMax('');
    setFiltros({});
  }

  const colunas: GridColDef<InstallmentSummary>[] = [
    { field: 'orderNumber', headerName: 'Pedido', width: 130 },
    { field: 'clientName', headerName: 'Cliente', flex: 1, minWidth: 160 },
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
        <Chip
          size="small"
          label={rotuloStatus[params.row.status]}
          color={corStatus[params.row.status]}
        />
      ),
    },
    {
      field: 'paymentDate',
      headerName: 'Pagamento',
      width: 130,
      renderCell: (params) => formatarData(params.row.paymentDate),
    },
    {
      field: 'acoes',
      type: 'actions',
      headerName: 'Ações',
      width: 80,
      getActions: (params) => {
        const itens = [];
        const atual = params.row.status;
        if (atual !== 'PAID') {
          itens.push(
            <GridActionsCellItem
              key="pago"
              icon={<CheckCircleIcon />}
              label="Marcar como Pago"
              onClick={() => mutation.mutate({ id: params.row.id, novoStatus: 'PAID' })}
              showInMenu
            />,
          );
        }
        if (atual !== 'PENDING') {
          itens.push(
            <GridActionsCellItem
              key="pendente"
              icon={<HourglassEmptyIcon />}
              label="Marcar como Pendente"
              onClick={() => mutation.mutate({ id: params.row.id, novoStatus: 'PENDING' })}
              showInMenu
            />,
          );
        }
        if (atual !== 'OVERDUE') {
          itens.push(
            <GridActionsCellItem
              key="atraso"
              icon={<WarningIcon />}
              label="Marcar como Em atraso"
              onClick={() => mutation.mutate({ id: params.row.id, novoStatus: 'OVERDUE' })}
              showInMenu
            />,
          );
        }
        return itens;
      },
    },
  ];

  return (
    <Box>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
        <Typography variant="h5">Acompanhamento de Pedidos</Typography>
        <Button variant="contained" onClick={() => setNovoPedidoAberto(true)}>
          Novo Pedido
        </Button>
      </Box>

      <Paper sx={{ p: 2, mb: 2, display: 'flex', flexWrap: 'wrap', gap: 2, alignItems: 'center' }}>
        <TextField
          select
          label="Status"
          size="small"
          value={status}
          onChange={(e) => setStatus(e.target.value as InstallmentStatus | '')}
          sx={{ minWidth: 160 }}
          slotProps={{ select: { displayEmpty: true }, inputLabel: { shrink: true } }}
        >
          <MenuItem value="">Todos</MenuItem>
          <MenuItem value="PENDING">Pendente</MenuItem>
          <MenuItem value="PAID">Pago</MenuItem>
          <MenuItem value="OVERDUE">Em atraso</MenuItem>
        </TextField>

        <DatePicker
          label="Vencimento de"
          value={vencInicio}
          onChange={setVencInicio}
          format="DD/MM/YYYY"
          slotProps={{ textField: { size: 'small' } }}
        />
        <DatePicker
          label="Vencimento até"
          value={vencFim}
          onChange={setVencFim}
          format="DD/MM/YYYY"
          slotProps={{ textField: { size: 'small' } }}
        />

        <TextField
          label="Valor mín."
          type="number"
          size="small"
          value={valorMin}
          onChange={(e) => setValorMin(e.target.value)}
          sx={{ width: 120 }}
        />
        <TextField
          label="Valor máx."
          type="number"
          size="small"
          value={valorMax}
          onChange={(e) => setValorMax(e.target.value)}
          sx={{ width: 120 }}
        />

        <Button variant="contained" onClick={aplicarFiltros}>
          Filtrar
        </Button>
        <Button variant="outlined" onClick={limparFiltros}>
          Limpar
        </Button>
      </Paper>

      {isError && <Alert severity="error">Erro ao carregar as parcelas.</Alert>}

      <Paper sx={{ height: 600 }}>
        <DataGrid
          rows={data ?? []}
          columns={colunas}
          loading={isLoading}
          disableRowSelectionOnClick
          onRowClick={abrirPedido}
          sx={{ '& .MuiDataGrid-row': { cursor: 'pointer' } }}
          initialState={{
            pagination: { paginationModel: { pageSize: 25 } },
          }}
          pageSizeOptions={[10, 25, 50, 100]}
          localeText={{ noRowsLabel: 'Nenhuma parcela encontrada' }}
        />
      </Paper>

      <NovoPedidoDialog
        open={novoPedidoAberto}
        onClose={() => setNovoPedidoAberto(false)}
        onCriado={() => setAviso({ tipo: 'success', texto: 'Pedido cadastrado com sucesso.' })}
      />

      <Snackbar
        open={aviso !== null}
        autoHideDuration={3000}
        onClose={() => setAviso(null)}
        anchorOrigin={{ vertical: 'bottom', horizontal: 'center' }}
      >
        {aviso ? (
          <Alert severity={aviso.tipo} onClose={() => setAviso(null)}>
            {aviso.texto}
          </Alert>
        ) : undefined}
      </Snackbar>
    </Box>
  );
}
