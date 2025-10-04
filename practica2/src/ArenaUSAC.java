import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.Scanner; // Añadido para Scanner

public class ArenaUSAC {
    private static Personaje[] personajes = new Personaje[50];
    private static int numPersonajes = 0;
    private static Batalla[] historial = new Batalla[50];
    private static int numBatallas = 0;
    private static int nextId = 1;

    private static final String ESTUDIANTE_INFO = "Nombre: TuNombre\nCarnet: TuCarnet\nSección: TuSección";

    public static void main(String[] args) {
        // Auto-carga al inicio si existe el archivo
        cargarEstado();
        SwingUtilities.invokeLater(ArenaUSAC::mostrarMenuPrincipal);
    }

    private static void mostrarMenuPrincipal() {
        JFrame frame = new JFrame("ArenaUSAC");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridLayout(10, 1));

        JButton btnAgregar = new JButton("Agregar Personaje");
        btnAgregar.addActionListener(e -> agregarPersonaje());
        frame.add(btnAgregar);

        JButton btnModificar = new JButton("Modificar Personaje");
        btnModificar.addActionListener(e -> modificarPersonaje());
        frame.add(btnModificar);

        JButton btnEliminar = new JButton("Eliminar Personaje");
        btnEliminar.addActionListener(e -> eliminarPersonaje());
        frame.add(btnEliminar);

        JButton btnVisualizar = new JButton("Visualizar Personajes");
        btnVisualizar.addActionListener(e -> visualizarPersonajes());
        frame.add(btnVisualizar);

        JButton btnSimular = new JButton("Simular Batalla");
        btnSimular.addActionListener(e -> simularBatalla());
        frame.add(btnSimular);

        JButton btnHistorial = new JButton("Ver Historial de Batallas");
        btnHistorial.addActionListener(e -> verHistorial());
        frame.add(btnHistorial);

        JButton btnBuscar = new JButton("Buscar Personaje por Nombre");
        btnBuscar.addActionListener(e -> buscarPersonaje());
        frame.add(btnBuscar);

        JButton btnGuardar = new JButton("Guardar Estado");
        btnGuardar.addActionListener(e -> guardarEstado());
        frame.add(btnGuardar);

        JButton btnCargar = new JButton("Cargar Estado");
        btnCargar.addActionListener(e -> cargarEstado());
        frame.add(btnCargar);

        JButton btnEstudiante = new JButton("Ver Datos de Estudiante");
        btnEstudiante.addActionListener(e -> mostrarDatosEstudiante());
        frame.add(btnEstudiante);

