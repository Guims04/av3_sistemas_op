import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Journal {
    private static final String JOURNAL_FILE = "journal.log";

    public void registrarOperacao(String operacao) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(JOURNAL_FILE, true))) {
            writer.write(operacao + "\n");
        } catch (IOException e) {
            System.err.println("Erro ao registrar operação no journal: " + e.getMessage());
        }
    }
}
