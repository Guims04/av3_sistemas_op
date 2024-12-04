import java.util.*;

public class Diretorio {
    String nome;
    Map<String, Diretorio> subdiretorios = new HashMap<>();
    List<String> arquivos = new ArrayList<>();

    Diretorio(String nome) {
        this.nome = nome;
    }
}