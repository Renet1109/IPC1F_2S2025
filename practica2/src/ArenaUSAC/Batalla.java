import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// Clase para registrar batallas con bitácora
public class Batalla {
    private int id; // ID de la batalla
    private String participante1; // Nombre del primer participante
    private String participante2; // Nombre del segundo participante
    private String ganador; // Nombre del ganador
    private String fecha; // Fecha de la batalla
    private String bitacora; // Log de eventos

    // Constructor: Inicializa con ID y participantes, fecha automática
    public Batalla(int id, String p1, String p2) {
        this.id = id;
        this.participante1 = p1;
        this.participante2 = p2;
        this.fecha = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        this.bitacora = "";
    }

    // Añadir evento a la bitácora
    public void agregarEvento(String evento) {
        bitacora += evento + "\n";
    }

    // Establecer ganador
    public void setGanador(String ganador) {
        this.ganador = ganador;
    }

    // Obtener resumen de la batalla
    public String getInfo() {
        return "Batalla " + id + " - Fecha: " + fecha + " - Participantes: " + participante1 + " vs " + participante2 + " - Ganador: " + ganador;
    }

    // Obtener bitácora completa
    public String getBitacora() { return bitacora; }

    String getId() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}