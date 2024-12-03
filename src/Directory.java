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

    // Criar subdiretório dentro de outro diretório
    public boolean criarSubdiretorio(String nomePai, String nomeNovo) {
        Directory diretorioPai = buscarSubdiretorio(nomePai);
        if (diretorioPai != null) {
            diretorioPai.adicionarDiretorio(new Directory(nomeNovo));
            return true;
        }
        return false;
    }

    // Método para listar conteúdo do diretório e seus subdiretórios
    public void listarConteudo() {
        System.out.println("Conteúdo do diretório: " + nome);
        arquivos.forEach(arquivo -> System.out.println("Arquivo: " + arquivo.getNome()));
        subdiretorios.forEach(diretorio -> {
            System.out.println("Diretório: " + diretorio.getNome());
            diretorio.listarConteudo(); // Chama recursivamente para listar conteúdo
        });
    }

    // Busca recursiva para encontrar um subdiretório pelo nome
    public Directory buscarSubdiretorio(String nome) {
        if (this.nome.equals(nome)) {
            return this;
        }
        for (Directory subdiretorio : subdiretorios) {
            Directory resultado = subdiretorio.buscarSubdiretorio(nome);
            if (resultado != null) {
                return resultado;
            }
        }
        return null;
    }

    // Copiar arquivo para um subdiretório específico
    public boolean copiarArquivo(String nomeArquivo, String nomeCopia, String nomeDiretorioDestino) {
        File arquivoOriginal = arquivos.stream()
                .filter(f -> f.getNome().equals(nomeArquivo))
                .findFirst()
                .orElse(null);

        Directory destino = buscarSubdiretorio(nomeDiretorioDestino);

        if (arquivoOriginal != null && destino != null) {
            File arquivoCopia = new File(nomeCopia);
            destino.adicionarArquivo(arquivoCopia);
            return true;
        }
        return false;
    }

    // Mover arquivo para um subdiretório específico
    public boolean moverArquivo(String nomeArquivo, String nomeDiretorioDestino) {
        File arquivo = arquivos.stream()
                .filter(f -> f.getNome().equals(nomeArquivo))
                .findFirst()
                .orElse(null);

        Directory destino = buscarSubdiretorio(nomeDiretorioDestino);

        if (arquivo != null && destino != null) {
            this.removerArquivo(nomeArquivo);
            destino.adicionarArquivo(arquivo);
            return true;
        }
        return false;
    }
}
