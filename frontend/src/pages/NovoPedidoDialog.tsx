import { useState } from 'react';
import {
  Alert,
  Autocomplete,
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  Stack,
  TextField,
} from '@mui/material';
import { DatePicker } from '@mui/x-date-pickers/DatePicker';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import dayjs, { type Dayjs } from 'dayjs';
import { listClients } from '../api/clients';
import { listSellers } from '../api/sellers';
import { createOrder } from '../api/orders';
import type { ClientSummary, SellerSummary } from '../types';

interface NovoPedidoDialogProps {
  open: boolean;
  onClose: () => void;
  onCriado: () => void;
}

export function NovoPedidoDialog({ open, onClose, onCriado }: NovoPedidoDialogProps) {
  const queryClient = useQueryClient();

  const [orderNumber, setOrderNumber] = useState('');
  const [issueDate, setIssueDate] = useState<Dayjs | null>(dayjs());
  const [orderDate, setOrderDate] = useState<Dayjs | null>(dayjs());
  const [totalAmount, setTotalAmount] = useState('');
  const [totalInstallments, setTotalInstallments] = useState('');
  const [notes, setNotes] = useState('');
  const [client, setClient] = useState<ClientSummary | null>(null);
  const [seller, setSeller] = useState<SellerSummary | null>(null);
  const [erro, setErro] = useState<string | null>(null);

  const { data: clientes, isLoading: carregandoClientes } = useQuery({
    queryKey: ['clients-autocomplete'],
    queryFn: () => listClients(),
    enabled: open,
  });

  const { data: vendedores, isLoading: carregandoVendedores } = useQuery({
    queryKey: ['sellers-autocomplete'],
    queryFn: () => listSellers(),
    enabled: open,
  });

  const mutation = useMutation({
    mutationFn: createOrder,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['installments'] });
      queryClient.invalidateQueries({ queryKey: ['dashboard-summary'] });
      limparFormulario();
      onCriado();
      onClose();
    },
    onError: () => {
      setErro('Erro ao cadastrar o pedido. Confira os dados e tente novamente.');
    },
  });

  function limparFormulario() {
    setOrderNumber('');
    setIssueDate(dayjs());
    setOrderDate(dayjs());
    setTotalAmount('');
    setTotalInstallments('');
    setNotes('');
    setClient(null);
    setSeller(null);
    setErro(null);
  }

  function fechar() {
    limparFormulario();
    onClose();
  }

  function salvar() {
    if (!orderNumber || !issueDate || !orderDate || !totalAmount || !totalInstallments || !client || !seller) {
      setErro('Preencha todos os campos obrigatórios.');
      return;
    }
    setErro(null);
    mutation.mutate({
      orderNumber,
      issueDate: issueDate.format('YYYY-MM-DD'),
      orderDate: orderDate.format('YYYY-MM-DD'),
      totalAmount: Number(totalAmount),
      totalInstallments: Number(totalInstallments),
      notes: notes || undefined,
      clientId: client.id,
      sellerId: seller.id,
    });
  }

  return (
    <Dialog open={open} onClose={fechar} maxWidth="sm" fullWidth>
      <DialogTitle>Novo Pedido</DialogTitle>
      <DialogContent>
        <Stack spacing={2} sx={{ mt: 1 }}>
          <TextField
            label="Número do pedido"
            value={orderNumber}
            onChange={(e) => setOrderNumber(e.target.value)}
            fullWidth
            required
          />

          <Autocomplete
            options={clientes?.content ?? []}
            getOptionLabel={(option) => option.name}
            isOptionEqualToValue={(option, value) => option.id === value.id}
            loading={carregandoClientes}
            value={client}
            onChange={(_event, novoValor) => setClient(novoValor)}
            renderInput={(params) => <TextField {...params} label="Cliente" required />}
          />

          <Autocomplete
            options={vendedores?.content ?? []}
            getOptionLabel={(option) => option.name}
            isOptionEqualToValue={(option, value) => option.id === value.id}
            loading={carregandoVendedores}
            value={seller}
            onChange={(_event, novoValor) => setSeller(novoValor)}
            renderInput={(params) => <TextField {...params} label="Vendedor" required />}
          />

          <Stack direction="row" spacing={2}>
            <DatePicker
              label="Data de emissão"
              value={issueDate}
              onChange={setIssueDate}
              format="DD/MM/YYYY"
              slotProps={{ textField: { fullWidth: true, required: true } }}
            />
            <DatePicker
              label="Data do pedido"
              value={orderDate}
              onChange={setOrderDate}
              format="DD/MM/YYYY"
              slotProps={{ textField: { fullWidth: true, required: true } }}
            />
          </Stack>

          <Stack direction="row" spacing={2}>
            <TextField
              label="Valor total"
              type="number"
              value={totalAmount}
              onChange={(e) => setTotalAmount(e.target.value)}
              fullWidth
              required
            />
            <TextField
              label="Quantidade de parcelas"
              type="number"
              value={totalInstallments}
              onChange={(e) => setTotalInstallments(e.target.value)}
              fullWidth
              required
            />
          </Stack>

          <TextField
            label="Observações"
            value={notes}
            onChange={(e) => setNotes(e.target.value)}
            fullWidth
            multiline
            minRows={2}
          />

          {erro && <Alert severity="error">{erro}</Alert>}
        </Stack>
      </DialogContent>
      <DialogActions>
        <Button onClick={fechar}>Cancelar</Button>
        <Button variant="contained" onClick={salvar} loading={mutation.isPending}>
          Salvar
        </Button>
      </DialogActions>
    </Dialog>
  );
}
