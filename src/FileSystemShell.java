import java.io.*;
import java.nio.file.*;
import java.util.Scanner;

public class FileSystemShell {

    private static final String JOURNAL_FILE = "journal.log";

    public static void main(String[] args) {
        FileSystemShell shell = new FileSystemShell();
        shell.runShell();
    }

    private void runShell() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Bem-vindo ao Simulador de Sistema de Arquivos");
        System.out.println("Digite 'help' para listar os comandos disponíveis.");

        while (true) {
            System.out.print("fs-shell> ");
            String input = scanner.nextLine().trim();
            if (input.equalsIgnoreCase("exit")) {
                System.out.println("Saindo do sistema...");
                break;
            }
            processCommand(input);
        }
        scanner.close();
    }

    private void processCommand(String command) {
        String[] parts = command.split(" ", 3);

        if (parts.length < 2 && !parts[0].equalsIgnoreCase("list")) {
            System.out.println("Comando inválido. Use 'help' para ajuda.");
            return;
        }

        try {
            switch (parts[0].toLowerCase()) {
                case "copy":
                    copy(parts[1], parts[2]);
                    break;
                case "delete":
                    delete(parts[1]);
                    break;
                case "rename":
                    rename(parts[1], parts[2]);
                    break;
                case "mkdir":
                    createDirectory(parts[1]);
                    break;
                case "rmdir":
                    deleteDirectory(parts[1]);
                    break;
                case "list":
                    listDirectory(parts.length > 1 ? parts[1] : ".");
                    break;
                case "help":
                    printHelp();
                    break;
                default:
                    System.out.println("Comando desconhecido. Use 'help' para ajuda.");
            }
        } catch (IOException e) {
            System.err.println("Erro: " + e.getMessage());
        }
    }

    private void copy(String source, String destination) throws IOException {
        logJournal("COPY " + source + " TO " + destination);
        Files.copy(Paths.get(source), Paths.get(destination), StandardCopyOption.REPLACE_EXISTING);
        System.out.println("Arquivo copiado de " + source + " para " + destination);
    }

    private void delete(String path) throws IOException {
        logJournal("DELETE " + path);
        Files.deleteIfExists(Paths.get(path));
        System.out.println("Arquivo ou diretório excluído: " + path);
    }

    private void rename(String source, String destination) throws IOException {
        logJournal("RENAME " + source + " TO " + destination);
        Files.move(Paths.get(source), Paths.get(destination), StandardCopyOption.REPLACE_EXISTING);
        System.out.println("Renomeado: " + source + " -> " + destination);
    }

    private void createDirectory(String dirPath) throws IOException {
        logJournal("CREATE DIRECTORY " + dirPath);
        Files.createDirectories(Paths.get(dirPath));
        System.out.println("Diretório criado: " + dirPath);
    }

    private void deleteDirectory(String dirPath) throws IOException {
        logJournal("DELETE DIRECTORY " + dirPath);
        Path path = Paths.get(dirPath);
        if (!Files.isDirectory(path)) {
            System.out.println("O caminho especificado não é um diretório: " + dirPath);
            return;
        }
        Files.walk(path)
                .sorted((a, b) -> b.compareTo(a)) // Ordena para deletar primeiro os filhos
                .forEach(p -> {
                    try {
                        Files.delete(p);
                    } catch (IOException e) {
                        System.err.println("Erro ao deletar: " + p);
                    }
                });
        System.out.println("Diretório excluído: " + dirPath);
    }

    private void listDirectory(String dirPath) {
        logJournal("LIST DIRECTORY " + dirPath);
        File directory = new File(dirPath);
        if (!directory.isDirectory()) {
            System.out.println("O caminho especificado não é um diretório: " + dirPath);
            return;
        }
        String[] contents = directory.list();
        System.out.println("Conteúdo do diretório " + dirPath + ":");
        if (contents != null) {
            for (String item : contents) {
                System.out.println(" - " + item);
            }
        } else {
            System.out.println("(Diretório vazio ou inacessível)");
        }
    }

    private void logJournal(String operation) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(JOURNAL_FILE, true))) {
            writer.write(operation);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Erro ao gravar no journal: " + e.getMessage());
        }
    }

    private void printHelp() {
        System.out.println("Comandos disponíveis:");
        System.out.println("copy <origem> <destino>        - Copia um arquivo para o destino.");
        System.out.println("delete <caminho>              - Apaga um arquivo ou diretório.");
        System.out.println("rename <origem> <destino>     - Renomeia um arquivo ou diretório.");
        System.out.println("mkdir <caminho>               - Cria um diretório.");
        System.out.println("rmdir <caminho>               - Apaga um diretório e seu conteúdo.");
        System.out.println("list [caminho]                - Lista os arquivos de um diretório.");
        System.out.println("help                          - Mostra esta ajuda.");
        System.out.println("exit                          - Sai do sistema.");
    }
}
