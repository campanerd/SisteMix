import { useEffect, useState } from 'react';
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
import { isAxiosError } from 'axios';
import dayjs, { type Dayjs } from 'dayjs';
import { listSellers } from '../api/sellers';
import { updateOrder } from '../api/orders';
import type { OrderResponse, SellerSummary } from '../types';

interface EditarPedidoDialogProps {
  open: boolean;
  pedido: OrderResponse;
  onClose: () => void;
  onAtualizado: () => void;
}

export function EditarPedidoDialog({ open, pedido, onClose, onAtualizado }: EditarPedidoDialogProps) {
  const queryClient = useQueryClient();

  const [issueDate, setIssueDate] = useState<Dayjs | null>(dayjs(pedido.issueDate));
  const [orderDate, setOrderDate] = useState<Dayjs | null>(dayjs(pedido.orderDate));
  const [totalAmount, setTotalAmount] = useState(String(pedido.totalAmount));
  const [notes, setNotes] = useState(pedido.notes ?? '');
  const [seller, setSeller] = useState<SellerSummary>({ id: pedido.sellerId, name: pedido.sellerName, cpf: null });
  const [erro, setErro] = useState<string | null>(null);

  // Reabre com os dados atuais do pedido a cada vez que o diálogo é aberto
  useEffect(() => {
    if (open) {
      setIssueDate(dayjs(pedido.issueDate));
      setOrderDate(dayjs(pedido.orderDate));
      setTotalAmount(String(pedido.totalAmount));
      setNotes(pedido.notes ?? '');
      setSeller({ id: pedido.sellerId, name: pedido.sellerName, cpf: null });
      setErro(null);
    }
  }, [open, pedido]);

  const { data: vendedores, isLoading: carregandoVendedores } = useQuery({
    queryKey: ['sellers-autocomplete'],
    queryFn: () => listSellers(),
    enabled: open,
  });

  const mutation = useMutation({
    mutationFn: updateOrder,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['order', pedido.id] });
      queryClient.invalidateQueries({ queryKey: ['order-installments', pedido.id] });
      queryClient.invalidateQueries({ queryKey: ['order-history', pedido.id] });
      queryClient.invalidateQueries({ queryKey: ['installments'] });
      queryClient.invalidateQueries({ queryKey: ['dashboard-summary'] });
      onAtualizado();
      onClose();
    },
    onError: (error) => {
      const mensagem = isAxiosError(error) ? error.response?.data?.message : null;
      setErro(mensagem ?? 'Erro ao atualizar o pedido.');
    },
  });

  function salvar() {
    if (!issueDate || !orderDate || !totalAmount || !seller) {
      setErro('Preencha todos os campos obrigatórios.');
      return;
    }
    setErro(null);
    mutation.mutate({
      id: pedido.id,
      issueDate: issueDate.format('YYYY-MM-DD'),
      orderDate: orderDate.format('YYYY-MM-DD'),
      totalAmount: Number(totalAmount),
      notes: notes || undefined,
      sellerId: seller.id,
    });
  }

  return (
    <Dialog open={open} onClose={onClose} maxWidth="sm" fullWidth>
      <DialogTitle>Editar Pedido {pedido.orderNumber}</DialogTitle>
      <DialogContent>
        <Stack spacing={2} sx={{ mt: 1 }}>
          <Autocomplete
            options={vendedores?.content ?? []}
            getOptionLabel={(option) => option.name}
            isOptionEqualToValue={(option, value) => option.id === value.id}
            loading={carregandoVendedores}
            value={seller}
            onChange={(_event, novoValor) => novoValor && setSeller(novoValor)}
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

          <TextField
            label="Valor total"
            type="number"
            value={totalAmount}
            onChange={(e) => setTotalAmount(e.target.value)}
            fullWidth
            required
          />

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
        <Button onClick={onClose}>Cancelar</Button>
        <Button variant="contained" onClick={salvar} loading={mutation.isPending}>
          Salvar
        </Button>
      </DialogActions>
    </Dialog>
  );
}
