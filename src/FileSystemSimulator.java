import java.io.*;
import java.util.*;

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
    }

    private static void copiarItem(String nome) {
        if (diretorioAtual.arquivos.contains(nome)) {
            itemCopiado = nome; // Copiar o nome do arquivo
            journal.log("Arquivo '" + nome + "' copiado.");
        } else if (diretorioAtual.subdiretorios.containsKey(nome)) {
            itemCopiado = diretorioAtual.subdiretorios.get(nome); // Copiar referência do diretório
            journal.log("Diretório '" + nome + "' copiado.");
        } else {
            journal.log("Erro: Arquivo ou diretório não encontrado para copiar.");
        }
    }

    private static void colarItem() {
        if (itemCopiado == null) {
            journal.log("Erro: Nenhum item copiado.");
        } else if (itemCopiado instanceof String) {
            String nomeArquivo = (String) itemCopiado;
            if (!diretorioAtual.arquivos.contains(nomeArquivo)) {
                diretorioAtual.arquivos.add(nomeArquivo);
                journal.log("Arquivo '" + nomeArquivo + "' colado.");
            } else {
                journal.log("Erro: O arquivo já existe no diretório atual.");
            }
        } else if (itemCopiado instanceof Diretorio) {
            Diretorio diretorioCopiado = (Diretorio) itemCopiado;
            if (!diretorioAtual.subdiretorios.containsKey(diretorioCopiado.nome)) {
                Diretorio novoDiretorio = copiarDiretorioRecursivamente(diretorioCopiado);
                diretorioAtual.subdiretorios.put(novoDiretorio.nome, novoDiretorio);
                journal.log("Diretório '" + diretorioCopiado.nome + "' colado.");
            } else {
                journal.log("Erro: O diretório já existe no diretório atual.");
            }
        }
    }

    private static Diretorio copiarDiretorioRecursivamente(Diretorio original) {
        Diretorio copia = new Diretorio(original.nome);
        copia.arquivos.addAll(original.arquivos);
        for (Map.Entry<String, Diretorio> entrada : original.subdiretorios.entrySet()) {
            copia.subdiretorios.put(entrada.getKey(), copiarDiretorioRecursivamente(entrada.getValue()));
        }
        return copia;
    }

    private static void exibirArvore(Diretorio dir, String prefixo) {
        journal.log(prefixo + "|-- " + dir.nome);
        for (String arquivo : dir.arquivos) {
            journal.log(prefixo + "    |-- " + arquivo);
        }
        for (Diretorio subdir : dir.subdiretorios.values()) {
            exibirArvore(subdir, prefixo + "    ");
        }
    }

    private static void renomearDiretorio(String nomeAntigo, String nomeNovo) {
        if (diretorioAtual.subdiretorios.containsKey(nomeAntigo)) {
            Diretorio diretorio = diretorioAtual.subdiretorios.remove(nomeAntigo);
            diretorio.nome = nomeNovo;
            diretorioAtual.subdiretorios.put(nomeNovo, diretorio);
            journal.log("Diretório '" + nomeAntigo + "' renomeado para '" + nomeNovo + "'.");
        } else {
            journal.log("Erro: Diretório não encontrado para renomear.");
        }
    }

    private static void renomearArquivo(String nomeAntigo, String nomeNovo) {
        if (diretorioAtual.arquivos.contains(nomeAntigo)) {
            int indice = diretorioAtual.arquivos.indexOf(nomeAntigo);
            diretorioAtual.arquivos.set(indice, nomeNovo);
            journal.log("Arquivo '" + nomeAntigo + "' renomeado para '" + nomeNovo + "'.");
        } else {
            journal.log("Erro: Arquivo não encontrado para renomear.");
        }
    }

    private static void exibirMenu() {
        System.out.println("\nComandos disponíveis:");
        System.out.println("1. cd [diretório] - Navegar para um diretório");
        System.out.println("2. cd .. - Retornar ao diretório pai");
        System.out.println("3. mkdir [nome_diretorio] - Criar um novo diretório");
        System.out.println("4. touch [nome_arquivo] - Criar um novo arquivo");
        System.out.println("5. ls - Listar arquivos e diretórios");
        System.out.println("6. rm [nome] - Excluir um arquivo ou diretório");
        System.out.println("7. copy [nome] - Copiar um arquivo ou diretório");
        System.out.println("8. paste - Colar o arquivo ou diretório copiado");
        System.out.println("9. renamedir [nome_antigo] [nome_novo] - Renomear diretório");
        System.out.println("10. rename [nome_antigo] [nome_novo] - Renomear arquivos");
        System.out.println("11. open_journal - Mostra os logs do journal");
        System.out.println("12. tree - Mostra a arvore dos diretórios e arquivos");
        System.out.println("13. help - Mostra o menu de comandos");
        System.out.println("14. exit - Sair do programa");
    }

    public static void saveState() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("filesystem.dat"))) {
            oos.writeObject(raiz);  // Salva o diretório raiz
            oos.writeObject(journal); // Salva o journal
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadState() {
        File file = new File("filesystem.dat");
        if (!file.exists()) {
            System.out.println("Arquivo filesystem.dat não encontrado. Criando novo estado inicial...");
            saveState(); // Salva o estado inicial (diretório raiz vazio e journal vazio)
            return;
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("filesystem.dat"))) {
            raiz = (Diretorio) ois.readObject();
            journal = (Journal) ois.readObject();
            diretorioAtual = raiz;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Bem-vindo ao Gerenciador de Arquivos");
        exibirMenu();
        loadState();

        while (true) {
            String caminhoAtual = obterCaminhoCompleto(diretorioAtual);
            System.out.print("\n" + caminhoAtual + " > ");
            String comando = scanner.nextLine().trim();
            journal.log_no_sout(comando);
            if (comando.equals("exit")) {
                saveState();
                journal.log("Encerrando o programa.");
                break;
            } else if (comando.startsWith("cd ")) {
                String caminho = comando.substring(3).trim();
                mudarDiretorio(caminho);
            } else if (comando.startsWith("mkdir ")) {
                String nomeDiretorio = comando.substring(6).trim();
                criarDiretorio(nomeDiretorio);
            } else if (comando.startsWith("touch ")) {
                String nomeArquivo = comando.substring(6).trim();
                criarArquivo(nomeArquivo);
            } else if (comando.startsWith("rm ")) {
                String nome = comando.substring(3).trim();
                if (diretorioAtual.arquivos.contains(nome)) {
                    diretorioAtual.arquivos.remove(nome);
                    journal.log("Arquivo '" + nome + "' excluído.");
                } else if (diretorioAtual.subdiretorios.containsKey(nome)) {
                    diretorioAtual.subdiretorios.remove(nome);
                    journal.log("Diretório '" + nome + "' excluído.");
                } else {
                    journal.log("Erro: Arquivo ou diretório não encontrado.");
                }
            } else if (comando.startsWith("copy ")) {
                String nome = comando.substring(5).trim();
                copiarItem(nome);
            } else if (comando.equals("paste")) {
                colarItem();
            } else if (comando.equals("ls")) {
                for (String arquivo : diretorioAtual.arquivos) {
                    journal.log("Arquivo: " + arquivo);
                }
                for (String subdir : diretorioAtual.subdiretorios.keySet()) {
                    journal.log("Diretório: " + subdir);
                }
            } else if (comando.startsWith("rename ")) {
                String[] partes = comando.split(" ");
                if (partes.length == 3) {
                    renomearArquivo(partes[1], partes[2]);
                } else {
                    journal.log("Uso incorreto. Formato esperado: rename [nome_antigo] [nome_novo]");
                }
            } else if (comando.startsWith("renamedir ")) {
                String[] partes = comando.split(" ");
                if (partes.length == 3) {
                    renomearDiretorio(partes[1], partes[2]);
                } else {
                    journal.log("Uso incorreto. Formato esperado: renamedir [nome_antigo] [nome_novo]");
                }
            } else if (comando.startsWith("open_journal")) {
                List<String> logs = journal.getLogs();
                System.out.println("\n\n-------------- Journal <start> --------------");
                for (int i = 0; i < logs.size(); i++){
                    System.out.println(logs.get(i));
                }
                System.out.println("-------------- Journal <end>  --------------\n\n");
            } else if (comando.startsWith("tree")) {
                exibirArvore(raiz, "");
            } else if (comando.startsWith("help")) {
                exibirMenu();
            } else {
                journal.log("Comando inválido. Tente novamente.");
            }
        }
        scanner.close();
    }
}
