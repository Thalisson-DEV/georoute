import { cleanupOldRoutes } from "../modules/routes/route.service.ts";

// Agenda a limpeza de rotas antigas para toda meia-noite (cron "0 0 0 * * *" do Java).
// Implementação leve com setTimeout recalculado, sem dependências externas.
function msUntilNextMidnight(): number {
  const now = new Date();
  const next = new Date(now);
  next.setHours(24, 0, 0, 0);
  return next.getTime() - now.getTime();
}

async function runCleanup(): Promise<void> {
  try {
    await cleanupOldRoutes();
    console.log("[cron] limpeza de rotas antigas concluída.");
  } catch (err) {
    console.error("[cron] falha na limpeza de rotas:", err);
  }
}

export function startCleanupScheduler(): void {
  const schedule = () => {
    setTimeout(async () => {
      await runCleanup();
      schedule(); // reagenda para a próxima meia-noite
    }, msUntilNextMidnight());
  };
  schedule();
  console.log("[cron] agendador de limpeza diária iniciado.");
}
