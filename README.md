# Projeto 2: Revisão do Melhor Preço 

## Integrantes da Dupla
* Eduardo Cardoso 
* Talles Souza

## Objetivo do Projeto
* Este projeto é uma evolução do buscador e comparador de preços para supermercados (Projeto 1).
* O objetivo principal desta segunda etapa foi resolver o problema de latência e o alto custo de processamento gerado pelas requisições web, implementando uma arquitetura de Cache Local Persistente.
* O sistema agora é capaz de memorizar os produtos pesquisados em execuções anteriores, acessando a API dos supermercados apenas quando é estritamente necessário e realizando requisições pontuais por ID apenas para garantir a atualização dos preços em tempo real.

## Arquitetura e Estruturas de Dados
* **Lista Sequencial (`ListaSequencial<T>`)**: Estrutura central utilizada para o armazenamento das coleções, fornecendo acesso indexado e incluindo a implementação do Merge Sort para organizar os orçamentos.
* **Tabela Hash Dupla (`TabHash<K, V>`)**: Responsável pelo gerenciamento assíncrono dos termos e produtos dentro do `CacheSupermercado`, garantindo consultas locais instantâneas pelas palavras-chave.
* **Normalização de Cache via Double Hash**: O arquivo de cache é dividido em prefixos `TERMO` (Mapeamento Busca para IDs) e `PROD` (Mapeamento ID para Objeto), evitando duplicação em memória e economizando espaço em disco.
* **Cache Negativo**: Se a API não retornar nada na busca, o sistema salva um Cache Negativo (tag VAZIO) para não repetir essa busca inútil no futuro.

## Configuração da Lista de Compras
* O arquivo de entrada do usuário, `lista-compras.txt`, permite que o usuário defina múltiplos produtos e suas restrições.
* O formato esperado para cada linha é delimitado por ponto e vírgula (`;`): `Termo de Busca; Termos Obrigatórios (vírgula); Marcas Aceitáveis`.
* Campos vazios após o ponto e vírgula significam que não há restrições obrigatórias para aquele critério.

## Manual de Implantação e Execução

### Pré-requisitos
* Java Development Kit (JDK) 11 ou superior instalado (Recomendado Java 20+).
* Bibliotecas `org.json` e `Lombok` configuradas no classpath ou gerenciadas via Gradle.

### Passos para Execução
1. Utilize uma IDE (como IntelliJ IDEA ou Eclipse) e importe o projeto.
2. Certifique-se de que o arquivo `lista-compras.txt` esteja criado e populado na raiz do diretório de execução.
3. Execute o método main da classe `Main.java`.

### Saída Esperada
* Se for a primeira execução (sem cache gerado), o console exibirá avisos informando a busca dos dados na API.
* Ao término, os orçamentos são ranqueados, priorizando cestas completas e depois os menores preços.
* Ao realizar novamente a mesma busca, o usuário é informado que a pesquisa está sendo feita diretamente no cache criado anteriormente, poupando tempo e recursos.
