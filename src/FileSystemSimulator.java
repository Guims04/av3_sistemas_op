import java.io.IOException;
import java.util.Scanner;

public class FileSystemSimulator {

    private static final Journal journal = new Journal();

    // Método para criar um arquivo
    public static void criarArquivo(Directory diretorio, String nome) {
        if (diretorio.contemArquivo(nome)) {
            System.out.println("Arquivo " + nome + " já existe no diretório.");
        } else {
            diretorio.adicionarArquivo(new File(nome));
            System.out.println("Arquivo " + nome + " criado no diretório " + diretorio.getNome());
            journal.registrarOperacao("touch " + diretorio.getNome() + "/" + nome);
        }
    }

    // Método para criar um diretório
    public static void criarDiretorio(Directory diretorioPai, String nome) {
        if (diretorioPai.contemDiretorio(nome)) {
            System.out.println("Diretório " + nome + " já existe.");
        } else {
            diretorioPai.adicionarDiretorio(new Directory(nome));
            System.out.println("Diretório " + nome + " criado em " + diretorioPai.getNome());
            journal.registrarOperacao("mkdir " + diretorioPai.getNome() + "/" + nome);
        }
    }

    // Método para listar o conteúdo de um diretório
    public static void listarDiretorio(Directory diretorio) {
        System.out.println("Conteúdo do diretório " + diretorio.getNome() + ":");
        diretorio.listarConteudo();
        journal.registrarOperacao("ls " + diretorio.getNome());
    }

    // Método para apagar um arquivo
    public static void apagarArquivo(Directory diretorio, String nome) {
        if (diretorio.removerArquivo(nome)) {
            System.out.println("Arquivo " + nome + " removido do diretório " + diretorio.getNome());
            journal.registrarOperacao("rm " + diretorio.getNome() + "/" + nome);
        } else {
            System.out.println("Arquivo " + nome + " não encontrado.");
        }
    }

    // Método para apagar um diretório
    public static void apagarDiretorio(Directory diretorioPai, String nome) {
        if (diretorioPai.removerDiretorio(nome)) {
            System.out.println("Diretório " + nome + " removido de " + diretorioPai.getNome());
            journal.registrarOperacao("rm " + diretorioPai.getNome() + "/" + nome);
        } else {
            System.out.println("Diretório " + nome + " não encontrado.");
        }
    }

    // Método para exibir ajuda
    public static void exibirAjuda() {
        System.out.println("Comandos disponíveis:");
        System.out.println("  mkdir <nome>         - Criar um diretório");
        System.out.println("  touch <nome>         - Criar um arquivo");
        System.out.println("  rm <nome>            - Apagar um arquivo ou diretório");
        System.out.println("  ls                   - Listar o conteúdo do diretório atual");
        System.out.println("  help                 - Mostrar esta ajuda");
        System.out.println("  exit                 - Encerrar o simulador");
    }

    // Modo Shell com suporte a comandos
    public static void modoShell() {
        Scanner scanner = new Scanner(System.in);
        Directory raiz = new Directory("raiz");
        Directory diretorioAtual = raiz;

        System.out.println("Bem-vindo ao simulador de sistema de arquivos!");
        System.out.println("Digite 'help' para ver a lista de comandos disponíveis.");

        while (true) {
            System.out.print("\n" + diretorioAtual.getNome() + " > ");
            String comando = scanner.nextLine();
            String[] partes = comando.split(" ");

            try {
                switch (partes[0].toLowerCase()) {
                    case "mkdir":
                        if (partes.length == 2) {
                            criarDiretorio(diretorioAtual, partes[1]);
                        } else {
                            System.out.println("Uso: mkdir <nome>");
                        }
                        break;
                    case "touch":
                        if (partes.length == 2) {
                            criarArquivo(diretorioAtual, partes[1]);
                        } else {
                            System.out.println("Uso: touch <nome>");
                        }
                        break;
                    case "rm":
                        if (partes.length == 2) {
                            apagarArquivo(diretorioAtual, partes[1]);
                        } else {
                            System.out.println("Uso: rm <nome>");
                        }
                        break;
                    case "ls":
                        listarDiretorio(diretorioAtual);
                        break;
                    case "help":
                        exibirAjuda();
                        break;
                    case "exit":
                        System.out.println("Encerrando o simulador. Até logo!");
                        scanner.close();
                        return;
                    default:
                        System.out.println("Comando inválido. Digite 'help' para ajuda.");
                }
            } catch (Exception e) { // Captura de exceções genéricas, se necessário
                System.err.println("Erro inesperado: " + e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        modoShell();
    }
}
