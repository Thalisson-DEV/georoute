# Configuração
$BaseUrl = "http://localhost:8080/api/v1"
$Iterations = 50 # Quantidade de requisições a fazer
$SleepMs = 500   # Pausa entre requisições (0.5s)

Write-Host "=== Iniciando Teste de Carga e Observabilidade ===" -ForegroundColor Cyan
Write-Host "Alvo: $BaseUrl"
Write-Host "Iterações: $Iterations"
Write-Host "------------------------------------------------"

for ($i = 1; $i -le $Iterations; $i++) {
    $Percent = ($i / $Iterations) * 100
    Write-Progress -Activity "Gerando Tráfego" -Status "Enviando requisição $i de $Iterations" -PercentComplete $Percent

    # 1. Simular Busca de Cliente (Aleatório)
    $TipoBusca = Get-Random -Minimum 1 -Maximum 5
    try {
        switch ($TipoBusca) {
            1 { 
                # Busca por Instalação
                $Id = Get-Random -Minimum 100000 -Maximum 999999
                Invoke-RestMethod -Method Get -Uri "$BaseUrl/clientes/instalacao/$Id" -ErrorAction SilentlyContinue
                Write-Host "[$i] GET /instalacao/$Id (Simulado)" -ForegroundColor Green
            }
            2 { 
                # Busca por Conta Contrato
                $Id = Get-Random -Minimum 10000000 -Maximum 99999999
                Invoke-RestMethod -Method Get -Uri "$BaseUrl/clientes/conta-contrato/$Id" -ErrorAction SilentlyContinue
                Write-Host "[$i] GET /conta-contrato/$Id (Simulado)" -ForegroundColor Green
            }
            3 { 
                # Busca por Número de Série
                $Id = Get-Random -Minimum 500 -Maximum 5000
                Invoke-RestMethod -Method Get -Uri "$BaseUrl/clientes/numero-serie/$Id" -ErrorAction SilentlyContinue
                Write-Host "[$i] GET /numero-serie/$Id (Simulado)" -ForegroundColor Green
            }
            4 { 
                # Busca por Poste
                $Letra = [char](Get-Random -Minimum 65 -Maximum 90)
                $Num = Get-Random -Minimum 100 -Maximum 999
                $Poste = "$Letra-$Num"
                Invoke-RestMethod -Method Get -Uri "$BaseUrl/clientes/numero-poste/$Poste" -ErrorAction SilentlyContinue
                Write-Host "[$i] GET /numero-poste/$Poste (Simulado)" -ForegroundColor Green
            }
        }
    } catch {
        # Ignoramos erros 404/500 pois o objetivo é incrementar o contador de tentativas
        Write-Host "[$i] Request feita (Retorno esperado de erro se DB vazio)" -ForegroundColor Gray
    }

    # 2. Simular Redirecionamento de Mapa
    try {
        $Lat = -23.5505 + (Get-Random -Minimum -100 -Maximum 100) / 10000
        $Lng = -46.6333 + (Get-Random -Minimum -100 -Maximum 100) / 10000
        
        $Body = @{
            latitude = $Lat
            longitude = $Lng
        } | ConvertTo-Json

        # Nota: GET com Body é atípico, mas suportado pelo seu Controller atual
        Invoke-RestMethod -Method Get -Uri "$BaseUrl/maps/redirect" -Body $Body -ContentType "application/json" -ErrorAction SilentlyContinue
        Write-Host "[$i] GET /maps/redirect (Lat: $Lat, Lng: $Lng)" -ForegroundColor Yellow
    } catch {
         Write-Host "[$i] Erro no redirect (pode ser esperado em teste)" -ForegroundColor Gray
    }

    Start-Sleep -Milliseconds $SleepMs
}

Write-Host "=== Teste Finalizado ===" -ForegroundColor Cyan
Write-Host "Verifique o Grafana em http://localhost:3000"
