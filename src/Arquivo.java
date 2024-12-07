public class Arquivo {
    private String nome;

    public Arquivo(String nome) {
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String novoNome) {
        this.nome = novoNome;
    }

    @Override
    public String toString() {
        return nome;
    }

    @Override
    public boolean equals(Object obj) {
        if (this.nome == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Arquivo arquivo = (Arquivo) obj;
        return nome.equals(arquivo.nome);
    }

    @Override
    public int hashCode() {
        return nome.hashCode();
    }
}

