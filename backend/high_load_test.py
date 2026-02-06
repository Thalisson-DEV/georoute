import concurrent.futures
import requests
import random
import time
import string

# Configuração
BASE_URL = "http://localhost:8080/api/v1"
TOTAL_REQUESTS = 100000
CONCURRENCY = 50  # Número de threads simultâneas

# Contadores
success_count = 0
error_count = 0
start_time = 0

def get_random_string(length):
    return ''.join(random.choices(string.ascii_uppercase, k=length))

def make_request(_):
    global success_count, error_count
    
    # Escolhe aleatoriamente um tipo de busca (pesos ajustados para realismo)
    choice = random.randint(1, 4)
    
    try:
        url = ""
        if choice == 1:
            # Busca por Instalação
            inst_id = random.randint(100000, 999999)
            url = f"{BASE_URL}/clientes/instalacao/{inst_id}"
        elif choice == 2:
            # Busca por Conta Contrato
            cc_id = random.randint(10000000, 99999999)
            url = f"{BASE_URL}/clientes/conta-contrato/{cc_id}"
        elif choice == 3:
            # Busca por Número de Série
            ns_id = random.randint(500, 5000)
            url = f"{BASE_URL}/clientes/numero-serie/{ns_id}"
        elif choice == 4:
            # Busca por Poste
            letra = get_random_string(1)
            num = random.randint(100, 999)
            poste = f"{letra}-{num}"
            url = f"{BASE_URL}/clientes/numero-poste/{poste}"

        # Timeout curto para falhar rápido se o servidor engasgar
        response = requests.get(url, timeout=5)
        
        # Consideramos sucesso 200 (Encontrado) ou 404 (Não encontrado, mas serviço respondeu)
        if response.status_code in [200, 404]:
            return True
        else:
            return False
            
    except Exception as e:
        return False

def run_load_test():
    global start_time, success_count, error_count
    
    print(f"=== Iniciando Teste de Carga ===")
    print(f"Alvo: {BASE_URL}")
    print(f"Total de Requisições: {TOTAL_REQUESTS}")
    print(f"Concorrência: {CONCURRENCY} threads")
    print(f"--------------------------------")
    
    start_time = time.time()
    
    with concurrent.futures.ThreadPoolExecutor(max_workers=CONCURRENCY) as executor:
        # Submete todas as tarefas
        futures = [executor.submit(make_request, i) for i in range(TOTAL_REQUESTS)]
        
        # Monitora o progresso
        for i, future in enumerate(concurrent.futures.as_completed(futures)):
            result = future.result()
            if result:
                success_count += 1
            else:
                error_count += 1
            
            if (i + 1) % 1000 == 0:
                elapsed = time.time() - start_time
                rate = (i + 1) / elapsed
                print(f"Progresso: {i + 1}/{TOTAL_REQUESTS} ({((i+1)/TOTAL_REQUESTS)*100:.1f}%) - Taxa: {rate:.1f} req/s")

    end_time = time.time()
    duration = end_time - start_time
    
    print(f"\n=== Teste Finalizado ===")
    print(f"Duração Total: {duration:.2f} segundos")
    print(f"Taxa Média: {TOTAL_REQUESTS / duration:.1f} req/s")
    print(f"Sucessos (200/404): {success_count}")
    print(f"Falhas (500/Timeout/Connection Error): {error_count}")

if __name__ == "__main__":
    run_load_test()
