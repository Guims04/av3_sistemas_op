import java.util.*;
import java.io.Serializable;

public class Diretorio implements Serializable {
    String nome;
    Map<String, Diretorio> subdiretorios = new HashMap<>();
    List<String> arquivos = new ArrayList<>();

    Diretorio(String nome) {
        this.nome = nome;
    }
}