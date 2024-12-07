# Relatório de Simulador de Sistema de Arquivos com Journaling

## Metodologia

O simulador será desenvolvido em linguagem de programação Java. Ele receberá as chamadas de métodos com os devidos parâmetros. Em seguida, serão implementados os métodos correspondentes aos comandos de um sistema operacional. O simulador será responsável por executar operações típicas de gerenciamento de arquivos e diretórios, como criação, exclusão e listagem de arquivos e diretórios. Além disso ele terá um arquivo próprio em .dat o qual irá responder ao estado do sistema.

O programa executará cada funcionalidade e exibirá o resultado na tela quando necessário, proporcionando uma interface simples para interação e visualização das operações realizadas no sistema de arquivos simulado.

## Parte 1: Introdução ao Sistema de Arquivos com Journaling

### Descrição do Sistema de Arquivos

Um sistema de arquivos é a estrutura que permite armazenar, organizar e recuperar dados em dispositivos de armazenamento, como discos rígidos, SSDs ou qualquer outro tipo de mídia persistente. Ele gerencia como os dados são salvos e acessados, além de oferecer funcionalidades de manipulação, como leitura, gravação e exclusão de arquivos e diretórios.

A importância de um sistema de arquivos é fundamental, pois ele fornece uma camada de abstração que facilita o gerenciamento de dados para o usuário e para os programas. Sem um sistema de arquivos eficiente, a manipulação de grandes volumes de dados seria complexa e propensa a erros.

### Journaling

O journaling é uma técnica usada em sistemas de arquivos para melhorar a confiabilidade e a recuperação de dados após falhas. O propósito do journaling é registrar as operações realizadas no sistema de arquivos em um "diário" (journal) antes que as modificações sejam feitas no armazenamento permanente. Em caso de falha (como queda de energia ou falha no sistema), o sistema pode recuperar ou desfazer as operações incompletas a partir do log.

Existem diferentes tipos de journaling, como:

- **Write-Ahead Logging (WAL)**: Nesse tipo de journaling, os dados são primeiramente gravados no log antes de serem aplicados no armazenamento real. Isso garante que, mesmo em caso de falha, as operações possam ser recuperadas.
- **Log-Structured File Systems (LSFS)**: Um sistema de arquivos estruturado por log registra todas as operações de arquivo como entradas sequenciais. Isso pode melhorar o desempenho em alguns cenários, como operações de gravação frequentes.
  
No contexto deste simulador, implementaremos um mecanismo básico de journaling, onde as operações realizadas no sistema de arquivos serão registradas em um log para possibilitar recuperação ou auditoria.

## Parte 2: Arquitetura do Simulador

### Estrutura de Dados

O simulador usará classes Java para representar os componentes do sistema de arquivos. As principais classes envolvidas serão:

- **File**: Representa um arquivo dentro de um diretório.
- **Directory**: Representa um diretório que pode conter arquivos e outros diretórios.
- **FileSystemSimulator**: Classe principal que gerencia o sistema de arquivos e executa os comandos simulados.
- **Journal**: Classe responsável pelo gerenciamento do log de operações realizadas, facilitando a implementação do journaling.

Alem disso o .dat é responsável por representar o nosso sistema operacional, o qual apenas será lido e escrito pela nossa aplicação.

#### Estruturas de dados específicas:
- **Lista de arquivos e diretórios**: Usada para armazenar os arquivos e diretórios criados.
- **Log de operações**: Armazena as ações realizadas para possibilitar o journaling, como criação de arquivos, exclusão, etc.

### Journaling

O journaling será implementado por meio de uma classe `Journal`, que manterá um registro das operações executadas no sistema de arquivos. Cada operação (como criação, exclusão ou movimentação de arquivos) será registrada no log antes de ser efetivamente aplicada no sistema. Isso garantirá que, no caso de falhas, seja possível reverter ou aplicar novamente as operações registradas.

A estrutura do log será simples, contendo informações como:
- Tipo de operação (criação, remoção, etc.)
- Objeto afetado (arquivo ou diretório)
- Dados relevantes sobre a operação (nome do arquivo, diretório de destino, etc.)

## Parte 3: Implementação em Java

### Classe "FileSystemSimulator"
A classe `FileSystemSimulator` é o núcleo do simulador. Ela gerencia os arquivos e diretórios, processando os comandos do usuário. Abaixo está um esboço de como seria a classe:


