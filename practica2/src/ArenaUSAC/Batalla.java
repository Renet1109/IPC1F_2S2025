import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Batalla {
    private int id;
    private String participante1;
    private String participante2;
    private String ganador;
    private String fecha;
    private String bitacora;

    public Batalla(int id, String p1, String p2) {
        this.id = id;
        this.participante1 = p1;
        this.participante2 = p2;
        this.fecha = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        this.bitacora = "";
    }

    public void agregarEvento(String evento) {
        bitacora += evento + "\n";
    }

    public void setGanador(String ganador) {
        this.ganador = ganador;
    }

    public String getInfo() {
        return "Batalla " + id + " - Fecha: " + fecha + " - Participantes: " + participante1 + " vs " + participante2 + " - Ganador: " + ganador;
    }

    public String getBitacora() { return bitacora; }
}