import java.util.Scanner;

public class FileSystemSimulator {

    private static final Journal journal = new Journal();

    // Método para criar um arquivo dentro de um diretório
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

    // Método para apagar um arquivo dentro de um diretório.
    public static void apagarArquivoEmDiretorio(Directory diretorioAtual, String caminhoDiretorio, String nomeArquivo) {
        String[] partesCaminho = caminhoDiretorio.split("/");

        Directory diretorio = diretorioAtual;
        for (String parte : partesCaminho) {
            diretorio = diretorio.buscarSubdiretorio(parte);
            if (diretorio == null) {
                System.out.println("Diretório " + parte + " não encontrado no caminho " + caminhoDiretorio);
                return;
            }
        }

        if (diretorio.removerArquivo(nomeArquivo)) {
            System.out.println("Arquivo " + nomeArquivo + " removido de " + caminhoDiretorio);
            journal.registrarOperacao("rm " + caminhoDiretorio + "/" + nomeArquivo);
        } else {
            System.out.println("Arquivo " + nomeArquivo + " não encontrado no diretório " + caminhoDiretorio);
        }
    }

    // Método para exibir ajuda
    public static void exibirAjuda() {
        System.out.println("Comandos disponíveis:");
        System.out.println("  mkdir <nome>                 - Criar um diretório");
        System.out.println("  touch <diretório> <nome>     - Criar um arquivo em um diretório específico");
        System.out.println("  touch <nome>                 - Criar um arquivo no diretório raiz");
        System.out.println("  rm <nome>                    - Apagar um arquivo");
        System.out.println("  rmdir <nome>                 - Apagar um diretório");
        System.out.println("  rmda <caminho_diretorio> <nome_arquivo> - Apaga arquivo de um diretório.");
        System.out.println("  rename <arquivo> <nome>      - Renomeia um arquivo");
        System.out.println("  renamedir <diretorio> <nome> - Renomeia um diretório");
        System.out.println("  ls                           - Listar o conteúdo do diretório atual");
        System.out.println("  lsdir <diretorio>            - Listar o conteúdo do diretório especifico");
        System.out.println("  mv <origem> <destino>        - Mover um arquivo de um diretório para outro");
        System.out.println("  help                         - Mostrar esta ajuda");
        System.out.println("  exit                         - Encerrar o simulador");
    }

    // Método para mover um arquivo de um diretório para outro
    public static void moverArquivo(Directory origem, Directory destino, String nomeArquivo) {
        if (origem.moverArquivo(destino, nomeArquivo)) {
            System.out.println("Arquivo " + nomeArquivo + " movido de " + origem.getNome() + " para " + destino.getNome());
            journal.registrarOperacao("mv " + origem.getNome() + "/" + nomeArquivo + " -> " + destino.getNome());
        } else {
            System.out.println("Arquivo " + nomeArquivo + " não encontrado em " + origem.getNome());
        }
    }

    // Método para renomear um arquivo
    public static void renomearArquivo(Directory diretorio, String nomeAntigo, String novoNome) {
        if (diretorio.renomearArquivo(nomeAntigo, novoNome)) {
            System.out.println("Arquivo " + nomeAntigo + " renomeado para " + novoNome);
            journal.registrarOperacao("rename " + diretorio.getNome() + "/" + nomeAntigo + " -> " + novoNome);
        } else {
            System.out.println("Arquivo " + nomeAntigo + " não encontrado.");
        }
    }

    // Método para copiar um arquivo
    public static void copiarArquivo(Directory diretorio, String nomeOriginal, String nomeCopia) {
        if (diretorio.copiarArquivo(nomeOriginal, nomeCopia)) {
            System.out.println("Arquivo " + nomeOriginal + " copiado para " + nomeCopia);
            journal.registrarOperacao("cp " + diretorio.getNome() + "/" + nomeOriginal + " -> " + nomeCopia);
        } else {
            System.out.println("Arquivo " + nomeOriginal + " não encontrado.");
        }
    }

