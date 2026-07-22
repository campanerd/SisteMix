import { Box, Paper, Typography, alpha } from '@mui/material';
import type { SvgIconProps } from '@mui/material';
import { useQuery } from '@tanstack/react-query';
import ReceiptLongIcon from '@mui/icons-material/ReceiptLong';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import HourglassEmptyIcon from '@mui/icons-material/HourglassEmpty';
import WarningIcon from '@mui/icons-material/Warning';
import PaidIcon from '@mui/icons-material/Paid';
import AccountBalanceWalletIcon from '@mui/icons-material/AccountBalanceWallet';
import type { ComponentType } from 'react';
import { getDashboardSummary } from '../api/dashboard';
import { formatarMoeda, formatarPercentual } from '../utils/format';

type CorIndicador = 'success' | 'warning' | 'error' | 'info';

interface CardIndicador {
  titulo: string;
  valor: string;
  percentual?: string;
  icone: ComponentType<SvgIconProps>;
  cor: CorIndicador;
}

function IndicadorCard({ titulo, valor, percentual, icone: Icone, cor, carregando }: CardIndicador & { carregando: boolean }) {
  return (
    <Paper
      elevation={0}
      sx={{
        p: 2.5,
        display: 'flex',
        alignItems: 'center',
        gap: 2,
        borderRadius: 3,
        boxShadow: '0 1px 4px rgba(0,0,0,0.06)',
      }}
    >
      <Box
        sx={{
          width: 48,
          height: 48,
          flexShrink: 0,
          borderRadius: '50%',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          bgcolor: (theme) => alpha(theme.palette[cor].main, 0.12),
        }}
      >
        <Icone sx={{ fontSize: 24, color: `${cor}.main` }} />
      </Box>
      <Box>
        <Typography variant="body2" color="text.secondary">
          {titulo}
        </Typography>
        <Typography variant="h5" sx={{ fontWeight: 700, lineHeight: 1.2 }}>
          {carregando ? '...' : valor}
        </Typography>
        {percentual && !carregando && (
          <Typography variant="caption" sx={{ color: `${cor}.main`, fontWeight: 600 }}>
            {percentual}
          </Typography>
        )}
      </Box>
    </Paper>
  );
}

function GrupoCards({ titulo, cards, carregando }: { titulo: string; cards: CardIndicador[]; carregando: boolean }) {
  return (
    <Box sx={{ mb: 3 }}>
      <Typography
        variant="overline"
        sx={{ mb: 1, display: 'block', fontWeight: 700, letterSpacing: 1, color: 'text.secondary' }}
      >
        {titulo}
      </Typography>
      <Box
        sx={{
          display: 'grid',
          gridTemplateColumns: { xs: '1fr', sm: '1fr 1fr', md: `repeat(${cards.length}, 1fr)` },
          gap: 2,
        }}
      >
        {cards.map((card) => (
          <IndicadorCard key={card.titulo} {...card} carregando={carregando} />
        ))}
      </Box>
    </Box>
  );
}

export function Acompanhamento() {
  const { data, isLoading, isError } = useQuery({
    queryKey: ['dashboard-summary'],
    queryFn: getDashboardSummary,
  });

  const totalParcelas = data ? data.paidCount + data.pendingCount + data.overdueCount : 0;
  const totalFinanceiro = data ? data.totalReceivedAmount + data.totalToReceiveAmount : 0;

  const cardsParcelas: CardIndicador[] = [
    {
      titulo: 'Parcelas pagas',
      valor: data ? String(data.paidCount) : '-',
      percentual: data ? formatarPercentual(data.paidCount, totalParcelas) : undefined,
      icone: CheckCircleIcon,
      cor: 'success',
    },
    {
      titulo: 'Parcelas pendentes',
      valor: data ? String(data.pendingCount) : '-',
      percentual: data ? formatarPercentual(data.pendingCount, totalParcelas) : undefined,
      icone: HourglassEmptyIcon,
      cor: 'warning',
    },
    {
      titulo: 'Parcelas em atraso',
      valor: data ? String(data.overdueCount) : '-',
      percentual: data ? formatarPercentual(data.overdueCount, totalParcelas) : undefined,
      icone: WarningIcon,
      cor: 'error',
    },
  ];

  const cardsFinanceiro: CardIndicador[] = [
    {
      titulo: 'Valor recebido',
      valor: data ? formatarMoeda(data.totalReceivedAmount) : '-',
      percentual: data ? formatarPercentual(data.totalReceivedAmount, totalFinanceiro) : undefined,
      icone: PaidIcon,
      cor: 'success',
    },
    {
      titulo: 'Valor a receber',
      valor: data ? formatarMoeda(data.totalToReceiveAmount) : '-',
      percentual: data ? formatarPercentual(data.totalToReceiveAmount, totalFinanceiro) : undefined,
      icone: AccountBalanceWalletIcon,
      cor: 'info',
    },
  ];

  return (
    <Box>
      <Typography variant="h5" sx={{ mb: 2 }}>
        Acompanhamento
      </Typography>

      {isError && <Typography color="error">Erro ao carregar os indicadores.</Typography>}

      <Box sx={{ maxWidth: 320, mb: 3 }}>
        <IndicadorCard
          titulo="Pedidos ativos"
          valor={data ? String(data.activeOrdersCount) : '-'}
          icone={ReceiptLongIcon}
          cor="info"
          carregando={isLoading}
        />
      </Box>

      <GrupoCards titulo="Parcelas por status" cards={cardsParcelas} carregando={isLoading} />
      <GrupoCards titulo="Financeiro" cards={cardsFinanceiro} carregando={isLoading} />
    </Box>
  );
}