```java
public class FileSystemSimulator {

    private static Diretorio raiz = new Diretorio("root");
    private static Journal journal = new Journal();
    private static Diretorio diretorioAtual = raiz;
    private static Object itemCopiado = null;

    private static String obterCaminhoCompleto(Diretorio dir) {
        if (dir == raiz) {
            return "raiz";
        }
        List<String> partes = new ArrayList<>();
        Diretorio atual = dir;
        while (atual != raiz) {
            partes.add(0, atual.nome);
            atual = encontrarPai(atual);
        }
        return String.join("/", partes);
    }

    private static Diretorio encontrarPai(Diretorio filho) {
        Queue<Diretorio> fila = new LinkedList<>();
        fila.add(raiz);
        while (!fila.isEmpty()) {
            Diretorio atual = fila.poll();
            for (Map.Entry<String, Diretorio> entrada : atual.subdiretorios.entrySet()) {
                if (entrada.getValue() == filho) {
                    return atual;
                }
                fila.add(entrada.getValue());
            }
        }
        return null;
    }

    private static void criarArquivo(String nomeArquivo) {
        if (diretorioAtual.arquivos.contains(nomeArquivo)) {
            journal.log("Erro: O arquivo já existe.");
        } else {
            diretorioAtual.arquivos.add(nomeArquivo);
            journal.log("Arquivo '" + nomeArquivo + "' criado com sucesso.");
        }
    }

    private static void criarDiretorio(String nomeDiretorio) {
        if (diretorioAtual.subdiretorios.containsKey(nomeDiretorio)) {
            journal.log("Erro: O diretório já existe.");
        } else {
            diretorioAtual.subdiretorios.put(nomeDiretorio, new Diretorio(nomeDiretorio));
            journal.log("Diretório '" + nomeDiretorio + "' criado com sucesso.");
        }
    }

    private static void mudarDiretorio(String caminho) {
        if (caminho.equals("..")) {
            if (diretorioAtual != raiz) {
                diretorioAtual = encontrarPai(diretorioAtual);
                journal.log("Retornou ao diretório: /" + obterCaminhoCompleto(diretorioAtual));
            } else {
                journal.log("Erro: Já está no diretório raiz.");
            }
        } else {
            Diretorio novoDiretorio = diretorioAtual.subdiretorios.get(caminho);
            if (novoDiretorio != null) {
                diretorioAtual = novoDiretorio;
                journal.log("Mudou para o diretório: /" + obterCaminhoCompleto(diretorioAtual));
            } else {
                journal.log("Erro: Diretório não encontrado.");
            }
        }
    } ...
```
### Classes "File" e "Directory"

Essas classes são responsáveis por representar os arquivos e diretórios no sistema de arquivos. A classe `File` terá atributos como `nome` e métodos para manipular os arquivos, enquanto `Directory` terá uma lista de arquivos e subdiretórios.

### Classe "Journal"

A classe `Journal` gerencia o log de operações, garantindo que cada ação realizada no sistema de arquivos seja registrada. Ela pode armazenar as operações em uma lista ou em um arquivo de log para posterior recuperação.

```java
public class Journal implements Serializable { // Implementando Serializable
    private static final long serialVersionUID = 1L;  // Adicionando o serialVersionUID (opcional, mas recomendado)

    private List<String> logs;

    public Journal() {
        logs = new ArrayList<>();
    }

    public void log(String message) {
        logs.add(message);
        // Salvar logs em arquivo ou console
        System.out.println(message);
    }

    public void log_no_sout(String message) {
        logs.add(message);
    }

    public List<String> getLogs() {
        return logs;
    }
}
```

## Resultados Esperados

Espera-se que o simulador forneça insights sobre o funcionamento de um sistema de arquivos. Com base nos resultados obtidos, será possível avaliar como a implementação do journaling pode melhorar a confiabilidade das operações de sistema de arquivos, especialmente em cenários de falhas ou inconsistências. Além disso, a implementação permitirá um entendimento mais profundo de como os sistemas operacionais lidam com dados persistentes e a recuperação após falhas.

A partir dessa simulação, será possível explorar e testar o comportamento do sistema de arquivos simulado em várias situações, verificando como ele responde a comandos de manipulação de arquivos e como o journaling ajuda na recuperação e auditoria das operações realizadas.