    // Método para renomear diretório
    public static void renomearDiretorio(Directory diretorio, String nomeAntigo, String novoNome) {
        if (diretorio.renomearDiretorio(nomeAntigo, novoNome)) {
            System.out.println("Diretório " + nomeAntigo + " renomeado para " + novoNome);
            journal.registrarOperacao("renamedir " + nomeAntigo + " -> " + novoNome);
        } else {
            System.out.println("Diretório " + nomeAntigo + " não encontrado.");
        }
    }

    // Método para listar um diretório específico
    public static void listarDiretorioEspecifico(Directory diretorioAtual, String nomeDiretorio) {
        Directory subdiretorio = diretorioAtual.buscarSubdiretorio(nomeDiretorio);
        if (subdiretorio != null) {
            listarDiretorio(subdiretorio);
        } else {
            System.out.println("Diretório " + nomeDiretorio + " não encontrado.");
        }
    }

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
                        if (partes.length == 3) {
                            Directory diretorioAlvo = diretorioAtual.buscarSubdiretorio(partes[1]);
                            if (diretorioAlvo != null) {
                                criarArquivo(diretorioAlvo, partes[2]);
                            } else {
                                System.out.println("Diretório " + partes[1] + " não encontrado.");
                            }
                        } else if (partes.length == 2) {
                            criarArquivo(diretorioAtual, partes[1]);
                        } else {
                            System.out.println("Uso: touch <diretório> <nome>");
                        }
                        break;
                    case "rm":
                        if (partes.length == 2) {
                            apagarArquivo(diretorioAtual, partes[1]);
                        } else {
                            System.out.println("Uso: rm <nome>");
                        }
                        break;
                    case "rmda":
                        if (partes.length == 3) {
                            apagarArquivoEmDiretorio(diretorioAtual, partes[1], partes[2]);
                        } else {
                            System.out.println("Uso: rmda <caminho_diretorio> <nome_arquivo>");
                        }
                        break;
                    case "rmdir":
                        if (partes.length == 2) {
                            apagarDiretorio(diretorioAtual, partes[1]);
                        } else {
                            System.out.println("Uso: rm <nome>");
                        }
                        break;
                    case "ls":
                        listarDiretorio(diretorioAtual);
                        break;
                    case "lsdir":
                        if (partes.length == 2) {
                            listarDiretorioEspecifico(diretorioAtual, partes[1]);
                        } else {
                            System.out.println("Uso: lsdir <nome_do_diretorio>");
                        }
                        break;
                    case "mv":
                        if (partes.length == 3) {
                            Directory destino = diretorioAtual.buscarSubdiretorio(partes[1]);
                            if (destino != null) {
                                moverArquivo(diretorioAtual, destino, partes[2]);
                            } else {
                                System.out.println("Diretório de destino " + partes[1] + " não encontrado.");
                            }
                        } else {
                            System.out.println("Uso: mv <origem> <destino>");
                        }
                        break;
                    case "rename":
                        if (partes.length == 3) {
                            renomearArquivo(diretorioAtual, partes[1], partes[2]);
                        } else {
                            System.out.println("Uso: rename <nome_antigo> <novo_nome>");
                        }
                        break;
                    case "renamedir":
                        if (partes.length == 3) {
                            renomearDiretorio(diretorioAtual, partes[1], partes[2]);
                        } else {
                            System.out.println("Uso: renamedir <nome_antigo> <novo_nome>");
                        }
                        break;
                    case "cp":
                        if (partes.length == 3) {
                            copiarArquivo(diretorioAtual, partes[1], partes[2]);
                        } else {
                            System.out.println("Uso: cp <nome_original> <nome_copia>");
                        }
                        break;
                    case "help":
                        exibirAjuda();
                        break;
                    case "exit":
                        System.out.println("Saindo...");
                        scanner.close();
                        return;
                    default:
                        System.out.println("Comando não reconhecido. Digite 'help' para obter a lista de comandos.");
                }
            } catch (Exception e) {
                System.out.println("Erro: " + e.getMessage());
            }
        }
    }


    public static void main(String[] args) {
        modoShell();
    }
}
