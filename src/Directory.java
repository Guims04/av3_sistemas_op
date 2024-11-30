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

    public void setNome(String novoNome) {
        this.nome = novoNome;
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

    // Método para listar conteúdo do diretório
    public void listarConteudo() {
        arquivos.forEach(arquivo -> System.out.println("Arquivo: " + arquivo.getNome()));
        subdiretorios.forEach(diretorio -> System.out.println("Diretório: " + diretorio.getNome()));
    }

    // Método para buscar subdiretório pelo nome, de forma recursiva
    public Directory buscarSubdiretorio(String nome) {
        if (this.nome.equals(nome)) {
            return this;  // Caso o diretório procurado seja o próprio
        }

        // Busca recursiva nos subdiretórios
        for (Directory subdiretorio : subdiretorios) {
            Directory resultado = subdiretorio.buscarSubdiretorio(nome);
            if (resultado != null) {
                return resultado;
            }
        }

        return null; // Retorna null se o subdiretório não for encontrado
    }

    // Método para mover um arquivo para outro diretório
    public boolean moverArquivo(Directory destino, String nomeArquivo) {
        // Tenta remover o arquivo do diretório atual
        File arquivo = arquivos.stream()
                .filter(f -> f.getNome().equals(nomeArquivo))
                .findFirst()
                .orElse(null);

        if (arquivo != null) {
            // Remover o arquivo do diretório atual
            this.removerArquivo(nomeArquivo);
            // Adicionar o arquivo ao diretório de destino
            destino.adicionarArquivo(arquivo);
            return true;
        }
        return false;
    }

    // Método para renomear um arquivo dentro do diretório
    public boolean renomearArquivo(String nomeAntigo, String novoNome) {
        File arquivo = arquivos.stream()
                .filter(f -> f.getNome().equals(nomeAntigo))
                .findFirst()
                .orElse(null);

        if (arquivo != null) {
            arquivo.setNome(novoNome);
            return true;
        }
        return false;  // Retorna falso se o arquivo não for encontrado
    }

    // Método para copiar um arquivo dentro do diretório
    public boolean copiarArquivo(String nome, String nomeCopia) {
        File arquivoOriginal = arquivos.stream()
                .filter(f -> f.getNome().equals(nome))
                .findFirst()
                .orElse(null);

        if (arquivoOriginal != null) {
            // Criando o arquivo copiado
            File arquivoCopia = new File(nomeCopia);
            arquivos.add(arquivoCopia);
            return true;
        }
        return false;  // Retorna falso se o arquivo original não for encontrado
    }

    // Método para renomear um diretório
    public boolean renomearDiretorio(String nomeAntigo, String novoNome) {
        Directory diretorioParaRenomear = subdiretorios.stream()
                .filter(d -> d.getNome().equals(nomeAntigo))
                .findFirst()
                .orElse(null);

        if (diretorioParaRenomear != null) {
            diretorioParaRenomear.setNome(novoNome);  // Renomeia o diretório
            return true;
        }
        return false;  // Retorna falso se o diretório não for encontrado
    }


}
