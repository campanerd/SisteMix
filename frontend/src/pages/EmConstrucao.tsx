import { Box, Typography } from '@mui/material';
import ConstructionIcon from '@mui/icons-material/Construction';

export function EmConstrucao({ titulo }: { titulo: string }) {
  return (
    <Box sx={{ textAlign: 'center', mt: 8, color: 'text.secondary' }}>
      <ConstructionIcon sx={{ fontSize: 64, mb: 2 }} />
      <Typography variant="h5">{titulo}</Typography>
      <Typography>Esta tela ainda será construída.</Typography>
    </Box>
  );
}
