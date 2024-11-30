import java.util.ArrayList;
import java.util.List;

public class Directory {
    private String nome;
    private List<File> arquivos;
    private List<Directory> subdiretorios;

    public Directory(String nome) {
        this.nome = nome;
        this.arquivos = new ArrayList<>();
        this.subdiretorios = new ArrayList<>();
    }

    public String getNome() {
        return nome;
    }

    public void adicionarArquivo(File arquivo) {
        arquivos.add(arquivo);
    }

    public void adicionarDiretorio(Directory diretorio) {
        subdiretorios.add(diretorio);
    }

    public boolean contemArquivo(String nome) {
        return arquivos.stream().anyMatch(arquivo -> arquivo.getNome().equals(nome));
    }

    public boolean contemDiretorio(String nome) {
        return subdiretorios.stream().anyMatch(diretorio -> diretorio.getNome().equals(nome));
    }

    public boolean removerArquivo(String nome) {
        return arquivos.removeIf(arquivo -> arquivo.getNome().equals(nome));
    }

    public boolean removerDiretorio(String nome) {
        return subdiretorios.removeIf(diretorio -> diretorio.getNome().equals(nome));
    }

    public void listarConteudo() {
        arquivos.forEach(arquivo -> System.out.println("Arquivo: " + arquivo.getNome()));
        subdiretorios.forEach(diretorio -> System.out.println("Diretório: " + diretorio.getNome()));
    }
}
