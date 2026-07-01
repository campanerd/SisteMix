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
import type { GridColDef } from '@mui/x-data-grid';
import { DatePicker } from '@mui/x-date-pickers/DatePicker';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import HourglassEmptyIcon from '@mui/icons-material/HourglassEmpty';
import WarningIcon from '@mui/icons-material/Warning';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import type { Dayjs } from 'dayjs';
import { atualizarStatusParcela, listarParcelas } from '../api/parcelas';
import type { ParcelaFiltros, ParcelaListagem, StatusParcela } from '../types';
import { corStatus, formatarData, formatarMoeda, rotuloStatus } from '../utils/format';

export function AcompanhamentoParcelas() {
  const queryClient = useQueryClient();

  // Estado do formulário de filtros
  const [status, setStatus] = useState<StatusParcela | ''>('');
  const [vencInicio, setVencInicio] = useState<Dayjs | null>(null);
  const [vencFim, setVencFim] = useState<Dayjs | null>(null);
  const [valorMin, setValorMin] = useState('');
  const [valorMax, setValorMax] = useState('');

  // Filtros efetivamente aplicados (disparam a busca)
  const [filtros, setFiltros] = useState<ParcelaFiltros>({});

  const [aviso, setAviso] = useState<{ tipo: 'success' | 'error'; texto: string } | null>(null);

  const { data, isLoading, isError } = useQuery({
    queryKey: ['parcelas', filtros],
    queryFn: () => listarParcelas(filtros),
  });

  const mutation = useMutation({
    mutationFn: ({ id, novoStatus }: { id: number; novoStatus: StatusParcela }) =>
      atualizarStatusParcela(id, novoStatus),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['parcelas'] });
      setAviso({ tipo: 'success', texto: 'Status atualizado com sucesso.' });
    },
    onError: () => {
      setAviso({ tipo: 'error', texto: 'Erro ao atualizar o status.' });
    },
  });

  function aplicarFiltros() {
    const novos: ParcelaFiltros = {};
    if (status) novos.status = status;
    if (vencInicio) novos.vencimentoInicio = vencInicio.format('YYYY-MM-DD');
    if (vencFim) novos.vencimentoFim = vencFim.format('YYYY-MM-DD');
    if (valorMin) novos.valorMin = Number(valorMin);
    if (valorMax) novos.valorMax = Number(valorMax);
    setFiltros(novos);
  }

  function limparFiltros() {
    setStatus('');
    setVencInicio(null);
    setVencFim(null);
    setValorMin('');
    setValorMax('');
    setFiltros({});
  }

  const colunas: GridColDef<ParcelaListagem>[] = [
    { field: 'numeroPedido', headerName: 'Pedido', width: 130 },
    { field: 'nomeCliente', headerName: 'Cliente', flex: 1, minWidth: 160 },
    {
      field: 'parcela',
      headerName: 'Parcela',
      width: 100,
      sortable: false,
      valueGetter: (_value, row) => `${row.numeroParcela}/${row.totalParcelas}`,
    },
    {
      field: 'valor',
      headerName: 'Valor',
      width: 130,
      renderCell: (params) => formatarMoeda(params.row.valor),
    },
    {
      field: 'vencimento',
      headerName: 'Vencimento',
      width: 130,
      renderCell: (params) => formatarData(params.row.vencimento),
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
      field: 'dataPagamento',
      headerName: 'Pagamento',
      width: 130,
      renderCell: (params) => formatarData(params.row.dataPagamento),
    },
    {
      field: 'acoes',
      type: 'actions',
      headerName: 'Ações',
      width: 80,
      getActions: (params) => {
        const itens = [];
        const atual = params.row.status;
        if (atual !== 'PAGO') {
          itens.push(
            <GridActionsCellItem
              key="pago"
              icon={<CheckCircleIcon />}
              label="Marcar como Pago"
              onClick={() => mutation.mutate({ id: params.row.id, novoStatus: 'PAGO' })}
              showInMenu
            />,
          );
        }
        if (atual !== 'PENDENTE') {
          itens.push(
            <GridActionsCellItem
              key="pendente"
              icon={<HourglassEmptyIcon />}
              label="Marcar como Pendente"
              onClick={() => mutation.mutate({ id: params.row.id, novoStatus: 'PENDENTE' })}
              showInMenu
            />,
          );
        }
        if (atual !== 'EM_ATRASO') {
          itens.push(
            <GridActionsCellItem
              key="atraso"
              icon={<WarningIcon />}
              label="Marcar como Em atraso"
              onClick={() => mutation.mutate({ id: params.row.id, novoStatus: 'EM_ATRASO' })}
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
      <Typography variant="h5" sx={{ mb: 2 }}>
        Acompanhamento de Parcelas
      </Typography>

      <Paper sx={{ p: 2, mb: 2, display: 'flex', flexWrap: 'wrap', gap: 2, alignItems: 'center' }}>
        <TextField
          select
          label="Status"
          size="small"
          value={status}
          onChange={(e) => setStatus(e.target.value as StatusParcela | '')}
          sx={{ minWidth: 160 }}
        >
          <MenuItem value="">Todos</MenuItem>
          <MenuItem value="PENDENTE">Pendente</MenuItem>
          <MenuItem value="PAGO">Pago</MenuItem>
          <MenuItem value="EM_ATRASO">Em atraso</MenuItem>
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
          initialState={{
            pagination: { paginationModel: { pageSize: 25 } },
          }}
          pageSizeOptions={[10, 25, 50, 100]}
          localeText={{ noRowsLabel: 'Nenhuma parcela encontrada' }}
        />
      </Paper>

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
