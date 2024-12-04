import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Journal implements Serializable { // Implementando Serializable
    private static final long serialVersionUID = 1L;  // Adicionando o serialVersionUID (opcional, mas recomendado)

    private List<String> logs;

    public Journal() {
        logs = new ArrayList<>();
    }

    public void log(String message) {
        logs.add(message);
        // Salvar logs em arquivo ou console
        System.out.println(message);
    }

    public void log_no_sout(String message) {
        logs.add(message);
    }

    public List<String> getLogs() {
        return logs;
    }
}
