import java.util.*;

public class FileSystemSimulator {

    private static class Diretorio {
        String nome;
        Map<String, Diretorio> subdiretorios = new HashMap<>();
        List<String> arquivos = new ArrayList<>();

        Diretorio(String nome) {
            this.nome = nome;
        }
    }

    private static Diretorio raiz = new Diretorio("raiz");
    private static Diretorio diretorioAtual = raiz;

    private static void exibirDiretorioAtual() {
        System.out.println("\nVocê está em: /" + obterCaminhoCompleto(diretorioAtual));
    }

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
            System.out.println("Erro: O arquivo já existe.");
        } else {
            diretorioAtual.arquivos.add(nomeArquivo);
            System.out.println("Arquivo '" + nomeArquivo + "' criado com sucesso.");
        }
    }

    private static void criarDiretorio(String nomeDiretorio) {
        if (diretorioAtual.subdiretorios.containsKey(nomeDiretorio)) {
            System.out.println("Erro: O diretório já existe.");
        } else {
            diretorioAtual.subdiretorios.put(nomeDiretorio, new Diretorio(nomeDiretorio));
            System.out.println("Diretório '" + nomeDiretorio + "' criado com sucesso.");
        }
    }

    private static void mudarDiretorio(String caminho) {
        if (caminho.equals("..")) {
            if (diretorioAtual != raiz) {
                diretorioAtual = encontrarPai(diretorioAtual);
                System.out.println("Retornou ao diretório: /" + obterCaminhoCompleto(diretorioAtual));
            } else {
                System.out.println("Erro: Já está no diretório raiz.");
            }
        } else {
            Diretorio novoDiretorio = diretorioAtual.subdiretorios.get(caminho);
            if (novoDiretorio != null) {
                diretorioAtual = novoDiretorio;
                System.out.println("Mudou para o diretório: /" + obterCaminhoCompleto(diretorioAtual));
            } else {
                System.out.println("Erro: Diretório não encontrado.");
            }
        }
    }

    private static void listarItens() {
        System.out.println("\n--- Conteúdo de /" + obterCaminhoCompleto(diretorioAtual) + " ---");
        for (String arquivo : diretorioAtual.arquivos) {
            System.out.println("Arquivo: " + arquivo);
        }
        for (String subdir : diretorioAtual.subdiretorios.keySet()) {
            System.out.println("Diretório: " + subdir);
        }
        System.out.println("--- Fim da Lista ---\n");
    }

    private static void exibirArvore(Diretorio dir, String prefixo) {
        System.out.println(prefixo + "|-- " + dir.nome);
        for (String arquivo : dir.arquivos) {
            System.out.println(prefixo + "    |-- " + arquivo);
        }
        for (Diretorio subdir : dir.subdiretorios.values()) {
            exibirArvore(subdir, prefixo + "    ");
        }
    }

    private static void exibirMenu() {
        System.out.println("Comandos disponíveis:");
        System.out.println("1. cd [diretório] - Navegar para um diretório");
        System.out.println("2. cd .. - Retornar ao diretório pai");
        System.out.println("3. mkdir [nome_diretorio] - Criar um novo diretório");
        System.out.println("4. criar [nome_arquivo] - Criar um novo arquivo");
        System.out.println("5. listar - Listar arquivos e diretórios");
        System.out.println("6. arvore - Exibir a árvore de diretórios a partir do atual");
        System.out.println("7. sair - Sair do programa");
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Bem-vindo ao Gerenciador de Arquivos Fictício");
        exibirDiretorioAtual();

        while (true) {
            exibirMenu();
            System.out.print("\nDigite um comando: ");
            String comando = scanner.nextLine().trim();

            if (comando.equals("sair")) {
                System.out.println("Encerrando o programa.");
                break;
            } else if (comando.startsWith("cd ")) {
                String caminho = comando.substring(3).trim();
                mudarDiretorio(caminho);
            } else if (comando.startsWith("mkdir ")) {
                String nomeDiretorio = comando.substring(6).trim();
                criarDiretorio(nomeDiretorio);
            } else if (comando.startsWith("criar ")) {
                String nomeArquivo = comando.substring(6).trim();
                criarArquivo(nomeArquivo);
            } else if (comando.equals("listar")) {
                listarItens();
            } else if (comando.equals("arvore")) {
                System.out.println("\n--- Árvore de Diretórios ---");
                exibirArvore(raiz, "");
                System.out.println("--- Fim da Árvore ---\n");
            } else {
                System.out.println("Comando inválido. Tente novamente.");
            }
            exibirDiretorioAtual();
        }

        scanner.close();
    }
}