        frame.setVisible(true);
    }

    // ... (métodos agregarPersonaje, modificarPersonaje, eliminarPersonaje, visualizarPersonajes, simularBatalla, verHistorial, buscarPersonaje, mostrarDatosEstudiante, getIntInput, buscarPorNombre, buscarPorIdONombre permanecen IGUALES al código anterior)

    // Guardar Estado (actualizado con debug y manejo de comas)
    private static void guardarEstado() {
        try (PrintWriter writer = new PrintWriter(new FileWriter("estado.txt"))) { // Especifica ruta absoluta si es necesario
            System.out.println("DEBUG: Iniciando guardado. numPersonajes = " + numPersonajes + ", numBatallas = " + numBatallas);
            writer.println(numPersonajes);
            for (int i = 0; i < numPersonajes; i++) {
                if (personajes[i] != null) {
                    Personaje p = personajes[i];
                    // Escapa comas en nombres/armas con comillas
                    String nombreEscapado = "\"" + p.getNombre().replace("\"", "\\\"") + "\"";
                    String armaEscapada = "\"" + p.getArma().replace("\"", "\\\"") + "\"";
                    writer.println(p.getId() + "," + nombreEscapado + "," + armaEscapada + "," + p.getHp() + "," + p.getAtaque() + "," +
                                   p.getVelocidad() + "," + p.getAgilidad() + "," + p.getDefensa() + "," +
                                   p.getBatallasGanadas() + "," + p.getBatallasPerdidas());
                    System.out.println("DEBUG: Guardado personaje " + i + ": " + p.getNombre());
                }
            }
            writer.println(numBatallas);
            for (int i = 0; i < numBatallas; i++) {
                if (historial[i] != null) {
                    Batalla b = historial[i];
                    writer.println(b.getId() + "," + b.getParticipante1() + "," + b.getParticipante2() + "," + b.getGanador() + "," + b.getFecha());
                    writer.println(b.getBitacora().replace("\n", "\\n")); // Escapa saltos de línea
                    writer.println("---");
                    System.out.println("DEBUG: Guardada batalla " + i);
                }
            }
            writer.flush(); // Fuerza escritura
            System.out.println("DEBUG: Guardado completado. Archivo: estado.txt");
            JOptionPane.showMessageDialog(null, "Estado guardado exitosamente en estado.txt");
        } catch (IOException e) {
            System.out.println("ERROR en guardarEstado: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al guardar: " + e.getMessage() + ". Verifica permisos de escritura.");
        }
    }

    // Cargar Estado (actualizado con debug y manejo de comas/escapes)
    private static void cargarEstado() {
        File file = new File("estado.txt");
        if (!file.exists()) {
            System.out.println("DEBUG: Archivo estado.txt no existe. Iniciando vacío.");
            JOptionPane.showMessageDialog(null, "No hay archivo para cargar. Inicia agregando personajes.");
            return;
        }

        try (Scanner scanner = new Scanner(file)) {
            System.out.println("DEBUG: Iniciando carga desde estado.txt");
            numPersonajes = scanner.nextInt();
            scanner.nextLine(); // Limpia línea
            System.out.println("DEBUG: Cargando " + numPersonajes + " personajes");
            for (int i = 0; i < numPersonajes; i++) {
                String line = scanner.nextLine();
                String[] parts = line.split(",", -1); // Mantiene comas vacías
                if (parts.length < 10) {
                    System.out.println("ERROR: Línea inválida en personaje " + i + ": " + line);
                    continue;
                }
                // Maneja comillas escapadas en nombres/armas
                String nombre = parts[1].replaceAll("^\"|\"$", "").replace("\\\"", "\"");
                String arma = parts[2].replaceAll("^\"|\"$", "").replace("\\\"", "\"");
                int id = Integer.parseInt(parts[0]);
                int hp = Integer.parseInt(parts[3]);
                int ataque = Integer.parseInt(parts[4]);
                int velocidad = Integer.parseInt(parts[5]);
                int agilidad = Integer.parseInt(parts[6]);
                int defensa = Integer.parseInt(parts[7]);
                int ganadas = Integer.parseInt(parts[8]);
                int perdidas = Integer.parseInt(parts[9]);

                personajes[i] = new Personaje(id, nombre, arma, hp, ataque, velocidad, agilidad, defensa);
                for (int j = 0; j < ganadas; j++) personajes[i].ganarBatalla();
                for (int j = 0; j < perdidas; j++) personajes[i].perderBatalla();
                nextId = Math.max(nextId, id + 1);
                System.out.println("DEBUG: Cargado personaje " + i + ": " + nombre);
            }
            numBatallas = scanner.nextInt();
            scanner.nextLine();
            System.out.println("DEBUG: Cargando " + numBatallas + " batallas");
            for (int i = 0; i < numBatallas; i++) {
                String line = scanner.nextLine();
                String[] parts = line.split(",", -1);
                if (parts.length < 5) continue;
                int idBatalla = Integer.parseInt(parts[0]);
                String p1 = parts[1];
                String p2 = parts[2];
                String ganador = parts[3];
                String fecha = parts[4];

                Batalla b = new Batalla(idBatalla, p1, p2);
                b.setGanador(ganador);
                b.fecha = fecha; // Restaura fecha (agrega setter si es necesario en Batalla)

                StringBuilder bitacora = new StringBuilder();
                String bitLine;
                while (scanner.hasNextLine() && !(bitLine = scanner.nextLine()).equals("---")) {
                    bitacora.append(bitLine.replace("\\n", "\n")).append("\n");
                }
                b.agregarEvento(bitacora.toString().trim());
                historial[i] = b;
                System.out.println("DEBUG: Cargada batalla " + i);
            }
            System.out.println("DEBUG: Carga completada. Total personajes: " + numPersonajes);
            JOptionPane.showMessageDialog(null, "Estado cargado exitosamente. Personajes: " + numPersonajes);
        } catch (IOException e) {
            System.out.println("ERROR en cargarEstado: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al cargar: " + e.getMessage() + ". Verifica el archivo estado.txt.");
        } catch (NumberFormatException e) {
            System.out.println("ERROR en formato: " + e.getMessage());
            JOptionPane.showMessageDialog(null, "Error en formato del archivo: " + e.getMessage());
        }
    }

    // ... (resto de métodos iguales al código anterior)
}
