import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.Scanner;

public class Main {
    private static Personaje[] personajes = new Personaje[50];
    private static int numPersonajes = 0;
    private static Batalla[] historial = new Batalla[50];
    private static int numBatallas = 0;
    private static int nextId = 1;

    private static final String ESTUDIANTE_INFO = "Nombre: TuNombre\nCarnet: TuCarnet\nSección: TuSección";

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::mostrarMenuPrincipal);
    }

    private static void mostrarMenuPrincipal() {
        JFrame frame = new JFrame("ArenaUSAC");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridLayout(10, 1));

        JButton[] botones = {
            new JButton("Agregar Personaje"), new JButton("Modificar Personaje"),
            new JButton("Eliminar Personaje"), new JButton("Visualizar Personajes"),
            new JButton("Simular Batalla"), new JButton("Ver Historial de Batallas"),
            new JButton("Buscar Personaje por Nombre"), new JButton("Guardar Estado"),
            new JButton("Cargar Estado"), new JButton("Ver Datos de Estudiante")
        };

        String[] acciones = {
            "agregarPersonaje", "modificarPersonaje", "eliminarPersonaje", "visualizarPersonajes",
            "simularBatalla", "verHistorial", "buscarPersonaje", "guardarEstado",
            "cargarEstado", "mostrarDatosEstudiante"
        };

        for (int i = 0; i < botones.length; i++) {
            int index = i;
            botones[i].addActionListener(e -> {
                try {
                    Main.class.getMethod(acciones[index]).invoke(null);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
                }
            });
            frame.add(botones[i]);
        }

        frame.setVisible(true);
    }

    private static void agregarPersonaje() {
        if (numPersonajes >= personajes.length) {
            JOptionPane.showMessageDialog(null, "Límite de personajes alcanzado (50).");
            return;
        }

        String nombre = JOptionPane.showInputDialog("Nombre:");
        if (nombre == null || nombre.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Nombre inválido.");
            return;
        }

        if (numPersonajes > 0 && buscarPorNombre(nombre) != -1) {
            JOptionPane.showMessageDialog(null, "Nombre duplicado.");
            return;
        }

        String arma = JOptionPane.showInputDialog("Arma:");
        if (arma == null || arma.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Arma inválida.");
            return;
        }

        int hp = getIntInput("HP (100-500):", 100, 500);
        int ataque = getIntInput("Ataque (10-100):", 10, 100);
        int velocidad = getIntInput("Velocidad (1-10):", 1, 10);
        int agilidad = getIntInput("Agilidad (1-10):", 1, 10);
        int defensa = getIntInput("Defensa (1-50):", 1, 50);

        personajes[numPersonajes] = new Personaje(nextId++, nombre, arma, hp, ataque, velocidad, agilidad, defensa);
        numPersonajes++;
        JOptionPane.showMessageDialog(null, "Personaje agregado con ID: " + (nextId - 1));
    }

    private static void modificarPersonaje() {
        String input = JOptionPane.showInputDialog("ID o Nombre:");
        if (input == null || input.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Entrada inválida.");
            return;
        }
        int index = buscarPorIdONombre(input);
        if (index == -1) {
            JOptionPane.showMessageDialog(null, "No encontrado.");
            return;
        }

        JOptionPane.showMessageDialog(null, "Datos actuales: " + personajes[index].toString());
        String nuevaArma = JOptionPane.showInputDialog("Nueva Arma:", personajes[index].getArma());
        if (nuevaArma != null && !nuevaArma.trim().isEmpty()) {
            personajes[index].setArma(nuevaArma);
        }
        int nuevoHp = getIntInput("Nuevo HP (100-500):", 100, 500);
        personajes[index].setHp(nuevoHp);
        JOptionPane.showMessageDialog(null, "Personaje modificado.");
    }

    private static void eliminarPersonaje() {
        String input = JOptionPane.showInputDialog("ID o Nombre:");
        if (input == null || input.trim().isEmpty()) {
            return;
        }
        int index = buscarPorIdONombre(input);
        if (index == -1) {
            JOptionPane.showMessageDialog(null, "No encontrado.");
            return;
        }

        if (JOptionPane.showConfirmDialog(null, "¿Eliminar " + personajes[index].getNombre() + "?", "Confirmar", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            for (int i = index; i < numPersonajes - 1; i++) {
                personajes[i] = personajes[i + 1];
            }
            personajes[numPersonajes - 1] = null;
            numPersonajes--;
            JOptionPane.showMessageDialog(null, "Personaje eliminado.");
        }
    }

    private static void visualizarPersonajes() {
        if (numPersonajes == 0) {
            JOptionPane.showMessageDialog(null, "No hay personajes registrados.");
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < numPersonajes; i++) {
            if (personajes[i] != null) {
                sb.append(personajes[i].toString()).append("\n");
            }
        }
        JTextArea area = new JTextArea(sb.toString());
        area.setEditable(false);
        JOptionPane.showMessageDialog(null, new JScrollPane(area));
    }

    private static void simularBatalla() {
        if (numPersonajes < 2) {
            JOptionPane.showMessageDialog(null, "Necesitas al menos 2 personajes.");
            return;
        }

        String p1Input = JOptionPane.showInputDialog("ID/Nombre Personaje 1:");
        if (p1Input == null || p1Input.trim().isEmpty()) return;
        int index1 = buscarPorIdONombre(p1Input);
        if (index1 == -1 || personajes[index1].getHp() <= 0) {
            JOptionPane.showMessageDialog(null, "Personaje 1 inválido o sin HP.");
            return;
        }

        String p2Input = JOptionPane.showInputDialog("ID/Nombre Personaje 2:");
        if (p2Input == null || p2Input.trim().isEmpty()) return;
        int index2 = buscarPorIdONombre(p2Input);
        if (index2 == -1 || index2 == index1 || personajes[index2].getHp() <= 0) {
            JOptionPane.showMessageDialog(null, "Personaje 2 inválido o igual al 1.");
            return;
        }

        Personaje p1 = new Personaje(personajes[index1].getId(), personajes[index1].getNombre(), personajes[index1].getArma(), personajes[index1].getHp(),
                                    personajes[index1].getAtaque(), personajes[index1].getVelocidad(), personajes[index1].getAgilidad(), personajes[index1].getDefensa());
        Personaje p2 = new Personaje(personajes[index2].getId(), personajes[index2].getNombre(), personajes[index2].getArma(), personajes[index2].getHp(),
                                    personajes[index2].getAtaque(), personajes[index2].getVelocidad(), personajes[index2].getAgilidad(), personajes[index2].getDefensa());

        Batalla batalla = new Batalla(numBatallas + 1, p1.getNombre(), p2.getNombre());
        historial[numBatallas++] = batalla;

        JFrame frame = new JFrame("Batalla: " + p1.getNombre() + " vs " + p2.getNombre());
        frame.setSize(400, 300);
        JTextArea logArea = new JTextArea();
        logArea.setEditable(false);
        frame.add(new JScrollPane(logArea));
        frame.setVisible(true);

        Thread t1 = new Thread(() -> {
            while (p1.getHp() > 0 && p2.getHp() > 0) {
                try { Thread.sleep(1000 / p1.getVelocidad()); } catch (InterruptedException e) {}
                if (!p2.esquivar()) {
                    int danio = p1.getAtaque();
                    p2.recibirDanio(danio);
                    String evento = p1.getNombre() + " ataca a " + p2.getNombre() + " - Daño: " + (danio - p2.getDefensa()) + " (HP: " + p2.getHp() + ")";
                    batalla.agregarEvento(evento);
                    logArea.append(evento + "\n");
                } else {
                    String evento = p1.getNombre() + " ataca a " + p2.getNombre() + " - Falló (esquiva)";
                    batalla.agregarEvento(evento);
                    logArea.append(evento + "\n");
                }
            }
        });

        Thread t2 = new Thread(() -> {
            while (p1.getHp() > 0 && p2.getHp() > 0) {
                try { Thread.sleep(1000 / p2.getVelocidad()); } catch (InterruptedException e) {}
                if (!p1.esquivar()) {
                    int danio = p2.getAtaque();
                    p1.recibirDanio(danio);
                    String evento = p2.getNombre() + " ataca a " + p1.getNombre() + " - Daño: " + (danio - p1.getDefensa()) + " (HP: " + p1.getHp() + ")";
                    batalla.agregarEvento(evento);
                    logArea.append(evento + "\n");
                } else {
                    String evento = p2.getNombre() + " ataca a " + p1.getNombre() + " - Falló (esquiva)";
                    batalla.agregarEvento(evento);
                    logArea.append(evento + "\n");
                }
            }
        });

        t1.start();
        t2.start();
        try { t1.join(); t2.join(); } catch (InterruptedException e) {}

        String ganador = p1.getHp() > 0 ? p1.getNombre() : p2.getNombre();
        batalla.setGanador(ganador);
        if (ganador.equals(p1.getNombre())) {
            personajes[index1].ganarBatalla();
            personajes[index2].perderBatalla();
        } else {
            personajes[index2].ganarBatalla();
            personajes[index1].perderBatalla();
        }
        logArea.append("Ganador: " + ganador + "\n");
    }

    private static void verHistorial() {
        if (numBatallas == 0) {
            JOptionPane.showMessageDialog(null, "No hay batallas registradas.");
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < numBatallas; i++) {
            if (historial[i] != null) {
                sb.append(historial[i].getInfo()).append("\n");
            }
        }
        JTextArea area = new JTextArea(sb.toString());
        area.setEditable(false);
        JOptionPane.showMessageDialog(null, new JScrollPane(area));
    }

    private static void buscarPersonaje() {
        String nombre = JOptionPane.showInputDialog("Nombre:");
        if (nombre == null || nombre.trim().isEmpty()) return;
        int index = buscarPorNombre(nombre);
        if (index == -1) {
            JOptionPane.showMessageDialog(null, "No encontrado.");
            return;
        }
        Personaje p = personajes[index];
        String info = p.toString() + "\nBatallas: " + (p.getBatallasGanadas() + p.getBatallasPerdidas()) +
                      "\nGanadas: " + p.getBatallasGanadas() + "\nPerdidas: " + p.getBatallasPerdidas();
        JOptionPane.showMessageDialog(null, info);
    }

    private static void guardarEstado() {
        try (PrintWriter writer = new PrintWriter("estado.txt")) {
            writer.println(numPersonajes);
            for (int i = 0; i < numPersonajes; i++) {
                if (personajes[i] != null) {
                    Personaje p = personajes[i];
                    writer.println(p.getId() + "," + p.getNombre() + "," + p.getArma() + "," + p.getHp() + "," + p.getAtaque() + "," +
                                   p.getVelocidad() + "," + p.getAgilidad() + "," + p.getDefensa() + "," +
                                   p.getBatallasGanadas() + "," + p.getBatallasPerdidas());
                }
            }
            writer.println(numBatallas);
            for (int i = 0; i < numBatallas; i++) {
                if (historial[i] != null) {
                    Batalla b = historial[i];
                    writer.println(b.getId() + "," + b.getParticipante1() + "," + b.getParticipante2() + "," + b.getGanador() + "," + b.getFecha());
                    writer.println(b.getBitacora());
                    writer.println("---");
                }
            }
            JOptionPane.showMessageDialog(null, "Guardado.");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error al guardar.");
        }
    }

    private static void cargarEstado() {
        try (Scanner scanner = new Scanner(new File("estado.txt"))) {
            numPersonajes = scanner.nextInt();
            scanner.nextLine();
            for (int i = 0; i < numPersonajes; i++) {
                String[] parts = scanner.nextLine().split(",");
                if (parts.length >= 10) {
                    int id = Integer.parseInt(parts[0]);
                    personajes[i] = new Personaje(id, parts[1], parts[2], Integer.parseInt(parts[3]),
                                                  Integer.parseInt(parts[4]), Integer.parseInt(parts[5]), Integer.parseInt(parts[6]),
                                                  Integer.parseInt(parts[7]));
                    for (int j = 0; j < Integer.parseInt(parts[8]); j++) personajes[i].ganarBatalla();
                    for (int j = 0; j < Integer.parseInt(parts[9]); j++) personajes[i].perderBatalla();
                    nextId = Math.max(nextId, id + 1);
                }
            }
            numBatallas = scanner.nextInt();
            scanner.nextLine();
            for (int i = 0; i < numBatallas; i++) {
                String[] parts = scanner.nextLine().split(",");
                if (parts.length >= 5) {
                    Batalla b = new Batalla(Integer.parseInt(parts[0]), parts[1], parts[2]);
                    b.setGanador(parts[3]);
                    StringBuilder bitacora = new StringBuilder();
                    String line;
                    while (scanner.hasNextLine() && !(line = scanner.nextLine()).equals("---")) {
                        bitacora.append(line).append("\n");
                    }
                    b.agregarEvento(bitacora.toString().trim());
                    historial[i] = b;
                }
            }
            JOptionPane.showMessageDialog(null, "Cargado.");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error al cargar (verifica que exista estado.txt).");
        }
    }

    private static void mostrarDatosEstudiante() {
        JOptionPane.showMessageDialog(null, ESTUDIANTE_INFO);
    }

    private static int getIntInput(String mensaje, int min, int max) {
        while (true) {
            try {
                String input = JOptionPane.showInputDialog(mensaje);
                if (input == null) return min;
                int valor = Integer.parseInt(input.trim());
                if (valor >= min && valor <= max) return valor;
                JOptionPane.showMessageDialog(null, "Valor fuera de rango (" + min + "-" + max + ").");
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Entrada inválida. Ingresa un número.");
            }
        }
    }

    private static int buscarPorNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) return -1;
        nombre = nombre.toLowerCase();
        for (int i = 0; i < numPersonajes; i++) {
            if (personajes[i] != null && personajes[i].getNombre().equals(nombre)) return i;
        }
        return -1;
    }

    private static int buscarPorIdONombre(String input) {
        if (input == null || input.trim().isEmpty()) return -1;
        try {
            int id = Integer.parseInt(input);
            for (int i = 0; i < numPersonajes; i++) {
                if (personajes[i] != null && personajes[i].getId() == id) return i;
            }
        } catch (NumberFormatException e) {
            return buscarPorNombre(input);
        }
        return -1;
    }
}import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.Scanner;

public class Main {
    private static Personaje[] personajes = new Personaje[50];
    private static int numPersonajes = 0;
    private static Batalla[] historial = new Batalla[50];
    private static int numBatallas = 0;
    private static int nextId = 1;

    private static final String ESTUDIANTE_INFO = "Nombre: TuNombre\nCarnet: TuCarnet\nSección: TuSección";

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::mostrarMenuPrincipal);
    }

    private static void mostrarMenuPrincipal() {
        JFrame frame = new JFrame("ArenaUSAC");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridLayout(10, 1));

        JButton[] botones = {
            new JButton("Agregar Personaje"), new JButton("Modificar Personaje"),
            new JButton("Eliminar Personaje"), new JButton("Visualizar Personajes"),
            new JButton("Simular Batalla"), new JButton("Ver Historial de Batallas"),
            new JButton("Buscar Personaje por Nombre"), new JButton("Guardar Estado"),
            new JButton("Cargar Estado"), new JButton("Ver Datos de Estudiante")
        };

        String[] acciones = {
            "agregarPersonaje", "modificarPersonaje", "eliminarPersonaje", "visualizarPersonajes",
            "simularBatalla", "verHistorial", "buscarPersonaje", "guardarEstado",
            "cargarEstado", "mostrarDatosEstudiante"
        };

        for (int i = 0; i < botones.length; i++) {
            int index = i;
            botones[i].addActionListener(e -> {
                try {
                    Main.class.getMethod(acciones[index]).invoke(null);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
                }
            });
            frame.add(botones[i]);
        }

        frame.setVisible(true);
    }

    private static void agregarPersonaje() {
        if (numPersonajes >= personajes.length) {
            JOptionPane.showMessageDialog(null, "Límite de personajes alcanzado (50).");
            return;
        }

        String nombre = JOptionPane.showInputDialog("Nombre:");
        if (nombre == null || nombre.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Nombre inválido.");
            return;
        }

        if (numPersonajes > 0 && buscarPorNombre(nombre) != -1) {
            JOptionPane.showMessageDialog(null, "Nombre duplicado.");
            return;
        }

        String arma = JOptionPane.showInputDialog("Arma:");
        if (arma == null || arma.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Arma inválida.");
            return;
        }

        int hp = getIntInput("HP (100-500):", 100, 500);
        int ataque = getIntInput("Ataque (10-100):", 10, 100);
        int velocidad = getIntInput("Velocidad (1-10):", 1, 10);
        int agilidad = getIntInput("Agilidad (1-10):", 1, 10);
        int defensa = getIntInput("Defensa (1-50):", 1, 50);

        personajes[numPersonajes] = new Personaje(nextId++, nombre, arma, hp, ataque, velocidad, agilidad, defensa);
        numPersonajes++;
        JOptionPane.showMessageDialog(null, "Personaje agregado con ID: " + (nextId - 1));
    }

    private static void modificarPersonaje() {
        String input = JOptionPane.showInputDialog("ID o Nombre:");
        if (input == null || input.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Entrada inválida.");
            return;
        }
        int index = buscarPorIdONombre(input);
        if (index == -1) {
            JOptionPane.showMessageDialog(null, "No encontrado.");
            return;
        }

        JOptionPane.showMessageDialog(null, "Datos actuales: " + personajes[index].toString());
        String nuevaArma = JOptionPane.showInputDialog("Nueva Arma:", personajes[index].getArma());
        if (nuevaArma != null && !nuevaArma.trim().isEmpty()) {
            personajes[index].setArma(nuevaArma);
        }
        int nuevoHp = getIntInput("Nuevo HP (100-500):", 100, 500);
        personajes[index].setHp(nuevoHp);
        JOptionPane.showMessageDialog(null, "Personaje modificado.");
    }

    private static void eliminarPersonaje() {
        String input = JOptionPane.showInputDialog("ID o Nombre:");
        if (input == null || input.trim().isEmpty()) {
            return;
        }
        int index = buscarPorIdONombre(input);
        if (index == -1) {
            JOptionPane.showMessageDialog(null, "No encontrado.");
            return;
        }

        if (JOptionPane.showConfirmDialog(null, "¿Eliminar " + personajes[index].getNombre() + "?", "Confirmar", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            for (int i = index; i < numPersonajes - 1; i++) {
                personajes[i] = personajes[i + 1];
            }
            personajes[numPersonajes - 1] = null;
            numPersonajes--;
            JOptionPane.showMessageDialog(null, "Personaje eliminado.");
        }
    }

    private static void visualizarPersonajes() {
        if (numPersonajes == 0) {
            JOptionPane.showMessageDialog(null, "No hay personajes registrados.");
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < numPersonajes; i++) {
            if (personajes[i] != null) {
                sb.append(personajes[i].toString()).append("\n");
            }
        }
        JTextArea area = new JTextArea(sb.toString());
        area.setEditable(false);
        JOptionPane.showMessageDialog(null, new JScrollPane(area));
    }

    private static void simularBatalla() {
        if (numPersonajes < 2) {
            JOptionPane.showMessageDialog(null, "Necesitas al menos 2 personajes.");
            return;
        }

        String p1Input = JOptionPane.showInputDialog("ID/Nombre Personaje 1:");
        if (p1Input == null || p1Input.trim().isEmpty()) return;
        int index1 = buscarPorIdONombre(p1Input);
        if (index1 == -1 || personajes[index1].getHp() <= 0) {
            JOptionPane.showMessageDialog(null, "Personaje 1 inválido o sin HP.");
            return;
        }

        String p2Input = JOptionPane.showInputDialog("ID/Nombre Personaje 2:");
        if (p2Input == null || p2Input.trim().isEmpty()) return;
        int index2 = buscarPorIdONombre(p2Input);
        if (index2 == -1 || index2 == index1 || personajes[index2].getHp() <= 0) {
            JOptionPane.showMessageDialog(null, "Personaje 2 inválido o igual al 1.");
            return;
        }

        Personaje p1 = new Personaje(personajes[index1].getId(), personajes[index1].getNombre(), personajes[index1].getArma(), personajes[index1].getHp(),
                                    personajes[index1].getAtaque(), personajes[index1].getVelocidad(), personajes[index1].getAgilidad(), personajes[index1].getDefensa());
        Personaje p2 = new Personaje(personajes[index2].getId(), personajes[index2].getNombre(), personajes[index2].getArma(), personajes[index2].getHp(),
                                    personajes[index2].getAtaque(), personajes[index2].getVelocidad(), personajes[index2].getAgilidad(), personajes[index2].getDefensa());

        Batalla batalla = new Batalla(numBatallas + 1, p1.getNombre(), p2.getNombre());
        historial[numBatallas++] = batalla;

        JFrame frame = new JFrame("Batalla: " + p1.getNombre() + " vs " + p2.getNombre());
        frame.setSize(400, 300);
        JTextArea logArea = new JTextArea();
        logArea.setEditable(false);
        frame.add(new JScrollPane(logArea));
        frame.setVisible(true);

        Thread t1 = new Thread(() -> {
            while (p1.getHp() > 0 && p2.getHp() > 0) {
                try { Thread.sleep(1000 / p1.getVelocidad()); } catch (InterruptedException e) {}
                if (!p2.esquivar()) {
                    int danio = p1.getAtaque();
                    p2.recibirDanio(danio);
                    String evento = p1.getNombre() + " ataca a " + p2.getNombre() + " - Daño: " + (danio - p2.getDefensa()) + " (HP: " + p2.getHp() + ")";
                    batalla.agregarEvento(evento);
                    logArea.append(evento + "\n");
                } else {
                    String evento = p1.getNombre() + " ataca a " + p2.getNombre() + " - Falló (esquiva)";
                    batalla.agregarEvento(evento);
                    logArea.append(evento + "\n");
                }
            }
        });

        Thread t2 = new Thread(() -> {
            while (p1.getHp() > 0 && p2.getHp() > 0) {
                try { Thread.sleep(1000 / p2.getVelocidad()); } catch (InterruptedException e) {}
                if (!p1.esquivar()) {
                    int danio = p2.getAtaque();
                    p1.recibirDanio(danio);
                    String evento = p2.getNombre() + " ataca a " + p1.getNombre() + " - Daño: " + (danio - p1.getDefensa()) + " (HP: " + p1.getHp() + ")";
                    batalla.agregarEvento(evento);
                    logArea.append(evento + "\n");
                } else {
                    String evento = p2.getNombre() + " ataca a " + p1.getNombre() + " - Falló (esquiva)";
                    batalla.agregarEvento(evento);
                    logArea.append(evento + "\n");
                }
            }
        });

        t1.start();
        t2.start();
        try { t1.join(); t2.join(); } catch (InterruptedException e) {}

        String ganador = p1.getHp() > 0 ? p1.getNombre() : p2.getNombre();
        batalla.setGanador(ganador);
        if (ganador.equals(p1.getNombre())) {
            personajes[index1].ganarBatalla();
            personajes[index2].perderBatalla();
        } else {
            personajes[index2].ganarBatalla();
            personajes[index1].perderBatalla();
        }
        logArea.append("Ganador: " + ganador + "\n");
    }

    private static void verHistorial() {
        if (numBatallas == 0) {
            JOptionPane.showMessageDialog(null, "No hay batallas registradas.");
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < numBatallas; i++) {
            if (historial[i] != null) {
                sb.append(historial[i].getInfo()).append("\n");
            }
        }
        JTextArea area = new JTextArea(sb.toString());
        area.setEditable(false);
        JOptionPane.showMessageDialog(null, new JScrollPane(area));
    }

    private static void buscarPersonaje() {
        String nombre = JOptionPane.showInputDialog("Nombre:");
        if (nombre == null || nombre.trim().isEmpty()) return;
        int index = buscarPorNombre(nombre);
        if (index == -1) {
            JOptionPane.showMessageDialog(null, "No encontrado.");
            return;
        }
        Personaje p = personajes[index];
        String info = p.toString() + "\nBatallas: " + (p.getBatallasGanadas() + p.getBatallasPerdidas()) +
                      "\nGanadas: " + p.getBatallasGanadas() + "\nPerdidas: " + p.getBatallasPerdidas();
        JOptionPane.showMessageDialog(null, info);
    }

    private static void guardarEstado() {
        try (PrintWriter writer = new PrintWriter("estado.txt")) {
            writer.println(numPersonajes);
            for (int i = 0; i < numPersonajes; i++) {
                if (personajes[i] != null) {
                    Personaje p = personajes[i];
                    writer.println(p.getId() + "," + p.getNombre() + "," + p.getArma() + "," + p.getHp() + "," + p.getAtaque() + "," +
                                   p.getVelocidad() + "," + p.getAgilidad() + "," + p.getDefensa() + "," +
                                   p.getBatallasGanadas() + "," + p.getBatallasPerdidas());
                }
            }
            writer.println(numBatallas);
            for (int i = 0; i < numBatallas; i++) {
                if (historial[i] != null) {
                    Batalla b = historial[i];
                    writer.println(b.getId() + "," + b.getParticipante1() + "," + b.getParticipante2() + "," + b.getGanador() + "," + b.getFecha());
                    writer.println(b.getBitacora());
                    writer.println("---");
                }
            }
            JOptionPane.showMessageDialog(null, "Guardado.");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error al guardar.");
        }
    }

    private static void cargarEstado() {
        try (Scanner scanner = new Scanner(new File("estado.txt"))) {
            numPersonajes = scanner.nextInt();
            scanner.nextLine();
            for (int i = 0; i < numPersonajes; i++) {
                String[] parts = scanner.nextLine().split(",");
                if (parts.length >= 10) {
                    int id = Integer.parseInt(parts[0]);
                    personajes[i] = new Personaje(id, parts[1], parts[2], Integer.parseInt(parts[3]),
                                                  Integer.parseInt(parts[4]), Integer.parseInt(parts[5]), Integer.parseInt(parts[6]),
                                                  Integer.parseInt(parts[7]));
                    for (int j = 0; j < Integer.parseInt(parts[8]); j++) personajes[i].ganarBatalla();
                    for (int j = 0; j < Integer.parseInt(parts[9]); j++) personajes[i].perderBatalla();
                    nextId = Math.max(nextId, id + 1);
                }
            }
            numBatallas = scanner.nextInt();
            scanner.nextLine();
            for (int i = 0; i < numBatallas; i++) {
                String[] parts = scanner.nextLine().split(",");
                if (parts.length >= 5) {
                    Batalla b = new Batalla(Integer.parseInt(parts[0]), parts[1], parts[2]);
                    b.setGanador(parts[3]);
                    StringBuilder bitacora = new StringBuilder();
                    String line;
                    while (scanner.hasNextLine() && !(line = scanner.nextLine()).equals("---")) {
                        bitacora.append(line).append("\n");
                    }
                    b.agregarEvento(bitacora.toString().trim());
                    historial[i] = b;
                }
            }
            JOptionPane.showMessageDialog(null, "Cargado.");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error al cargar (verifica que exista estado.txt).");
        }
    }

    private static void mostrarDatosEstudiante() {
        JOptionPane.showMessageDialog(null, ESTUDIANTE_INFO);
    }

    private static int getIntInput(String mensaje, int min, int max) {
        while (true) {
            try {
                String input = JOptionPane.showInputDialog(mensaje);
                if (input == null) return min;
                int valor = Integer.parseInt(input.trim());
                if (valor >= min && valor <= max) return valor;
                JOptionPane.showMessageDialog(null, "Valor fuera de rango (" + min + "-" + max + ").");
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Entrada inválida. Ingresa un número.");
            }
        }
    }

    private static int buscarPorNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) return -1;
        nombre = nombre.toLowerCase();
        for (int i = 0; i < numPersonajes; i++) {
            if (personajes[i] != null && personajes[i].getNombre().equals(nombre)) return i;
        }
        return -1;
    }

    private static int buscarPorIdONombre(String input) {
        if (input == null || input.trim().isEmpty()) return -1;
        try {
            int id = Integer.parseInt(input);
            for (int i = 0; i < numPersonajes; i++) {
                if (personajes[i] != null && personajes[i].getId() == id) return i;
            }
        } catch (NumberFormatException e) {
            return buscarPorNombre(input);
        }
        return -1;
    }
}import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.Scanner;

public class Main {
    private static Personaje[] personajes = new Personaje[50];
    private static int numPersonajes = 0;
    private static Batalla[] historial = new Batalla[50];
    private static int numBatallas = 0;
    private static int nextId = 1;

    private static final String ESTUDIANTE_INFO = "Nombre: TuNombre\nCarnet: TuCarnet\nSección: TuSección";

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::mostrarMenuPrincipal);
    }

    private static void mostrarMenuPrincipal() {
        JFrame frame = new JFrame("ArenaUSAC");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridLayout(10, 1));

        JButton[] botones = {
            new JButton("Agregar Personaje"), new JButton("Modificar Personaje"),
            new JButton("Eliminar Personaje"), new JButton("Visualizar Personajes"),
            new JButton("Simular Batalla"), new JButton("Ver Historial de Batallas"),
            new JButton("Buscar Personaje por Nombre"), new JButton("Guardar Estado"),
            new JButton("Cargar Estado"), new JButton("Ver Datos de Estudiante")
        };

        String[] acciones = {
            "agregarPersonaje", "modificarPersonaje", "eliminarPersonaje", "visualizarPersonajes",
            "simularBatalla", "verHistorial", "buscarPersonaje", "guardarEstado",
            "cargarEstado", "mostrarDatosEstudiante"
        };

        for (int i = 0; i < botones.length; i++) {
            int index = i;
            botones[i].addActionListener(e -> {
                try {
                    Main.class.getMethod(acciones[index]).invoke(null);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
                }
            });
            frame.add(botones[i]);
        }

        frame.setVisible(true);
    }

    private static void agregarPersonaje() {
        if (numPersonajes >= personajes.length) {
            JOptionPane.showMessageDialog(null, "Límite de personajes alcanzado (50).");
            return;
        }

        String nombre = JOptionPane.showInputDialog("Nombre:");
        if (nombre == null || nombre.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Nombre inválido.");
            return;
        }

        if (numPersonajes > 0 && buscarPorNombre(nombre) != -1) {
            JOptionPane.showMessageDialog(null, "Nombre duplicado.");
            return;
        }

        String arma = JOptionPane.showInputDialog("Arma:");
        if (arma == null || arma.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Arma inválida.");
            return;
        }

        int hp = getIntInput("HP (100-500):", 100, 500);
        int ataque = getIntInput("Ataque (10-100):", 10, 100);
        int velocidad = getIntInput("Velocidad (1-10):", 1, 10);
        int agilidad = getIntInput("Agilidad (1-10):", 1, 10);
        int defensa = getIntInput("Defensa (1-50):", 1, 50);

        personajes[numPersonajes] = new Personaje(nextId++, nombre, arma, hp, ataque, velocidad, agilidad, defensa);
        numPersonajes++;
        JOptionPane.showMessageDialog(null, "Personaje agregado con ID: " + (nextId - 1));
    }

    private static void modificarPersonaje() {
        String input = JOptionPane.showInputDialog("ID o Nombre:");
        if (input == null || input.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Entrada inválida.");
            return;
        }
        int index = buscarPorIdONombre(input);
        if (index == -1) {
            JOptionPane.showMessageDialog(null, "No encontrado.");
            return;
        }

        JOptionPane.showMessageDialog(null, "Datos actuales: " + personajes[index].toString());
        String nuevaArma = JOptionPane.showInputDialog("Nueva Arma:", personajes[index].getArma());
        if (nuevaArma != null && !nuevaArma.trim().isEmpty()) {
            personajes[index].setArma(nuevaArma);
        }
        int nuevoHp = getIntInput("Nuevo HP (100-500):", 100, 500);
        personajes[index].setHp(nuevoHp);
        JOptionPane.showMessageDialog(null, "Personaje modificado.");
    }

    private static void eliminarPersonaje() {
        String input = JOptionPane.showInputDialog("ID o Nombre:");
        if (input == null || input.trim().isEmpty()) {
            return;
        }
        int index = buscarPorIdONombre(input);
        if (index == -1) {
            JOptionPane.showMessageDialog(null, "No encontrado.");
            return;
        }

        if (JOptionPane.showConfirmDialog(null, "¿Eliminar " + personajes[index].getNombre() + "?", "Confirmar", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            for (int i = index; i < numPersonajes - 1; i++) {
                personajes[i] = personajes[i + 1];
            }
            personajes[numPersonajes - 1] = null;
            numPersonajes--;
            JOptionPane.showMessageDialog(null, "Personaje eliminado.");
        }
    }

    private static void visualizarPersonajes() {
        if (numPersonajes == 0) {
            JOptionPane.showMessageDialog(null, "No hay personajes registrados.");
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < numPersonajes; i++) {
            if (personajes[i] != null) {
                sb.append(personajes[i].toString()).append("\n");
            }
        }
        JTextArea area = new JTextArea(sb.toString());
        area.setEditable(false);
        JOptionPane.showMessageDialog(null, new JScrollPane(area));
    }

    private static void simularBatalla() {
        if (numPersonajes < 2) {
            JOptionPane.showMessageDialog(null, "Necesitas al menos 2 personajes.");
            return;
        }

        String p1Input = JOptionPane.showInputDialog("ID/Nombre Personaje 1:");
        if (p1Input == null || p1Input.trim().isEmpty()) return;
        int index1 = buscarPorIdONombre(p1Input);
        if (index1 == -1 || personajes[index1].getHp() <= 0) {
            JOptionPane.showMessageDialog(null, "Personaje 1 inválido o sin HP.");
            return;
        }

        String p2Input = JOptionPane.showInputDialog("ID/Nombre Personaje 2:");
        if (p2Input == null || p2Input.trim().isEmpty()) return;
        int index2 = buscarPorIdONombre(p2Input);
        if (index2 == -1 || index2 == index1 || personajes[index2].getHp() <= 0) {
            JOptionPane.showMessageDialog(null, "Personaje 2 inválido o igual al 1.");
            return;
        }

        Personaje p1 = new Personaje(personajes[index1].getId(), personajes[index1].getNombre(), personajes[index1].getArma(), personajes[index1].getHp(),
                                    personajes[index1].getAtaque(), personajes[index1].getVelocidad(), personajes[index1].getAgilidad(), personajes[index1].getDefensa());
        Personaje p2 = new Personaje(personajes[index2].getId(), personajes[index2].getNombre(), personajes[index2].getArma(), personajes[index2].getHp(),
                                    personajes[index2].getAtaque(), personajes[index2].getVelocidad(), personajes[index2].getAgilidad(), personajes[index2].getDefensa());

        Batalla batalla = new Batalla(numBatallas + 1, p1.getNombre(), p2.getNombre());
        historial[numBatallas++] = batalla;

        JFrame frame = new JFrame("Batalla: " + p1.getNombre() + " vs " + p2.getNombre());
        frame.setSize(400, 300);
        JTextArea logArea = new JTextArea();
        logArea.setEditable(false);
        frame.add(new JScrollPane(logArea));
        frame.setVisible(true);

        Thread t1 = new Thread(() -> {
            while (p1.getHp() > 0 && p2.getHp() > 0) {
                try { Thread.sleep(1000 / p1.getVelocidad()); } catch (InterruptedException e) {}
                if (!p2.esquivar()) {
                    int danio = p1.getAtaque();
                    p2.recibirDanio(danio);
                    String evento = p1.getNombre() + " ataca a " + p2.getNombre() + " - Daño: " + (danio - p2.getDefensa()) + " (HP: " + p2.getHp() + ")";
                    batalla.agregarEvento(evento);
                    logArea.append(evento + "\n");
                } else {
                    String evento = p1.getNombre() + " ataca a " + p2.getNombre() + " - Falló (esquiva)";
                    batalla.agregarEvento(evento);
                    logArea.append(evento + "\n");
                }
            }
        });

        Thread t2 = new Thread(() -> {
            while (p1.getHp() > 0 && p2.getHp() > 0) {
                try { Thread.sleep(1000 / p2.getVelocidad()); } catch (InterruptedException e) {}
                if (!p1.esquivar()) {
                    int danio = p2.getAtaque();
                    p1.recibirDanio(danio);
                    String evento = p2.getNombre() + " ataca a " + p1.getNombre() + " - Daño: " + (danio - p1.getDefensa()) + " (HP: " + p1.getHp() + ")";
                    batalla.agregarEvento(evento);
                    logArea.append(evento + "\n");
                } else {
                    String evento = p2.getNombre() + " ataca a " + p1.getNombre() + " - Falló (esquiva)";
                    batalla.agregarEvento(evento);
                    logArea.append(evento + "\n");
                }
            }
        });

        t1.start();
        t2.start();
        try { t1.join(); t2.join(); } catch (InterruptedException e) {}

        String ganador = p1.getHp() > 0 ? p1.getNombre() : p2.getNombre();
        batalla.setGanador(ganador);
        if (ganador.equals(p1.getNombre())) {
            personajes[index1].ganarBatalla();
            personajes[index2].perderBatalla();
        } else {
            personajes[index2].ganarBatalla();
            personajes[index1].perderBatalla();
        }
        logArea.append("Ganador: " + ganador + "\n");
    }

    private static void verHistorial() {
        if (numBatallas == 0) {
            JOptionPane.showMessageDialog(null, "No hay batallas registradas.");
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < numBatallas; i++) {
            if (historial[i] != null) {
                sb.append(historial[i].getInfo()).append("\n");
            }
        }
        JTextArea area = new JTextArea(sb.toString());
        area.setEditable(false);
        JOptionPane.showMessageDialog(null, new JScrollPane(area));
    }

    private static void buscarPersonaje() {
        String nombre = JOptionPane.showInputDialog("Nombre:");
        if (nombre == null || nombre.trim().isEmpty()) return;
        int index = buscarPorNombre(nombre);
        if (index == -1) {
            JOptionPane.showMessageDialog(null, "No encontrado.");
            return;
        }
        Personaje p = personajes[index];
        String info = p.toString() + "\nBatallas: " + (p.getBatallasGanadas() + p.getBatallasPerdidas()) +
                      "\nGanadas: " + p.getBatallasGanadas() + "\nPerdidas: " + p.getBatallasPerdidas();
        JOptionPane.showMessageDialog(null, info);
    }

    private static void guardarEstado() {
        try (PrintWriter writer = new PrintWriter("estado.txt")) {
            writer.println(numPersonajes);
            for (int i = 0; i < numPersonajes; i++) {
                if (personajes[i] != null) {
                    Personaje p = personajes[i];
                    writer.println(p.getId() + "," + p.getNombre() + "," + p.getArma() + "," + p.getHp() + "," + p.getAtaque() + "," +
                                   p.getVelocidad() + "," + p.getAgilidad() + "," + p.getDefensa() + "," +
                                   p.getBatallasGanadas() + "," + p.getBatallasPerdidas());
                }
            }
            writer.println(numBatallas);
            for (int i = 0; i < numBatallas; i++) {
                if (historial[i] != null) {
                    Batalla b = historial[i];
                    writer.println(b.getId() + "," + b.getParticipante1() + "," + b.getParticipante2() + "," + b.getGanador() + "," + b.getFecha());
                    writer.println(b.getBitacora());
                    writer.println("---");
                }
            }
            JOptionPane.showMessageDialog(null, "Guardado.");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error al guardar.");
        }
    }

    private static void cargarEstado() {
        try (Scanner scanner = new Scanner(new File("estado.txt"))) {
            numPersonajes = scanner.nextInt();
            scanner.nextLine();
            for (int i = 0; i < numPersonajes; i++) {
                String[] parts = scanner.nextLine().split(",");
                if (parts.length >= 10) {
                    int id = Integer.parseInt(parts[0]);
                    personajes[i] = new Personaje(id, parts[1], parts[2], Integer.parseInt(parts[3]),
                                                  Integer.parseInt(parts[4]), Integer.parseInt(parts[5]), Integer.parseInt(parts[6]),
                                                  Integer.parseInt(parts[7]));
                    for (int j = 0; j < Integer.parseInt(parts[8]); j++) personajes[i].ganarBatalla();
                    for (int j = 0; j < Integer.parseInt(parts[9]); j++) personajes[i].perderBatalla();
                    nextId = Math.max(nextId, id + 1);
                }
            }
            numBatallas = scanner.nextInt();
            scanner.nextLine();
            for (int i = 0; i < numBatallas; i++) {
                String[] parts = scanner.nextLine().split(",");
                if (parts.length >= 5) {
                    Batalla b = new Batalla(Integer.parseInt(parts[0]), parts[1], parts[2]);
                    b.setGanador(parts[3]);
                    StringBuilder bitacora = new StringBuilder();
                    String line;
                    while (scanner.hasNextLine() && !(line = scanner.nextLine()).equals("---")) {
                        bitacora.append(line).append("\n");
                    }
                    b.agregarEvento(bitacora.toString().trim());
                    historial[i] = b;
                }
            }
            JOptionPane.showMessageDialog(null, "Cargado.");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error al cargar (verifica que exista estado.txt).");
        }
    }

    private static void mostrarDatosEstudiante() {
        JOptionPane.showMessageDialog(null, ESTUDIANTE_INFO);
    }

    private static int getIntInput(String mensaje, int min, int max) {
        while (true) {
            try {
                String input = JOptionPane.showInputDialog(mensaje);
                if (input == null) return min;
                int valor = Integer.parseInt(input.trim());
                if (valor >= min && valor <= max) return valor;
                JOptionPane.showMessageDialog(null, "Valor fuera de rango (" + min + "-" + max + ").");
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Entrada inválida. Ingresa un número.");
            }
        }
    }

    private static int buscarPorNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) return -1;
        nombre = nombre.toLowerCase();
        for (int i = 0; i < numPersonajes; i++) {
            if (personajes[i] != null && personajes[i].getNombre().equals(nombre)) return i;
        }
        return -1;
    }

    private static int buscarPorIdONombre(String input) {
        if (input == null || input.trim().isEmpty()) return -1;
        try {
            int id = Integer.parseInt(input);
            for (int i = 0; i < numPersonajes; i++) {
                if (personajes[i] != null && personajes[i].getId() == id) return i;
            }
        } catch (NumberFormatException e) {
            return buscarPorNombre(input);
        }
        return -1;
    }
}import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.Scanner;

public class Main {
    private static Personaje[] personajes = new Personaje[50];
    private static int numPersonajes = 0;
    private static Batalla[] historial = new Batalla[50];
    private static int numBatallas = 0;
    private static int nextId = 1;

    private static final String ESTUDIANTE_INFO = "Nombre: TuNombre\nCarnet: TuCarnet\nSección: TuSección";

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::mostrarMenuPrincipal);
    }

    private static void mostrarMenuPrincipal() {
        JFrame frame = new JFrame("ArenaUSAC");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridLayout(10, 1));

        JButton[] botones = {
            new JButton("Agregar Personaje"), new JButton("Modificar Personaje"),
            new JButton("Eliminar Personaje"), new JButton("Visualizar Personajes"),
            new JButton("Simular Batalla"), new JButton("Ver Historial de Batallas"),
            new JButton("Buscar Personaje por Nombre"), new JButton("Guardar Estado"),
            new JButton("Cargar Estado"), new JButton("Ver Datos de Estudiante")
        };

        String[] acciones = {
            "agregarPersonaje", "modificarPersonaje", "eliminarPersonaje", "visualizarPersonajes",
            "simularBatalla", "verHistorial", "buscarPersonaje", "guardarEstado",
            "cargarEstado", "mostrarDatosEstudiante"
        };

        for (int i = 0; i < botones.length; i++) {
            int index = i;
            botones[i].addActionListener(e -> {
                try {
                    Main.class.getMethod(acciones[index]).invoke(null);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
                }
            });
            frame.add(botones[i]);
        }

        frame.setVisible(true);
    }

    private static void agregarPersonaje() {
        if (numPersonajes >= personajes.length) {
            JOptionPane.showMessageDialog(null, "Límite de personajes alcanzado (50).");
            return;
        }

        String nombre = JOptionPane.showInputDialog("Nombre:");
        if (nombre == null || nombre.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Nombre inválido.");
            return;
        }

        if (numPersonajes > 0 && buscarPorNombre(nombre) != -1) {
            JOptionPane.showMessageDialog(null, "Nombre duplicado.");
            return;
        }

        String arma = JOptionPane.showInputDialog("Arma:");
        if (arma == null || arma.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Arma inválida.");
            return;
        }

        int hp = getIntInput("HP (100-500):", 100, 500);
        int ataque = getIntInput("Ataque (10-100):", 10, 100);
        int velocidad = getIntInput("Velocidad (1-10):", 1, 10);
        int agilidad = getIntInput("Agilidad (1-10):", 1, 10);
        int defensa = getIntInput("Defensa (1-50):", 1, 50);

        personajes[numPersonajes] = new Personaje(nextId++, nombre, arma, hp, ataque, velocidad, agilidad, defensa);
        numPersonajes++;
        JOptionPane.showMessageDialog(null, "Personaje agregado con ID: " + (nextId - 1));
    }

    private static void modificarPersonaje() {
        String input = JOptionPane.showInputDialog("ID o Nombre:");
        if (input == null || input.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Entrada inválida.");
            return;
        }
        int index = buscarPorIdONombre(input);
        if (index == -1) {
            JOptionPane.showMessageDialog(null, "No encontrado.");
            return;
        }

        JOptionPane.showMessageDialog(null, "Datos actuales: " + personajes[index].toString());
        String nuevaArma = JOptionPane.showInputDialog("Nueva Arma:", personajes[index].getArma());
        if (nuevaArma != null && !nuevaArma.trim().isEmpty()) {
            personajes[index].setArma(nuevaArma);
        }
        int nuevoHp = getIntInput("Nuevo HP (100-500):", 100, 500);
        personajes[index].setHp(nuevoHp);
        JOptionPane.showMessageDialog(null, "Personaje modificado.");
    }

    private static void eliminarPersonaje() {
        String input = JOptionPane.showInputDialog("ID o Nombre:");
        if (input == null || input.trim().isEmpty()) {
            return;
        }
        int index = buscarPorIdONombre(input);
        if (index == -1) {
            JOptionPane.showMessageDialog(null, "No encontrado.");
            return;
        }

        if (JOptionPane.showConfirmDialog(null, "¿Eliminar " + personajes[index].getNombre() + "?", "Confirmar", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            for (int i = index; i < numPersonajes - 1; i++) {
                personajes[i] = personajes[i + 1];
            }
            personajes[numPersonajes - 1] = null;
            numPersonajes--;
            JOptionPane.showMessageDialog(null, "Personaje eliminado.");
        }
    }

    private static void visualizarPersonajes() {
        if (numPersonajes == 0) {
            JOptionPane.showMessageDialog(null, "No hay personajes registrados.");
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < numPersonajes; i++) {
            if (personajes[i] != null) {
                sb.append(personajes[i].toString()).append("\n");
            }
        }
        JTextArea area = new JTextArea(sb.toString());
        area.setEditable(false);
        JOptionPane.showMessageDialog(null, new JScrollPane(area));
    }

    private static void simularBatalla() {
        if (numPersonajes < 2) {
            JOptionPane.showMessageDialog(null, "Necesitas al menos 2 personajes.");
            return;
        }

        String p1Input = JOptionPane.showInputDialog("ID/Nombre Personaje 1:");
        if (p1Input == null || p1Input.trim().isEmpty()) return;
        int index1 = buscarPorIdONombre(p1Input);
        if (index1 == -1 || personajes[index1].getHp() <= 0) {
            JOptionPane.showMessageDialog(null, "Personaje 1 inválido o sin HP.");
            return;
        }

        String p2Input = JOptionPane.showInputDialog("ID/Nombre Personaje 2:");
        if (p2Input == null || p2Input.trim().isEmpty()) return;
        int index2 = buscarPorIdONombre(p2Input);
        if (index2 == -1 || index2 == index1 || personajes[index2].getHp() <= 0) {
            JOptionPane.showMessageDialog(null, "Personaje 2 inválido o igual al 1.");
            return;
        }

        Personaje p1 = new Personaje(personajes[index1].getId(), personajes[index1].getNombre(), personajes[index1].getArma(), personajes[index1].getHp(),
                                    personajes[index1].getAtaque(), personajes[index1].getVelocidad(), personajes[index1].getAgilidad(), personajes[index1].getDefensa());
        Personaje p2 = new Personaje(personajes[index2].getId(), personajes[index2].getNombre(), personajes[index2].getArma(), personajes[index2].getHp(),
                                    personajes[index2].getAtaque(), personajes[index2].getVelocidad(), personajes[index2].getAgilidad(), personajes[index2].getDefensa());

        Batalla batalla = new Batalla(numBatallas + 1, p1.getNombre(), p2.getNombre());
        historial[numBatallas++] = batalla;

        JFrame frame = new JFrame("Batalla: " + p1.getNombre() + " vs " + p2.getNombre());
        frame.setSize(400, 300);
        JTextArea logArea = new JTextArea();
        logArea.setEditable(false);
        frame.add(new JScrollPane(logArea));
        frame.setVisible(true);

        Thread t1 = new Thread(() -> {
            while (p1.getHp() > 0 && p2.getHp() > 0) {
                try { Thread.sleep(1000 / p1.getVelocidad()); } catch (InterruptedException e) {}
                if (!p2.esquivar()) {
                    int danio = p1.getAtaque();
                    p2.recibirDanio(danio);
                    String evento = p1.getNombre() + " ataca a " + p2.getNombre() + " - Daño: " + (danio - p2.getDefensa()) + " (HP: " + p2.getHp() + ")";
                    batalla.agregarEvento(evento);
                    logArea.append(evento + "\n");
                } else {
                    String evento = p1.getNombre() + " ataca a " + p2.getNombre() + " - Falló (esquiva)";
                    batalla.agregarEvento(evento);
                    logArea.append(evento + "\n");
                }
            }
        });

        Thread t2 = new Thread(() -> {
            while (p1.getHp() > 0 && p2.getHp() > 0) {
                try { Thread.sleep(1000 / p2.getVelocidad()); } catch (InterruptedException e) {}
                if (!p1.esquivar()) {
                    int danio = p2.getAtaque();
                    p1.recibirDanio(danio);
                    String evento = p2.getNombre() + " ataca a " + p1.getNombre() + " - Daño: " + (danio - p1.getDefensa()) + " (HP: " + p1.getHp() + ")";
                    batalla.agregarEvento(evento);
                    logArea.append(evento + "\n");
                } else {
                    String evento = p2.getNombre() + " ataca a " + p1.getNombre() + " - Falló (esquiva)";
                    batalla.agregarEvento(evento);
                    logArea.append(evento + "\n");
                }
            }
        });

        t1.start();
        t2.start();
        try { t1.join(); t2.join(); } catch (InterruptedException e) {}

        String ganador = p1.getHp() > 0 ? p1.getNombre() : p2.getNombre();
        batalla.setGanador(ganador);
        if (ganador.equals(p1.getNombre())) {
            personajes[index1].ganarBatalla();
            personajes[index2].perderBatalla();
        } else {
            personajes[index2].ganarBatalla();
            personajes[index1].perderBatalla();
        }
        logArea.append("Ganador: " + ganador + "\n");
    }

    private static void verHistorial() {
        if (numBatallas == 0) {
            JOptionPane.showMessageDialog(null, "No hay batallas registradas.");
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < numBatallas; i++) {
            if (historial[i] != null) {
                sb.append(historial[i].getInfo()).append("\n");
            }
        }
        JTextArea area = new JTextArea(sb.toString());
        area.setEditable(false);
        JOptionPane.showMessageDialog(null, new JScrollPane(area));
    }

    private static void buscarPersonaje() {
        String nombre = JOptionPane.showInputDialog("Nombre:");
        if (nombre == null || nombre.trim().isEmpty()) return;
        int index = buscarPorNombre(nombre);
        if (index == -1) {
            JOptionPane.showMessageDialog(null, "No encontrado.");
            return;
        }
        Personaje p = personajes[index];
        String info = p.toString() + "\nBatallas: " + (p.getBatallasGanadas() + p.getBatallasPerdidas()) +
                      "\nGanadas: " + p.getBatallasGanadas() + "\nPerdidas: " + p.getBatallasPerdidas();
        JOptionPane.showMessageDialog(null, info);
    }

    private static void guardarEstado() {
        try (PrintWriter writer = new PrintWriter("estado.txt")) {
            writer.println(numPersonajes);
            for (int i = 0; i < numPersonajes; i++) {
                if (personajes[i] != null) {
                    Personaje p = personajes[i];
                    writer.println(p.getId() + "," + p.getNombre() + "," + p.getArma() + "," + p.getHp() + "," + p.getAtaque() + "," +
                                   p.getVelocidad() + "," + p.getAgilidad() + "," + p.getDefensa() + "," +
                                   p.getBatallasGanadas() + "," + p.getBatallasPerdidas());
                }
            }
            writer.println(numBatallas);
            for (int i = 0; i < numBatallas; i++) {
                if (historial[i] != null) {
                    Batalla b = historial[i];
                    writer.println(b.getId() + "," + b.getParticipante1() + "," + b.getParticipante2() + "," + b.getGanador() + "," + b.getFecha());
                    writer.println(b.getBitacora());
                    writer.println("---");
                }
            }
            JOptionPane.showMessageDialog(null, "Guardado.");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error al guardar.");
        }
    }

    private static void cargarEstado() {
        try (Scanner scanner = new Scanner(new File("estado.txt"))) {
            numPersonajes = scanner.nextInt();
            scanner.nextLine();
            for (int i = 0; i < numPersonajes; i++) {
                String[] parts = scanner.nextLine().split(",");
                if (parts.length >= 10) {
                    int id = Integer.parseInt(parts[0]);
                    personajes[i] = new Personaje(id, parts[1], parts[2], Integer.parseInt(parts[3]),
                                                  Integer.parseInt(parts[4]), Integer.parseInt(parts[5]), Integer.parseInt(parts[6]),
                                                  Integer.parseInt(parts[7]));
                    for (int j = 0; j < Integer.parseInt(parts[8]); j++) personajes[i].ganarBatalla();
                    for (int j = 0; j < Integer.parseInt(parts[9]); j++) personajes[i].perderBatalla();
                    nextId = Math.max(nextId, id + 1);
                }
            }
            numBatallas = scanner.nextInt();
            scanner.nextLine();
            for (int i = 0; i < numBatallas; i++) {
                String[] parts = scanner.nextLine().split(",");
                if (parts.length >= 5) {
                    Batalla b = new Batalla(Integer.parseInt(parts[0]), parts[1], parts[2]);
                    b.setGanador(parts[3]);
                    StringBuilder bitacora = new StringBuilder();
                    String line;
                    while (scanner.hasNextLine() && !(line = scanner.nextLine()).equals("---")) {
                        bitacora.append(line).append("\n");
                    }
                    b.agregarEvento(bitacora.toString().trim());
                    historial[i] = b;
                }
            }
            JOptionPane.showMessageDialog(null, "Cargado.");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error al cargar (verifica que exista estado.txt).");
        }
    }

    private static void mostrarDatosEstudiante() {
        JOptionPane.showMessageDialog(null, ESTUDIANTE_INFO);
    }

    private static int getIntInput(String mensaje, int min, int max) {
        while (true) {
            try {
                String input = JOptionPane.showInputDialog(mensaje);
                if (input == null) return min;
                int valor = Integer.parseInt(input.trim());
                if (valor >= min && valor <= max) return valor;
                JOptionPane.showMessageDialog(null, "Valor fuera de rango (" + min + "-" + max + ").");
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Entrada inválida. Ingresa un número.");
            }
        }
    }

    private static int buscarPorNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) return -1;
        nombre = nombre.toLowerCase();
        for (int i = 0; i < numPersonajes; i++) {
            if (personajes[i] != null && personajes[i].getNombre().equals(nombre)) return i;
        }
        return -1;
    }

    private static int buscarPorIdONombre(String input) {
        if (input == null || input.trim().isEmpty()) return -1;
        try {
            int id = Integer.parseInt(input);
            for (int i = 0; i < numPersonajes; i++) {
                if (personajes[i] != null && personajes[i].getId() == id) return i;
            }
        } catch (NumberFormatException e) {
            return buscarPorNombre(input);
        }
        return -1;
    }
}import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.Scanner;

public class Main {
    private static Personaje[] personajes = new Personaje[50];
    private static int numPersonajes = 0;
    private static Batalla[] historial = new Batalla[50];
    private static int numBatallas = 0;
    private static int nextId = 1;

    private static final String ESTUDIANTE_INFO = "Nombre: TuNombre\nCarnet: TuCarnet\nSección: TuSección";

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::mostrarMenuPrincipal);
    }

    private static void mostrarMenuPrincipal() {
        JFrame frame = new JFrame("ArenaUSAC");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridLayout(10, 1));

        JButton[] botones = {
            new JButton("Agregar Personaje"), new JButton("Modificar Personaje"),
            new JButton("Eliminar Personaje"), new JButton("Visualizar Personajes"),
            new JButton("Simular Batalla"), new JButton("Ver Historial de Batallas"),
            new JButton("Buscar Personaje por Nombre"), new JButton("Guardar Estado"),
            new JButton("Cargar Estado"), new JButton("Ver Datos de Estudiante")
        };

        String[] acciones = {
            "agregarPersonaje", "modificarPersonaje", "eliminarPersonaje", "visualizarPersonajes",
            "simularBatalla", "verHistorial", "buscarPersonaje", "guardarEstado",
            "cargarEstado", "mostrarDatosEstudiante"
        };

        for (int i = 0; i < botones.length; i++) {
            int index = i;
            botones[i].addActionListener(e -> {
                try {
                    Main.class.getMethod(acciones[index]).invoke(null);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
                }
            });
            frame.add(botones[i]);
        }

        frame.setVisible(true);
    }

    private static void agregarPersonaje() {
        if (numPersonajes >= personajes.length) {
            JOptionPane.showMessageDialog(null, "Límite de personajes alcanzado (50).");
            return;
        }

        String nombre = JOptionPane.showInputDialog("Nombre:");
        if (nombre == null || nombre.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Nombre inválido.");
            return;
        }

        if (numPersonajes > 0 && buscarPorNombre(nombre) != -1) {
            JOptionPane.showMessageDialog(null, "Nombre duplicado.");
            return;
        }

        String arma = JOptionPane.showInputDialog("Arma:");
        if (arma == null || arma.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Arma inválida.");
            return;
        }

        int hp = getIntInput("HP (100-500):", 100, 500);
        int ataque = getIntInput("Ataque (10-100):", 10, 100);
        int velocidad = getIntInput("Velocidad (1-10):", 1, 10);
        int agilidad = getIntInput("Agilidad (1-10):", 1, 10);
        int defensa = getIntInput("Defensa (1-50):", 1, 50);

        personajes[numPersonajes] = new Personaje(nextId++, nombre, arma, hp, ataque, velocidad, agilidad, defensa);
        numPersonajes++;
        JOptionPane.showMessageDialog(null, "Personaje agregado con ID: " + (nextId - 1));
    }

    private static void modificarPersonaje() {
        String input = JOptionPane.showInputDialog("ID o Nombre:");
        if (input == null || input.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Entrada inválida.");
            return;
        }
        int index = buscarPorIdONombre(input);
        if (index == -1) {
            JOptionPane.showMessageDialog(null, "No encontrado.");
            return;
        }

        JOptionPane.showMessageDialog(null, "Datos actuales: " + personajes[index].toString());
        String nuevaArma = JOptionPane.showInputDialog("Nueva Arma:", personajes[index].getArma());
        if (nuevaArma != null && !nuevaArma.trim().isEmpty()) {
            personajes[index].setArma(nuevaArma);
        }
        int nuevoHp = getIntInput("Nuevo HP (100-500):", 100, 500);
        personajes[index].setHp(nuevoHp);
        JOptionPane.showMessageDialog(null, "Personaje modificado.");
    }

    private static void eliminarPersonaje() {
        String input = JOptionPane.showInputDialog("ID o Nombre:");
        if (input == null || input.trim().isEmpty()) {
            return;
        }
        int index = buscarPorIdONombre(input);
        if (index == -1) {
            JOptionPane.showMessageDialog(null, "No encontrado.");
            return;
        }

        if (JOptionPane.showConfirmDialog(null, "¿Eliminar " + personajes[index].getNombre() + "?", "Confirmar", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            for (int i = index; i < numPersonajes - 1; i++) {
                personajes[i] = personajes[i + 1];
            }
            personajes[numPersonajes - 1] = null;
            numPersonajes--;
            JOptionPane.showMessageDialog(null, "Personaje eliminado.");
        }
    }

    private static void visualizarPersonajes() {
        if (numPersonajes == 0) {
            JOptionPane.showMessageDialog(null, "No hay personajes registrados.");
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < numPersonajes; i++) {
            if (personajes[i] != null) {
                sb.append(personajes[i].toString()).append("\n");
            }
        }
        JTextArea area = new JTextArea(sb.toString());
        area.setEditable(false);
        JOptionPane.showMessageDialog(null, new JScrollPane(area));
    }

    private static void simularBatalla() {
        if (numPersonajes < 2) {
            JOptionPane.showMessageDialog(null, "Necesitas al menos 2 personajes.");
            return;
        }

        String p1Input = JOptionPane.showInputDialog("ID/Nombre Personaje 1:");
        if (p1Input == null || p1Input.trim().isEmpty()) return;
        int index1 = buscarPorIdONombre(p1Input);
        if (index1 == -1 || personajes[index1].getHp() <= 0) {
            JOptionPane.showMessageDialog(null, "Personaje 1 inválido o sin HP.");
            return;
        }

        String p2Input = JOptionPane.showInputDialog("ID/Nombre Personaje 2:");
        if (p2Input == null || p2Input.trim().isEmpty()) return;
        int index2 = buscarPorIdONombre(p2Input);
        if (index2 == -1 || index2 == index1 || personajes[index2].getHp() <= 0) {
            JOptionPane.showMessageDialog(null, "Personaje 2 inválido o igual al 1.");
            return;
        }

        Personaje p1 = new Personaje(personajes[index1].getId(), personajes[index1].getNombre(), personajes[index1].getArma(), personajes[index1].getHp(),
                                    personajes[index1].getAtaque(), personajes[index1].getVelocidad(), personajes[index1].getAgilidad(), personajes[index1].getDefensa());
        Personaje p2 = new Personaje(personajes[index2].getId(), personajes[index2].getNombre(), personajes[index2].getArma(), personajes[index2].getHp(),
                                    personajes[index2].getAtaque(), personajes[index2].getVelocidad(), personajes[index2].getAgilidad(), personajes[index2].getDefensa());

        Batalla batalla = new Batalla(numBatallas + 1, p1.getNombre(), p2.getNombre());
        historial[numBatallas++] = batalla;

        JFrame frame = new JFrame("Batalla: " + p1.getNombre() + " vs " + p2.getNombre());
        frame.setSize(400, 300);
        JTextArea logArea = new JTextArea();
        logArea.setEditable(false);
        frame.add(new JScrollPane(logArea));
        frame.setVisible(true);

        Thread t1 = new Thread(() -> {
            while (p1.getHp() > 0 && p2.getHp() > 0) {
                try { Thread.sleep(1000 / p1.getVelocidad()); } catch (InterruptedException e) {}
                if (!p2.esquivar()) {
                    int danio = p1.getAtaque();
                    p2.recibirDanio(danio);
                    String evento = p1.getNombre() + " ataca a " + p2.getNombre() + " - Daño: " + (danio - p2.getDefensa()) + " (HP: " + p2.getHp() + ")";
                    batalla.agregarEvento(evento);
                    logArea.append(evento + "\n");
                } else {
                    String evento = p1.getNombre() + " ataca a " + p2.getNombre() + " - Falló (esquiva)";
                    batalla.agregarEvento(evento);
                    logArea.append(evento + "\n");
                }
            }
        });

        Thread t2 = new Thread(() -> {
            while (p1.getHp() > 0 && p2.getHp() > 0) {
                try { Thread.sleep(1000 / p2.getVelocidad()); } catch (InterruptedException e) {}
                if (!p1.esquivar()) {
                    int danio = p2.getAtaque();
                    p1.recibirDanio(danio);
                    String evento = p2.getNombre() + " ataca a " + p1.getNombre() + " - Daño: " + (danio - p1.getDefensa()) + " (HP: " + p1.getHp() + ")";
                    batalla.agregarEvento(evento);
                    logArea.append(evento + "\n");
                } else {
                    String evento = p2.getNombre() + " ataca a " + p1.getNombre() + " - Falló (esquiva)";
                    batalla.agregarEvento(evento);
                    logArea.append(evento + "\n");
                }
            }
        });

        t1.start();
        t2.start();
        try { t1.join(); t2.join(); } catch (InterruptedException e) {}

        String ganador = p1.getHp() > 0 ? p1.getNombre() : p2.getNombre();
        batalla.setGanador(ganador);
        if (ganador.equals(p1.getNombre())) {
            personajes[index1].ganarBatalla();
            personajes[index2].perderBatalla();
        } else {
            personajes[index2].ganarBatalla();
            personajes[index1].perderBatalla();
        }
        logArea.append("Ganador: " + ganador + "\n");
    }

    private static void verHistorial() {
        if (numBatallas == 0) {
            JOptionPane.showMessageDialog(null, "No hay batallas registradas.");
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < numBatallas; i++) {
            if (historial[i] != null) {
                sb.append(historial[i].getInfo()).append("\n");
            }
        }
        JTextArea area = new JTextArea(sb.toString());
        area.setEditable(false);
        JOptionPane.showMessageDialog(null, new JScrollPane(area));
    }

    private static void buscarPersonaje() {
        String nombre = JOptionPane.showInputDialog("Nombre:");
        if (nombre == null || nombre.trim().isEmpty()) return;
        int index = buscarPorNombre(nombre);
        if (index == -1) {
            JOptionPane.showMessageDialog(null, "No encontrado.");
            return;
        }
        Personaje p = personajes[index];
        String info = p.toString() + "\nBatallas: " + (p.getBatallasGanadas() + p.getBatallasPerdidas()) +
                      "\nGanadas: " + p.getBatallasGanadas() + "\nPerdidas: " + p.getBatallasPerdidas();
        JOptionPane.showMessageDialog(null, info);
    }

    private static void guardarEstado() {
        try (PrintWriter writer = new PrintWriter("estado.txt")) {
            writer.println(numPersonajes);
            for (int i = 0; i < numPersonajes; i++) {
                if (personajes[i] != null) {
                    Personaje p = personajes[i];
                    writer.println(p.getId() + "," + p.getNombre() + "," + p.getArma() + "," + p.getHp() + "," + p.getAtaque() + "," +
                                   p.getVelocidad() + "," + p.getAgilidad() + "," + p.getDefensa() + "," +
                                   p.getBatallasGanadas() + "," + p.getBatallasPerdidas());
                }
            }
            writer.println(numBatallas);
            for (int i = 0; i < numBatallas; i++) {
                if (historial[i] != null) {
                    Batalla b = historial[i];
                    writer.println(b.getId() + "," + b.getParticipante1() + "," + b.getParticipante2() + "," + b.getGanador() + "," + b.getFecha());
                    writer.println(b.getBitacora());
                    writer.println("---");
                }
            }
            JOptionPane.showMessageDialog(null, "Guardado.");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error al guardar.");
        }
    }

    private static void cargarEstado() {
        try (Scanner scanner = new Scanner(new File("estado.txt"))) {
            numPersonajes = scanner.nextInt();
            scanner.nextLine();
            for (int i = 0; i < numPersonajes; i++) {
                String[] parts = scanner.nextLine().split(",");
                if (parts.length >= 10) {
                    int id = Integer.parseInt(parts[0]);
                    personajes[i] = new Personaje(id, parts[1], parts[2], Integer.parseInt(parts[3]),
                                                  Integer.parseInt(parts[4]), Integer.parseInt(parts[5]), Integer.parseInt(parts[6]),
                                                  Integer.parseInt(parts[7]));
                    for (int j = 0; j < Integer.parseInt(parts[8]); j++) personajes[i].ganarBatalla();
                    for (int j = 0; j < Integer.parseInt(parts[9]); j++) personajes[i].perderBatalla();
                    nextId = Math.max(nextId, id + 1);
                }
            }
            numBatallas = scanner.nextInt();
            scanner.nextLine();
            for (int i = 0; i < numBatallas; i++) {
                String[] parts = scanner.nextLine().split(",");
                if (parts.length >= 5) {
                    Batalla b = new Batalla(Integer.parseInt(parts[0]), parts[1], parts[2]);
                    b.setGanador(parts[3]);
                    StringBuilder bitacora = new StringBuilder();
                    String line;
                    while (scanner.hasNextLine() && !(line = scanner.nextLine()).equals("---")) {
                        bitacora.append(line).append("\n");
                    }
                    b.agregarEvento(bitacora.toString().trim());
                    historial[i] = b;
                }
            }
            JOptionPane.showMessageDialog(null, "Cargado.");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error al cargar (verifica que exista estado.txt).");
        }
    }

    private static void mostrarDatosEstudiante() {
        JOptionPane.showMessageDialog(null, ESTUDIANTE_INFO);
    }

    private static int getIntInput(String mensaje, int min, int max) {
        while (true) {
            try {
                String input = JOptionPane.showInputDialog(mensaje);
                if (input == null) return min;
                int valor = Integer.parseInt(input.trim());
                if (valor >= min && valor <= max) return valor;
                JOptionPane.showMessageDialog(null, "Valor fuera de rango (" + min + "-" + max + ").");
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Entrada inválida. Ingresa un número.");
            }
        }
    }

    private static int buscarPorNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) return -1;
        nombre = nombre.toLowerCase();
        for (int i = 0; i < numPersonajes; i++) {
            if (personajes[i] != null && personajes[i].getNombre().equals(nombre)) return i;
        }
        return -1;
    }

    private static int buscarPorIdONombre(String input) {
        if (input == null || input.trim().isEmpty()) return -1;
        try {
            int id = Integer.parseInt(input);
            for (int i = 0; i < numPersonajes; i++) {
                if (personajes[i] != null && personajes[i].getId() == id) return i;
            }
        } catch (NumberFormatException e) {
            return buscarPorNombre(input);
        }
        return -1;
    }
}import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.Scanner;

public class Main {
    private static Personaje[] personajes = new Personaje[50];
    private static int numPersonajes = 0;
    private static Batalla[] historial = new Batalla[50];
    private static int numBatallas = 0;
    private static int nextId = 1;

    private static final String ESTUDIANTE_INFO = "Nombre: TuNombre\nCarnet: TuCarnet\nSección: TuSección";

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::mostrarMenuPrincipal);
    }

    private static void mostrarMenuPrincipal() {
        JFrame frame = new JFrame("ArenaUSAC");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridLayout(10, 1));

        JButton[] botones = {
            new JButton("Agregar Personaje"), new JButton("Modificar Personaje"),
            new JButton("Eliminar Personaje"), new JButton("Visualizar Personajes"),
            new JButton("Simular Batalla"), new JButton("Ver Historial de Batallas"),
            new JButton("Buscar Personaje por Nombre"), new JButton("Guardar Estado"),
            new JButton("Cargar Estado"), new JButton("Ver Datos de Estudiante")
        };

        String[] acciones = {
            "agregarPersonaje", "modificarPersonaje", "eliminarPersonaje", "visualizarPersonajes",
            "simularBatalla", "verHistorial", "buscarPersonaje", "guardarEstado",
            "cargarEstado", "mostrarDatosEstudiante"
        };

        for (int i = 0; i < botones.length; i++) {
            int index = i;
            botones[i].addActionListener(e -> {
                try {
                    Main.class.getMethod(acciones[index]).invoke(null);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
                }
            });
            frame.add(botones[i]);
        }

        frame.setVisible(true);
    }

    private static void agregarPersonaje() {
        if (numPersonajes >= personajes.length) {
            JOptionPane.showMessageDialog(null, "Límite de personajes alcanzado (50).");
            return;
        }

        String nombre = JOptionPane.showInputDialog("Nombre:");
        if (nombre == null || nombre.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Nombre inválido.");
            return;
        }

        if (numPersonajes > 0 && buscarPorNombre(nombre) != -1) {
            JOptionPane.showMessageDialog(null, "Nombre duplicado.");
            return;
        }

        String arma = JOptionPane.showInputDialog("Arma:");
        if (arma == null || arma.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Arma inválida.");
            return;
        }

        int hp = getIntInput("HP (100-500):", 100, 500);
        int ataque = getIntInput("Ataque (10-100):", 10, 100);
        int velocidad = getIntInput("Velocidad (1-10):", 1, 10);
        int agilidad = getIntInput("Agilidad (1-10):", 1, 10);
        int defensa = getIntInput("Defensa (1-50):", 1, 50);

        personajes[numPersonajes] = new Personaje(nextId++, nombre, arma, hp, ataque, velocidad, agilidad, defensa);
        numPersonajes++;
        JOptionPane.showMessageDialog(null, "Personaje agregado con ID: " + (nextId - 1));
    }

    private static void modificarPersonaje() {
        String input = JOptionPane.showInputDialog("ID o Nombre:");
        if (input == null || input.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Entrada inválida.");
            return;
        }
        int index = buscarPorIdONombre(input);
        if (index == -1) {
            JOptionPane.showMessageDialog(null, "No encontrado.");
            return;
        }

        JOptionPane.showMessageDialog(null, "Datos actuales: " + personajes[index].toString());
        String nuevaArma = JOptionPane.showInputDialog("Nueva Arma:", personajes[index].getArma());
        if (nuevaArma != null && !nuevaArma.trim().isEmpty()) {
            personajes[index].setArma(nuevaArma);
        }
        int nuevoHp = getIntInput("Nuevo HP (100-500):", 100, 500);
        personajes[index].setHp(nuevoHp);
        JOptionPane.showMessageDialog(null, "Personaje modificado.");
    }

    private static void eliminarPersonaje() {
        String input = JOptionPane.showInputDialog("ID o Nombre:");
        if (input == null || input.trim().isEmpty()) {
            return;
        }
        int index = buscarPorIdONombre(input);
        if (index == -1) {
            JOptionPane.showMessageDialog(null, "No encontrado.");
            return;
        }

        if (JOptionPane.showConfirmDialog(null, "¿Eliminar " + personajes[index].getNombre() + "?", "Confirmar", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            for (int i = index; i < numPersonajes - 1; i++) {
                personajes[i] = personajes[i + 1];
            }
            personajes[numPersonajes - 1] = null;
            numPersonajes--;
            JOptionPane.showMessageDialog(null, "Personaje eliminado.");
        }
    }

    private static void visualizarPersonajes() {
        if (numPersonajes == 0) {
            JOptionPane.showMessageDialog(null, "No hay personajes registrados.");
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < numPersonajes; i++) {
            if (personajes[i] != null) {
                sb.append(personajes[i].toString()).append("\n");
            }
        }
        JTextArea area = new JTextArea(sb.toString());
        area.setEditable(false);
        JOptionPane.showMessageDialog(null, new JScrollPane(area));
    }

    private static void simularBatalla() {
        if (numPersonajes < 2) {
            JOptionPane.showMessageDialog(null, "Necesitas al menos 2 personajes.");
            return;
        }

        String p1Input = JOptionPane.showInputDialog("ID/Nombre Personaje 1:");
        if (p1Input == null || p1Input.trim().isEmpty()) return;
        int index1 = buscarPorIdONombre(p1Input);
        if (index1 == -1 || personajes[index1].getHp() <= 0) {
            JOptionPane.showMessageDialog(null, "Personaje 1 inválido o sin HP.");
            return;
        }

        String p2Input = JOptionPane.showInputDialog("ID/Nombre Personaje 2:");
        if (p2Input == null || p2Input.trim().isEmpty()) return;
        int index2 = buscarPorIdONombre(p2Input);
        if (index2 == -1 || index2 == index1 || personajes[index2].getHp() <= 0) {
            JOptionPane.showMessageDialog(null, "Personaje 2 inválido o igual al 1.");
            return;
        }

        Personaje p1 = new Personaje(personajes[index1].getId(), personajes[index1].getNombre(), personajes[index1].getArma(), personajes[index1].getHp(),
                                    personajes[index1].getAtaque(), personajes[index1].getVelocidad(), personajes[index1].getAgilidad(), personajes[index1].getDefensa());
        Personaje p2 = new Personaje(personajes[index2].getId(), personajes[index2].getNombre(), personajes[index2].getArma(), personajes[index2].getHp(),
                                    personajes[index2].getAtaque(), personajes[index2].getVelocidad(), personajes[index2].getAgilidad(), personajes[index2].getDefensa());

        Batalla batalla = new Batalla(numBatallas + 1, p1.getNombre(), p2.getNombre());
        historial[numBatallas++] = batalla;

        JFrame frame = new JFrame("Batalla: " + p1.getNombre() + " vs " + p2.getNombre());
        frame.setSize(400, 300);
        JTextArea logArea = new JTextArea();
        logArea.setEditable(false);
        frame.add(new JScrollPane(logArea));
        frame.setVisible(true);

        Thread t1 = new Thread(() -> {
            while (p1.getHp() > 0 && p2.getHp() > 0) {
                try { Thread.sleep(1000 / p1.getVelocidad()); } catch (InterruptedException e) {}
                if (!p2.esquivar()) {
                    int danio = p1.getAtaque();
                    p2.recibirDanio(danio);
                    String evento = p1.getNombre() + " ataca a " + p2.getNombre() + " - Daño: " + (danio - p2.getDefensa()) + " (HP: " + p2.getHp() + ")";
                    batalla.agregarEvento(evento);
                    logArea.append(evento + "\n");
                } else {
                    String evento = p1.getNombre() + " ataca a " + p2.getNombre() + " - Falló (esquiva)";
                    batalla.agregarEvento(evento);
                    logArea.append(evento + "\n");
                }
            }
        });

        Thread t2 = new Thread(() -> {
            while (p1.getHp() > 0 && p2.getHp() > 0) {
                try { Thread.sleep(1000 / p2.getVelocidad()); } catch (InterruptedException e) {}
                if (!p1.esquivar()) {
                    int danio = p2.getAtaque();
                    p1.recibirDanio(danio);
                    String evento = p2.getNombre() + " ataca a " + p1.getNombre() + " - Daño: " + (danio - p1.getDefensa()) + " (HP: " + p1.getHp() + ")";
                    batalla.agregarEvento(evento);
                    logArea.append(evento + "\n");
                } else {
                    String evento = p2.getNombre() + " ataca a " + p1.getNombre() + " - Falló (esquiva)";
                    batalla.agregarEvento(evento);
                    logArea.append(evento + "\n");
                }
            }
        });

        t1.start();
        t2.start();
        try { t1.join(); t2.join(); } catch (InterruptedException e) {}

        String ganador = p1.getHp() > 0 ? p1.getNombre() : p2.getNombre();
        batalla.setGanador(ganador);
        if (ganador.equals(p1.getNombre())) {
            personajes[index1].ganarBatalla();
            personajes[index2].perderBatalla();
        } else {
            personajes[index2].ganarBatalla();
            personajes[index1].perderBatalla();
        }
        logArea.append("Ganador: " + ganador + "\n");
    }

    private static void verHistorial() {
        if (numBatallas == 0) {
            JOptionPane.showMessageDialog(null, "No hay batallas registradas.");
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < numBatallas; i++) {
            if (historial[i] != null) {
                sb.append(historial[i].getInfo()).append("\n");
            }
        }
        JTextArea area = new JTextArea(sb.toString());
        area.setEditable(false);
        JOptionPane.showMessageDialog(null, new JScrollPane(area));
    }

    private static void buscarPersonaje() {
        String nombre = JOptionPane.showInputDialog("Nombre:");
        if (nombre == null || nombre.trim().isEmpty()) return;
        int index = buscarPorNombre(nombre);
        if (index == -1) {
            JOptionPane.showMessageDialog(null, "No encontrado.");
            return;
        }
        Personaje p = personajes[index];
        String info = p.toString() + "\nBatallas: " + (p.getBatallasGanadas() + p.getBatallasPerdidas()) +
                      "\nGanadas: " + p.getBatallasGanadas() + "\nPerdidas: " + p.getBatallasPerdidas();
        JOptionPane.showMessageDialog(null, info);
    }

    private static void guardarEstado() {
        try (PrintWriter writer = new PrintWriter("estado.txt")) {
            writer.println(numPersonajes);
            for (int i = 0; i < numPersonajes; i++) {
                if (personajes[i] != null) {
                    Personaje p = personajes[i];
                    writer.println(p.getId() + "," + p.getNombre() + "," + p.getArma() + "," + p.getHp() + "," + p.getAtaque() + "," +
                                   p.getVelocidad() + "," + p.getAgilidad() + "," + p.getDefensa() + "," +
                                   p.getBatallasGanadas() + "," + p.getBatallasPerdidas());
                }
            }
            writer.println(numBatallas);
            for (int i = 0; i < numBatallas; i++) {
                if (historial[i] != null) {
                    Batalla b = historial[i];
                    writer.println(b.getId() + "," + b.getParticipante1() + "," + b.getParticipante2() + "," + b.getGanador() + "," + b.getFecha());
                    writer.println(b.getBitacora());
                    writer.println("---");
                }
            }
            JOptionPane.showMessageDialog(null, "Guardado.");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error al guardar.");
        }
    }

    private static void cargarEstado() {
        try (Scanner scanner = new Scanner(new File("estado.txt"))) {
            numPersonajes = scanner.nextInt();
            scanner.nextLine();
            for (int i = 0; i < numPersonajes; i++) {
                String[] parts = scanner.nextLine().split(",");
                if (parts.length >= 10) {
                    int id = Integer.parseInt(parts[0]);
                    personajes[i] = new Personaje(id, parts[1], parts[2], Integer.parseInt(parts[3]),
                                                  Integer.parseInt(parts[4]), Integer.parseInt(parts[5]), Integer.parseInt(parts[6]),
                                                  Integer.parseInt(parts[7]));
                    for (int j = 0; j < Integer.parseInt(parts[8]); j++) personajes[i].ganarBatalla();
                    for (int j = 0; j < Integer.parseInt(parts[9]); j++) personajes[i].perderBatalla();
                    nextId = Math.max(nextId, id + 1);
                }
            }
            numBatallas = scanner.nextInt();
            scanner.nextLine();
            for (int i = 0; i < numBatallas; i++) {
                String[] parts = scanner.nextLine().split(",");
                if (parts.length >= 5) {
                    Batalla b = new Batalla(Integer.parseInt(parts[0]), parts[1], parts[2]);
                    b.setGanador(parts[3]);
                    StringBuilder bitacora = new StringBuilder();
                    String line;
                    while (scanner.hasNextLine() && !(line = scanner.nextLine()).equals("---")) {
                        bitacora.append(line).append("\n");
                    }
                    b.agregarEvento(bitacora.toString().trim());
                    historial[i] = b;
                }
            }
            JOptionPane.showMessageDialog(null, "Cargado.");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error al cargar (verifica que exista estado.txt).");
        }
    }

    private static void mostrarDatosEstudiante() {
        JOptionPane.showMessageDialog(null, ESTUDIANTE_INFO);
    }

    private static int getIntInput(String mensaje, int min, int max) {
        while (true) {
            try {
                String input = JOptionPane.showInputDialog(mensaje);
                if (input == null) return min;
                int valor = Integer.parseInt(input.trim());
                if (valor >= min && valor <= max) return valor;
                JOptionPane.showMessageDialog(null, "Valor fuera de rango (" + min + "-" + max + ").");
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Entrada inválida. Ingresa un número.");
            }
        }
    }

    private static int buscarPorNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) return -1;
        nombre = nombre.toLowerCase();
        for (int i = 0; i < numPersonajes; i++) {
            if (personajes[i] != null && personajes[i].getNombre().equals(nombre)) return i;
        }
        return -1;
    }

    private static int buscarPorIdONombre(String input) {
        if (input == null || input.trim().isEmpty()) return -1;
        try {
            int id = Integer.parseInt(input);
            for (int i = 0; i < numPersonajes; i++) {
                if (personajes[i] != null && personajes[i].getId() == id) return i;
            }
        } catch (NumberFormatException e) {
            return buscarPorNombre(input);
        }
        return -1;
    }
}import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.Scanner;

public class Main {
    private static Personaje[] personajes = new Personaje[50];
    private static int numPersonajes = 0;
    private static Batalla[] historial = new Batalla[50];
    private static int numBatallas = 0;
    private static int nextId = 1;

    private static final String ESTUDIANTE_INFO = "Nombre: TuNombre\nCarnet: TuCarnet\nSección: TuSección";

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::mostrarMenuPrincipal);
    }

    private static void mostrarMenuPrincipal() {
        JFrame frame = new JFrame("ArenaUSAC");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridLayout(10, 1));

        JButton[] botones = {
            new JButton("Agregar Personaje"), new JButton("Modificar Personaje"),
            new JButton("Eliminar Personaje"), new JButton("Visualizar Personajes"),
            new JButton("Simular Batalla"), new JButton("Ver Historial de Batallas"),
            new JButton("Buscar Personaje por Nombre"), new JButton("Guardar Estado"),
            new JButton("Cargar Estado"), new JButton("Ver Datos de Estudiante")
        };

        String[] acciones = {
            "agregarPersonaje", "modificarPersonaje", "eliminarPersonaje", "visualizarPersonajes",
            "simularBatalla", "verHistorial", "buscarPersonaje", "guardarEstado",
            "cargarEstado", "mostrarDatosEstudiante"
        };

        for (int i = 0; i < botones.length; i++) {
            int index = i;
            botones[i].addActionListener(e -> {
                try {
                    Main.class.getMethod(acciones[index]).invoke(null);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
                }
            });
            frame.add(botones[i]);
        }

        frame.setVisible(true);
    }

    private static void agregarPersonaje() {
        if (numPersonajes >= personajes.length) {
            JOptionPane.showMessageDialog(null, "Límite de personajes alcanzado (50).");
            return;
        }

        String nombre = JOptionPane.showInputDialog("Nombre:");
        if (nombre == null || nombre.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Nombre inválido.");
            return;
        }

        if (numPersonajes > 0 && buscarPorNombre(nombre) != -1) {
            JOptionPane.showMessageDialog(null, "Nombre duplicado.");
            return;
        }

        String arma = JOptionPane.showInputDialog("Arma:");
        if (arma == null || arma.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Arma inválida.");
            return;
        }

        int hp = getIntInput("HP (100-500):", 100, 500);
        int ataque = getIntInput("Ataque (10-100):", 10, 100);
        int velocidad = getIntInput("Velocidad (1-10):", 1, 10);
        int agilidad = getIntInput("Agilidad (1-10):", 1, 10);
        int defensa = getIntInput("Defensa (1-50):", 1, 50);

        personajes[numPersonajes] = new Personaje(nextId++, nombre, arma, hp, ataque, velocidad, agilidad, defensa);
        numPersonajes++;
        JOptionPane.showMessageDialog(null, "Personaje agregado con ID: " + (nextId - 1));
    }

    private static void modificarPersonaje() {
        String input = JOptionPane.showInputDialog("ID o Nombre:");
        if (input == null || input.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Entrada inválida.");
            return;
        }
        int index = buscarPorIdONombre(input);
        if (index == -1) {
            JOptionPane.showMessageDialog(null, "No encontrado.");
            return;
        }

        JOptionPane.showMessageDialog(null, "Datos actuales: " + personajes[index].toString());
        String nuevaArma = JOptionPane.showInputDialog("Nueva Arma:", personajes[index].getArma());
        if (nuevaArma != null && !nuevaArma.trim().isEmpty()) {
            personajes[index].setArma(nuevaArma);
        }
        int nuevoHp = getIntInput("Nuevo HP (100-500):", 100, 500);
        personajes[index].setHp(nuevoHp);
        JOptionPane.showMessageDialog(null, "Personaje modificado.");
    }

    private static void eliminarPersonaje() {
        String input = JOptionPane.showInputDialog("ID o Nombre:");
        if (input == null || input.trim().isEmpty()) {
            return;
        }
        int index = buscarPorIdONombre(input);
        if (index == -1) {
            JOptionPane.showMessageDialog(null, "No encontrado.");
            return;
        }

        if (JOptionPane.showConfirmDialog(null, "¿Eliminar " + personajes[index].getNombre() + "?", "Confirmar", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            for (int i = index; i < numPersonajes - 1; i++) {
                personajes[i] = personajes[i + 1];
            }
            personajes[numPersonajes - 1] = null;
            numPersonajes--;
            JOptionPane.showMessageDialog(null, "Personaje eliminado.");
        }
    }

    private static void visualizarPersonajes() {
        if (numPersonajes == 0) {
            JOptionPane.showMessageDialog(null, "No hay personajes registrados.");
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < numPersonajes; i++) {
            if (personajes[i] != null) {
                sb.append(personajes[i].toString()).append("\n");
            }
        }
        JTextArea area = new JTextArea(sb.toString());
        area.setEditable(false);
        JOptionPane.showMessageDialog(null, new JScrollPane(area));
    }

    private static void simularBatalla() {
        if (numPersonajes < 2) {
            JOptionPane.showMessageDialog(null, "Necesitas al menos 2 personajes.");
            return;
        }

        String p1Input = JOptionPane.showInputDialog("ID/Nombre Personaje 1:");
        if (p1Input == null || p1Input.trim().isEmpty()) return;
        int index1 = buscarPorIdONombre(p1Input);
        if (index1 == -1 || personajes[index1].getHp() <= 0) {
            JOptionPane.showMessageDialog(null, "Personaje 1 inválido o sin HP.");
            return;
        }

        String p2Input = JOptionPane.showInputDialog("ID/Nombre Personaje 2:");
        if (p2Input == null || p2Input.trim().isEmpty()) return;
        int index2 = buscarPorIdONombre(p2Input);
        if (index2 == -1 || index2 == index1 || personajes[index2].getHp() <= 0) {
            JOptionPane.showMessageDialog(null, "Personaje 2 inválido o igual al 1.");
            return;
        }

        Personaje p1 = new Personaje(personajes[index1].getId(), personajes[index1].getNombre(), personajes[index1].getArma(), personajes[index1].getHp(),
                                    personajes[index1].getAtaque(), personajes[index1].getVelocidad(), personajes[index1].getAgilidad(), personajes[index1].getDefensa());
        Personaje p2 = new Personaje(personajes[index2].getId(), personajes[index2].getNombre(), personajes[index2].getArma(), personajes[index2].getHp(),
                                    personajes[index2].getAtaque(), personajes[index2].getVelocidad(), personajes[index2].getAgilidad(), personajes[index2].getDefensa());

        Batalla batalla = new Batalla(numBatallas + 1, p1.getNombre(), p2.getNombre());
        historial[numBatallas++] = batalla;

        JFrame frame = new JFrame("Batalla: " + p1.getNombre() + " vs " + p2.getNombre());
        frame.setSize(400, 300);
        JTextArea logArea = new JTextArea();
        logArea.setEditable(false);
        frame.add(new JScrollPane(logArea));
        frame.setVisible(true);

        Thread t1 = new Thread(() -> {
            while (p1.getHp() > 0 && p2.getHp() > 0) {
                try { Thread.sleep(1000 / p1.getVelocidad()); } catch (InterruptedException e) {}
                if (!p2.esquivar()) {
                    int danio = p1.getAtaque();
                    p2.recibirDanio(danio);
                    String evento = p1.getNombre() + " ataca a " + p2.getNombre() + " - Daño: " + (danio - p2.getDefensa()) + " (HP: " + p2.getHp() + ")";
                    batalla.agregarEvento(evento);
                    logArea.append(evento + "\n");
                } else {
                    String evento = p1.getNombre() + " ataca a " + p2.getNombre() + " - Falló (esquiva)";
                    batalla.agregarEvento(evento);
                    logArea.append(evento + "\n");
                }
            }
        });

        Thread t2 = new Thread(() -> {
            while (p1.getHp() > 0 && p2.getHp() > 0) {
                try { Thread.sleep(1000 / p2.getVelocidad()); } catch (InterruptedException e) {}
                if (!p1.esquivar()) {
                    int danio = p2.getAtaque();
                    p1.recibirDanio(danio);
                    String evento = p2.getNombre() + " ataca a " + p1.getNombre() + " - Daño: " + (danio - p1.getDefensa()) + " (HP: " + p1.getHp() + ")";
                    batalla.agregarEvento(evento);
                    logArea.append(evento + "\n");
                } else {
                    String evento = p2.getNombre() + " ataca a " + p1.getNombre() + " - Falló (esquiva)";
                    batalla.agregarEvento(evento);
                    logArea.append(evento + "\n");
                }
            }
        });

        t1.start();
        t2.start();
        try { t1.join(); t2.join(); } catch (InterruptedException e) {}

        String ganador = p1.getHp() > 0 ? p1.getNombre() : p2.getNombre();
        batalla.setGanador(ganador);
        if (ganador.equals(p1.getNombre())) {
            personajes[index1].ganarBatalla();
            personajes[index2].perderBatalla();
        } else {
            personajes[index2].ganarBatalla();
            personajes[index1].perderBatalla();
        }
        logArea.append("Ganador: " + ganador + "\n");
    }

    private static void verHistorial() {
        if (numBatallas == 0) {
            JOptionPane.showMessageDialog(null, "No hay batallas registradas.");
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < numBatallas; i++) {
            if (historial[i] != null) {
                sb.append(historial[i].getInfo()).append("\n");
            }
        }
        JTextArea area = new JTextArea(sb.toString());
        area.setEditable(false);
        JOptionPane.showMessageDialog(null, new JScrollPane(area));
    }

    private static void buscarPersonaje() {
        String nombre = JOptionPane.showInputDialog("Nombre:");
        if (nombre == null || nombre.trim().isEmpty()) return;
        int index = buscarPorNombre(nombre);
        if (index == -1) {
            JOptionPane.showMessageDialog(null, "No encontrado.");
            return;
        }
        Personaje p = personajes[index];
        String info = p.toString() + "\nBatallas: " + (p.getBatallasGanadas() + p.getBatallasPerdidas()) +
                      "\nGanadas: " + p.getBatallasGanadas() + "\nPerdidas: " + p.getBatallasPerdidas();
        JOptionPane.showMessageDialog(null, info);
    }

    private static void guardarEstado() {
        try (PrintWriter writer = new PrintWriter("estado.txt")) {
            writer.println(numPersonajes);
            for (int i = 0; i < numPersonajes; i++) {
                if (personajes[i] != null) {
                    Personaje p = personajes[i];
                    writer.println(p.getId() + "," + p.getNombre() + "," + p.getArma() + "," + p.getHp() + "," + p.getAtaque() + "," +
                                   p.getVelocidad() + "," + p.getAgilidad() + "," + p.getDefensa() + "," +
                                   p.getBatallasGanadas() + "," + p.getBatallasPerdidas());
                }
            }
            writer.println(numBatallas);
            for (int i = 0; i < numBatallas; i++) {
                if (historial[i] != null) {
                    Batalla b = historial[i];
                    writer.println(b.getId() + "," + b.getParticipante1() + "," + b.getParticipante2() + "," + b.getGanador() + "," + b.getFecha());
                    writer.println(b.getBitacora());
                    writer.println("---");
                }
            }
            JOptionPane.showMessageDialog(null, "Guardado.");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error al guardar.");
        }
    }

    private static void cargarEstado() {
        try (Scanner scanner = new Scanner(new File("estado.txt"))) {
            numPersonajes = scanner.nextInt();
            scanner.nextLine();
            for (int i = 0; i < numPersonajes; i++) {
                String[] parts = scanner.nextLine().split(",");
                if (parts.length >= 10) {
                    int id = Integer.parseInt(parts[0]);
                    personajes[i] = new Personaje(id, parts[1], parts[2], Integer.parseInt(parts[3]),
                                                  Integer.parseInt(parts[4]), Integer.parseInt(parts[5]), Integer.parseInt(parts[6]),
                                                  Integer.parseInt(parts[7]));
                    for (int j = 0; j < Integer.parseInt(parts[8]); j++) personajes[i].ganarBatalla();
                    for (int j = 0; j < Integer.parseInt(parts[9]); j++) personajes[i].perderBatalla();
                    nextId = Math.max(nextId, id + 1);
                }
            }
            numBatallas = scanner.nextInt();
            scanner.nextLine();
            for (int i = 0; i < numBatallas; i++) {
                String[] parts = scanner.nextLine().split(",");
                if (parts.length >= 5) {
                    Batalla b = new Batalla(Integer.parseInt(parts[0]), parts[1], parts[2]);
                    b.setGanador(parts[3]);
                    StringBuilder bitacora = new StringBuilder();
                    String line;
                    while (scanner.hasNextLine() && !(line = scanner.nextLine()).equals("---")) {
                        bitacora.append(line).append("\n");
                    }
                    b.agregarEvento(bitacora.toString().trim());
                    historial[i] = b;
                }
            }
            JOptionPane.showMessageDialog(null, "Cargado.");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error al cargar (verifica que exista estado.txt).");
        }
    }

    private static void mostrarDatosEstudiante() {
        JOptionPane.showMessageDialog(null, ESTUDIANTE_INFO);
    }

    private static int getIntInput(String mensaje, int min, int max) {
        while (true) {
            try {
                String input = JOptionPane.showInputDialog(mensaje);
                if (input == null) return min;
                int valor = Integer.parseInt(input.trim());
                if (valor >= min && valor <= max) return valor;
                JOptionPane.showMessageDialog(null, "Valor fuera de rango (" + min + "-" + max + ").");
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Entrada inválida. Ingresa un número.");
            }
        }
    }

    private static int buscarPorNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) return -1;
        nombre = nombre.toLowerCase();
        for (int i = 0; i < numPersonajes; i++) {
            if (personajes[i] != null && personajes[i].getNombre().equals(nombre)) return i;
        }
        return -1;
    }

    private static int buscarPorIdONombre(String input) {
        if (input == null || input.trim().isEmpty()) return -1;
        try {
            int id = Integer.parseInt(input);
            for (int i = 0; i < numPersonajes; i++) {
                if (personajes[i] != null && personajes[i].getId() == id) return i;
            }
        } catch (NumberFormatException e) {
            return buscarPorNombre(input);
        }
        return -1;
    }
}import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.Scanner;

public class Main {
    private static Personaje[] personajes = new Personaje[50];
    private static int numPersonajes = 0;
    private static Batalla[] historial = new Batalla[50];
    private static int numBatallas = 0;
    private static int nextId = 1;

    private static final String ESTUDIANTE_INFO = "Nombre: TuNombre\nCarnet: TuCarnet\nSección: TuSección";

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::mostrarMenuPrincipal);
    }

    private static void mostrarMenuPrincipal() {
        JFrame frame = new JFrame("ArenaUSAC");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridLayout(10, 1));

        JButton[] botones = {
            new JButton("Agregar Personaje"), new JButton("Modificar Personaje"),
            new JButton("Eliminar Personaje"), new JButton("Visualizar Personajes"),
            new JButton("Simular Batalla"), new JButton("Ver Historial de Batallas"),
            new JButton("Buscar Personaje por Nombre"), new JButton("Guardar Estado"),
            new JButton("Cargar Estado"), new JButton("Ver Datos de Estudiante")
        };

        String[] acciones = {
            "agregarPersonaje", "modificarPersonaje", "eliminarPersonaje", "visualizarPersonajes",
            "simularBatalla", "verHistorial", "buscarPersonaje", "guardarEstado",
            "cargarEstado", "mostrarDatosEstudiante"
        };

        for (int i = 0; i < botones.length; i++) {
            int index = i;
            botones[i].addActionListener(e -> {
                try {
                    Main.class.getMethod(acciones[index]).invoke(null);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
                }
            });
            frame.add(botones[i]);
        }

        frame.setVisible(true);
    }

    private static void agregarPersonaje() {
        if (numPersonajes >= personajes.length) {
            JOptionPane.showMessageDialog(null, "Límite de personajes alcanzado (50).");
            return;
        }

        String nombre = JOptionPane.showInputDialog("Nombre:");
        if (nombre == null || nombre.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Nombre inválido.");
            return;
        }

        if (numPersonajes > 0 && buscarPorNombre(nombre) != -1) {
            JOptionPane.showMessageDialog(null, "Nombre duplicado.");
            return;
        }

        String arma = JOptionPane.showInputDialog("Arma:");
        if (arma == null || arma.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Arma inválida.");
            return;
        }

        int hp = getIntInput("HP (100-500):", 100, 500);
        int ataque = getIntInput("Ataque (10-100):", 10, 100);
        int velocidad = getIntInput("Velocidad (1-10):", 1, 10);
        int agilidad = getIntInput("Agilidad (1-10):", 1, 10);
        int defensa = getIntInput("Defensa (1-50):", 1, 50);

        personajes[numPersonajes] = new Personaje(nextId++, nombre, arma, hp, ataque, velocidad, agilidad, defensa);
        numPersonajes++;
        JOptionPane.showMessageDialog(null, "Personaje agregado con ID: " + (nextId - 1));
    }

    private static void modificarPersonaje() {
        String input = JOptionPane.showInputDialog("ID o Nombre:");
        if (input == null || input.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Entrada inválida.");
            return;
        }
        int index = buscarPorIdONombre(input);
        if (index == -1) {
            JOptionPane.showMessageDialog(null, "No encontrado.");
            return;
        }

        JOptionPane.showMessageDialog(null, "Datos actuales: " + personajes[index].toString());
        String nuevaArma = JOptionPane.showInputDialog("Nueva Arma:", personajes[index].getArma());
        if (nuevaArma != null && !nuevaArma.trim().isEmpty()) {
            personajes[index].setArma(nuevaArma);
        }
        int nuevoHp = getIntInput("Nuevo HP (100-500):", 100, 500);
        personajes[index].setHp(nuevoHp);
        JOptionPane.showMessageDialog(null, "Personaje modificado.");
    }

    private static void eliminarPersonaje() {
        String input = JOptionPane.showInputDialog("ID o Nombre:");
        if (input == null || input.trim().isEmpty()) {
            return;
        }
        int index = buscarPorIdONombre(input);
        if (index == -1) {
            JOptionPane.showMessageDialog(null, "No encontrado.");
            return;
        }

        if (JOptionPane.showConfirmDialog(null, "¿Eliminar " + personajes[index].getNombre() + "?", "Confirmar", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            for (int i = index; i < numPersonajes - 1; i++) {
                personajes[i] = personajes[i + 1];
            }
            personajes[numPersonajes - 1] = null;
            numPersonajes--;
            JOptionPane.showMessageDialog(null, "Personaje eliminado.");
        }
    }

    private static void visualizarPersonajes() {
        if (numPersonajes == 0) {
            JOptionPane.showMessageDialog(null, "No hay personajes registrados.");
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < numPersonajes; i++) {
            if (personajes[i] != null) {
                sb.append(personajes[i].toString()).append("\n");
            }
        }
        JTextArea area = new JTextArea(sb.toString());
        area.setEditable(false);
        JOptionPane.showMessageDialog(null, new JScrollPane(area));
    }

    private static void simularBatalla() {
        if (numPersonajes < 2) {
            JOptionPane.showMessageDialog(null, "Necesitas al menos 2 personajes.");
            return;
        }

        String p1Input = JOptionPane.showInputDialog("ID/Nombre Personaje 1:");
        if (p1Input == null || p1Input.trim().isEmpty()) return;
        int index1 = buscarPorIdONombre(p1Input);
        if (index1 == -1 || personajes[index1].getHp() <= 0) {
            JOptionPane.showMessageDialog(null, "Personaje 1 inválido o sin HP.");
            return;
        }

        String p2Input = JOptionPane.showInputDialog("ID/Nombre Personaje 2:");
        if (p2Input == null || p2Input.trim().isEmpty()) return;
        int index2 = buscarPorIdONombre(p2Input);
        if (index2 == -1 || index2 == index1 || personajes[index2].getHp() <= 0) {
            JOptionPane.showMessageDialog(null, "Personaje 2 inválido o igual al 1.");
            return;
        }

        Personaje p1 = new Personaje(personajes[index1].getId(), personajes[index1].getNombre(), personajes[index1].getArma(), personajes[index1].getHp(),
                                    personajes[index1].getAtaque(), personajes[index1].getVelocidad(), personajes[index1].getAgilidad(), personajes[index1].getDefensa());
        Personaje p2 = new Personaje(personajes[index2].getId(), personajes[index2].getNombre(), personajes[index2].getArma(), personajes[index2].getHp(),
                                    personajes[index2].getAtaque(), personajes[index2].getVelocidad(), personajes[index2].getAgilidad(), personajes[index2].getDefensa());

        Batalla batalla = new Batalla(numBatallas + 1, p1.getNombre(), p2.getNombre());
        historial[numBatallas++] = batalla;

        JFrame frame = new JFrame("Batalla: " + p1.getNombre() + " vs " + p2.getNombre());
        frame.setSize(400, 300);
        JTextArea logArea = new JTextArea();
        logArea.setEditable(false);
        frame.add(new JScrollPane(logArea));
        frame.setVisible(true);

        Thread t1 = new Thread(() -> {
            while (p1.getHp() > 0 && p2.getHp() > 0) {
                try { Thread.sleep(1000 / p1.getVelocidad()); } catch (InterruptedException e) {}
                if (!p2.esquivar()) {
                    int danio = p1.getAtaque();
                    p2.recibirDanio(danio);
                    String evento = p1.getNombre() + " ataca a " + p2.getNombre() + " - Daño: " + (danio - p2.getDefensa()) + " (HP: " + p2.getHp() + ")";
                    batalla.agregarEvento(evento);
                    logArea.append(evento + "\n");
                } else {
                    String evento = p1.getNombre() + " ataca a " + p2.getNombre() + " - Falló (esquiva)";
                    batalla.agregarEvento(evento);
                    logArea.append(evento + "\n");
                }
            }
        });

        Thread t2 = new Thread(() -> {
            while (p1.getHp() > 0 && p2.getHp() > 0) {
                try { Thread.sleep(1000 / p2.getVelocidad()); } catch (InterruptedException e) {}
                if (!p1.esquivar()) {
                    int danio = p2.getAtaque();
                    p1.recibirDanio(danio);
                    String evento = p2.getNombre() + " ataca a " + p1.getNombre() + " - Daño: " + (danio - p1.getDefensa()) + " (HP: " + p1.getHp() + ")";
                    batalla.agregarEvento(evento);
                    logArea.append(evento + "\n");
                } else {
                    String evento = p2.getNombre() + " ataca a " + p1.getNombre() + " - Falló (esquiva)";
                    batalla.agregarEvento(evento);
                    logArea.append(evento + "\n");
                }
            }
        });

        t1.start();
        t2.start();
        try { t1.join(); t2.join(); } catch (InterruptedException e) {}

        String ganador = p1.getHp() > 0 ? p1.getNombre() : p2.getNombre();
        batalla.setGanador(ganador);
        if (ganador.equals(p1.getNombre())) {
            personajes[index1].ganarBatalla();
            personajes[index2].perderBatalla();
        } else {
            personajes[index2].ganarBatalla();
            personajes[index1].perderBatalla();
        }
        logArea.append("Ganador: " + ganador + "\n");
    }

    private static void verHistorial() {
        if (numBatallas == 0) {
            JOptionPane.showMessageDialog(null, "No hay batallas registradas.");
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < numBatallas; i++) {
            if (historial[i] != null) {
                sb.append(historial[i].getInfo()).append("\n");
            }
        }
        JTextArea area = new JTextArea(sb.toString());
        area.setEditable(false);
        JOptionPane.showMessageDialog(null, new JScrollPane(area));
    }

    private static void buscarPersonaje() {
        String nombre = JOptionPane.showInputDialog("Nombre:");
        if (nombre == null || nombre.trim().isEmpty()) return;
        int index = buscarPorNombre(nombre);
        if (index == -1) {
            JOptionPane.showMessageDialog(null, "No encontrado.");
            return;
        }
        Personaje p = personajes[index];
        String info = p.toString() + "\nBatallas: " + (p.getBatallasGanadas() + p.getBatallasPerdidas()) +
                      "\nGanadas: " + p.getBatallasGanadas() + "\nPerdidas: " + p.getBatallasPerdidas();
        JOptionPane.showMessageDialog(null, info);
    }

    private static void guardarEstado() {
        try (PrintWriter writer = new PrintWriter("estado.txt")) {
            writer.println(numPersonajes);
            for (int i = 0; i < numPersonajes; i++) {
                if (personajes[i] != null) {
                    Personaje p = personajes[i];
                    writer.println(p.getId() + "," + p.getNombre() + "," + p.getArma() + "," + p.getHp() + "," + p.getAtaque() + "," +
                                   p.getVelocidad() + "," + p.getAgilidad() + "," + p.getDefensa() + "," +
                                   p.getBatallasGanadas() + "," + p.getBatallasPerdidas());
                }
            }
            writer.println(numBatallas);
            for (int i = 0; i < numBatallas; i++) {
                if (historial[i] != null) {
                    Batalla b = historial[i];
                    writer.println(b.getId() + "," + b.getParticipante1() + "," + b.getParticipante2() + "," + b.getGanador() + "," + b.getFecha());
                    writer.println(b.getBitacora());
                    writer.println("---");
                }
            }
            JOptionPane.showMessageDialog(null, "Guardado.");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error al guardar.");
        }
    }

    private static void cargarEstado() {
        try (Scanner scanner = new Scanner(new File("estado.txt"))) {
            numPersonajes = scanner.nextInt();
            scanner.nextLine();
            for (int i = 0; i < numPersonajes; i++) {
                String[] parts = scanner.nextLine().split(",");
                if (parts.length >= 10) {
                    int id = Integer.parseInt(parts[0]);
                    personajes[i] = new Personaje(id, parts[1], parts[2], Integer.parseInt(parts[3]),
                                                  Integer.parseInt(parts[4]), Integer.parseInt(parts[5]), Integer.parseInt(parts[6]),
                                                  Integer.parseInt(parts[7]));
                    for (int j = 0; j < Integer.parseInt(parts[8]); j++) personajes[i].ganarBatalla();
                    for (int j = 0; j < Integer.parseInt(parts[9]); j++) personajes[i].perderBatalla();
                    nextId = Math.max(nextId, id + 1);
                }
            }
            numBatallas = scanner.nextInt();
            scanner.nextLine();
            for (int i = 0; i < numBatallas; i++) {
                String[] parts = scanner.nextLine().split(",");
                if (parts.length >= 5) {
                    Batalla b = new Batalla(Integer.parseInt(parts[0]), parts[1], parts[2]);
                    b.setGanador(parts[3]);
                    StringBuilder bitacora = new StringBuilder();
                    String line;
                    while (scanner.hasNextLine() && !(line = scanner.nextLine()).equals("---")) {
                        bitacora.append(line).append("\n");
                    }
                    b.agregarEvento(bitacora.toString().trim());
                    historial[i] = b;
                }
            }
            JOptionPane.showMessageDialog(null, "Cargado.");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error al cargar (verifica que exista estado.txt).");
        }
    }

    private static void mostrarDatosEstudiante() {
        JOptionPane.showMessageDialog(null, ESTUDIANTE_INFO);
    }

    private static int getIntInput(String mensaje, int min, int max) {
        while (true) {
            try {
                String input = JOptionPane.showInputDialog(mensaje);
                if (input == null) return min;
                int valor = Integer.parseInt(input.trim());
                if (valor >= min && valor <= max) return valor;
                JOptionPane.showMessageDialog(null, "Valor fuera de rango (" + min + "-" + max + ").");
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Entrada inválida. Ingresa un número.");
            }
        }
    }

    private static int buscarPorNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) return -1;
        nombre = nombre.toLowerCase();
        for (int i = 0; i < numPersonajes; i++) {
            if (personajes[i] != null && personajes[i].getNombre().equals(nombre)) return i;
        }
        return -1;
    }

    private static int buscarPorIdONombre(String input) {
        if (input == null || input.trim().isEmpty()) return -1;
        try {
            int id = Integer.parseInt(input);
            for (int i = 0; i < numPersonajes; i++) {
                if (personajes[i] != null && personajes[i].getId() == id) return i;
            }
        } catch (NumberFormatException e) {
            return buscarPorNombre(input);
        }
        return -1;
    }
}import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.Scanner;

public class Main {
    private static Personaje[] personajes = new Personaje[50];
    private static int numPersonajes = 0;
    private static Batalla[] historial = new Batalla[50];
    private static int numBatallas = 0;
    private static int nextId = 1;

    private static final String ESTUDIANTE_INFO = "Nombre: TuNombre\nCarnet: TuCarnet\nSección: TuSección";

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::mostrarMenuPrincipal);
    }

    private static void mostrarMenuPrincipal() {
        JFrame frame = new JFrame("ArenaUSAC");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridLayout(10, 1));

        JButton[] botones = {
            new JButton("Agregar Personaje"), new JButton("Modificar Personaje"),
            new JButton("Eliminar Personaje"), new JButton("Visualizar Personajes"),
            new JButton("Simular Batalla"), new JButton("Ver Historial de Batallas"),
            new JButton("Buscar Personaje por Nombre"), new JButton("Guardar Estado"),
            new JButton("Cargar Estado"), new JButton("Ver Datos de Estudiante")
        };

        String[] acciones = {
            "agregarPersonaje", "modificarPersonaje", "eliminarPersonaje", "visualizarPersonajes",
            "simularBatalla", "verHistorial", "buscarPersonaje", "guardarEstado",
            "cargarEstado", "mostrarDatosEstudiante"
        };

        for (int i = 0; i < botones.length; i++) {
            int index = i;
            botones[i].addActionListener(e -> {
                try {
                    Main.class.getMethod(acciones[index]).invoke(null);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
                }
            });
            frame.add(botones[i]);
        }

        frame.setVisible(true);
    }

    private static void agregarPersonaje() {
        if (numPersonajes >= personajes.length) {
            JOptionPane.showMessageDialog(null, "Límite de personajes alcanzado (50).");
            return;
        }

        String nombre = JOptionPane.showInputDialog("Nombre:");
        if (nombre == null || nombre.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Nombre inválido.");
            return;
        }

        if (numPersonajes > 0 && buscarPorNombre(nombre) != -1) {
            JOptionPane.showMessageDialog(null, "Nombre duplicado.");
            return;
        }

        String arma = JOptionPane.showInputDialog("Arma:");
        if (arma == null || arma.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Arma inválida.");
            return;
        }

        int hp = getIntInput("HP (100-500):", 100, 500);
        int ataque = getIntInput("Ataque (10-100):", 10, 100);
        int velocidad = getIntInput("Velocidad (1-10):", 1, 10);
        int agilidad = getIntInput("Agilidad (1-10):", 1, 10);
        int defensa = getIntInput("Defensa (1-50):", 1, 50);

        personajes[numPersonajes] = new Personaje(nextId++, nombre, arma, hp, ataque, velocidad, agilidad, defensa);
        numPersonajes++;
        JOptionPane.showMessageDialog(null, "Personaje agregado con ID: " + (nextId - 1));
    }

    private static void modificarPersonaje() {
        String input = JOptionPane.showInputDialog("ID o Nombre:");
        if (input == null || input.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Entrada inválida.");
            return;
        }
        int index = buscarPorIdONombre(input);
        if (index == -1) {
            JOptionPane.showMessageDialog(null, "No encontrado.");
            return;
        }

        JOptionPane.showMessageDialog(null, "Datos actuales: " + personajes[index].toString());
        String nuevaArma = JOptionPane.showInputDialog("Nueva Arma:", personajes[index].getArma());
        if (nuevaArma != null && !nuevaArma.trim().isEmpty()) {
            personajes[index].setArma(nuevaArma);
        }
        int nuevoHp = getIntInput("Nuevo HP (100-500):", 100, 500);
        personajes[index].setHp(nuevoHp);
        JOptionPane.showMessageDialog(null, "Personaje modificado.");
    }

    private static void eliminarPersonaje() {
        String input = JOptionPane.showInputDialog("ID o Nombre:");
        if (input == null || input.trim().isEmpty()) {
            return;
        }
        int index = buscarPorIdONombre(input);
        if (index == -1) {
            JOptionPane.showMessageDialog(null, "No encontrado.");
            return;
        }

        if (JOptionPane.showConfirmDialog(null, "¿Eliminar " + personajes[index].getNombre() + "?", "Confirmar", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            for (int i = index; i < numPersonajes - 1; i++) {
                personajes[i] = personajes[i + 1];
            }
            personajes[numPersonajes - 1] = null;
            numPersonajes--;
            JOptionPane.showMessageDialog(null, "Personaje eliminado.");
        }
    }

    private static void visualizarPersonajes() {
        if (numPersonajes == 0) {
            JOptionPane.showMessageDialog(null, "No hay personajes registrados.");
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < numPersonajes; i++) {
            if (personajes[i] != null) {
                sb.append(personajes[i].toString()).append("\n");
            }
        }
        JTextArea area = new JTextArea(sb.toString());
        area.setEditable(false);
        JOptionPane.showMessageDialog(null, new JScrollPane(area));
    }

    private static void simularBatalla() {
        if (numPersonajes < 2) {
            JOptionPane.showMessageDialog(null, "Necesitas al menos 2 personajes.");
            return;
        }

        String p1Input = JOptionPane.showInputDialog("ID/Nombre Personaje 1:");
        if (p1Input == null || p1Input.trim().isEmpty()) return;
        int index1 = buscarPorIdONombre(p1Input);
        if (index1 == -1 || personajes[index1].getHp() <= 0) {
            JOptionPane.showMessageDialog(null, "Personaje 1 inválido o sin HP.");
            return;
        }

        String p2Input = JOptionPane.showInputDialog("ID/Nombre Personaje 2:");
        if (p2Input == null || p2Input.trim().isEmpty()) return;
        int index2 = buscarPorIdONombre(p2Input);
        if (index2 == -1 || index2 == index1 || personajes[index2].getHp() <= 0) {
            JOptionPane.showMessageDialog(null, "Personaje 2 inválido o igual al 1.");
            return;
        }

        Personaje p1 = new Personaje(personajes[index1].getId(), personajes[index1].getNombre(), personajes[index1].getArma(), personajes[index1].getHp(),
                                    personajes[index1].getAtaque(), personajes[index1].getVelocidad(), personajes[index1].getAgilidad(), personajes[index1].getDefensa());
        Personaje p2 = new Personaje(personajes[index2].getId(), personajes[index2].getNombre(), personajes[index2].getArma(), personajes[index2].getHp(),
                                    personajes[index2].getAtaque(), personajes[index2].getVelocidad(), personajes[index2].getAgilidad(), personajes[index2].getDefensa());

        Batalla batalla = new Batalla(numBatallas + 1, p1.getNombre(), p2.getNombre());
        historial[numBatallas++] = batalla;

        JFrame frame = new JFrame("Batalla: " + p1.getNombre() + " vs " + p2.getNombre());
        frame.setSize(400, 300);
        JTextArea logArea = new JTextArea();
        logArea.setEditable(false);
        frame.add(new JScrollPane(logArea));
        frame.setVisible(true);

        Thread t1 = new Thread(() -> {
            while (p1.getHp() > 0 && p2.getHp() > 0) {
                try { Thread.sleep(1000 / p1.getVelocidad()); } catch (InterruptedException e) {}
                if (!p2.esquivar()) {
                    int danio = p1.getAtaque();
                    p2.recibirDanio(danio);
                    String evento = p1.getNombre() + " ataca a " + p2.getNombre() + " - Daño: " + (danio - p2.getDefensa()) + " (HP: " + p2.getHp() + ")";
                    batalla.agregarEvento(evento);
                    logArea.append(evento + "\n");
                } else {
                    String evento = p1.getNombre() + " ataca a " + p2.getNombre() + " - Falló (esquiva)";
                    batalla.agregarEvento(evento);
                    logArea.append(evento + "\n");
                }
            }
        });

        Thread t2 = new Thread(() -> {
            while (p1.getHp() > 0 && p2.getHp() > 0) {
                try { Thread.sleep(1000 / p2.getVelocidad()); } catch (InterruptedException e) {}
                if (!p1.esquivar()) {
                    int danio = p2.getAtaque();
                    p1.recibirDanio(danio);
                    String evento = p2.getNombre() + " ataca a " + p1.getNombre() + " - Daño: " + (danio - p1.getDefensa()) + " (HP: " + p1.getHp() + ")";
                    batalla.agregarEvento(evento);
                    logArea.append(evento + "\n");
                } else {
                    String evento = p2.getNombre() + " ataca a " + p1.getNombre() + " - Falló (esquiva)";
                    batalla.agregarEvento(evento);
                    logArea.append(evento + "\n");
                }
            }
        });

        t1.start();
        t2.start();
        try { t1.join(); t2.join(); } catch (InterruptedException e) {}

        String ganador = p1.getHp() > 0 ? p1.getNombre() : p2.getNombre();
        batalla.setGanador(ganador);
        if (ganador.equals(p1.getNombre())) {
            personajes[index1].ganarBatalla();
            personajes[index2].perderBatalla();
        } else {
            personajes[index2].ganarBatalla();
            personajes[index1].perderBatalla();
        }
        logArea.append("Ganador: " + ganador + "\n");
    }

    private static void verHistorial() {
        if (numBatallas == 0) {
            JOptionPane.showMessageDialog(null, "No hay batallas registradas.");
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < numBatallas; i++) {
            if (historial[i] != null) {
                sb.append(historial[i].getInfo()).append("\n");
            }
        }
        JTextArea area = new JTextArea(sb.toString());
        area.setEditable(false);
        JOptionPane.showMessageDialog(null, new JScrollPane(area));
    }

    private static void buscarPersonaje() {
        String nombre = JOptionPane.showInputDialog("Nombre:");
        if (nombre == null || nombre.trim().isEmpty()) return;
        int index = buscarPorNombre(nombre);
        if (index == -1) {
            JOptionPane.showMessageDialog(null, "No encontrado.");
            return;
        }
        Personaje p = personajes[index];
        String info = p.toString() + "\nBatallas: " + (p.getBatallasGanadas() + p.getBatallasPerdidas()) +
                      "\nGanadas: " + p.getBatallasGanadas() + "\nPerdidas: " + p.getBatallasPerdidas();
        JOptionPane.showMessageDialog(null, info);
    }

    private static void guardarEstado() {
        try (PrintWriter writer = new PrintWriter("estado.txt")) {
            writer.println(numPersonajes);
            for (int i = 0; i < numPersonajes; i++) {
                if (personajes[i] != null) {
                    Personaje p = personajes[i];
                    writer.println(p.getId() + "," + p.getNombre() + "," + p.getArma() + "," + p.getHp() + "," + p.getAtaque() + "," +
                                   p.getVelocidad() + "," + p.getAgilidad() + "," + p.getDefensa() + "," +
                                   p.getBatallasGanadas() + "," + p.getBatallasPerdidas());
                }
            }
            writer.println(numBatallas);
            for (int i = 0; i < numBatallas; i++) {
                if (historial[i] != null) {
                    Batalla b = historial[i];
                    writer.println(b.getId() + "," + b.getParticipante1() + "," + b.getParticipante2() + "," + b.getGanador() + "," + b.getFecha());
                    writer.println(b.getBitacora());
                    writer.println("---");
                }
            }
            JOptionPane.showMessageDialog(null, "Guardado.");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error al guardar.");
        }
    }

    private static void cargarEstado() {
        try (Scanner scanner = new Scanner(new File("estado.txt"))) {
            numPersonajes = scanner.nextInt();
            scanner.nextLine();
            for (int i = 0; i < numPersonajes; i++) {
                String[] parts = scanner.nextLine().split(",");
                if (parts.length >= 10) {
                    int id = Integer.parseInt(parts[0]);
                    personajes[i] = new Personaje(id, parts[1], parts[2], Integer.parseInt(parts[3]),
                                                  Integer.parseInt(parts[4]), Integer.parseInt(parts[5]), Integer.parseInt(parts[6]),
                                                  Integer.parseInt(parts[7]));
                    for (int j = 0; j < Integer.parseInt(parts[8]); j++) personajes[i].ganarBatalla();
                    for (int j = 0; j < Integer.parseInt(parts[9]); j++) personajes[i].perderBatalla();
                    nextId = Math.max(nextId, id + 1);
                }
            }
            numBatallas = scanner.nextInt();
            scanner.nextLine();
            for (int i = 0; i < numBatallas; i++) {
                String[] parts = scanner.nextLine().split(",");
                if (parts.length >= 5) {
                    Batalla b = new Batalla(Integer.parseInt(parts[0]), parts[1], parts[2]);
                    b.setGanador(parts[3]);
                    StringBuilder bitacora = new StringBuilder();
                    String line;
                    while (scanner.hasNextLine() && !(line = scanner.nextLine()).equals("---")) {
                        bitacora.append(line).append("\n");
                    }
                    b.agregarEvento(bitacora.toString().trim());
                    historial[i] = b;
                }
            }
            JOptionPane.showMessageDialog(null, "Cargado.");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error al cargar (verifica que exista estado.txt).");
        }
    }

    private static void mostrarDatosEstudiante() {
        JOptionPane.showMessageDialog(null, ESTUDIANTE_INFO);
    }

    private static int getIntInput(String mensaje, int min, int max) {
        while (true) {
            try {
                String input = JOptionPane.showInputDialog(mensaje);
                if (input == null) return min;
                int valor = Integer.parseInt(input.trim());
                if (valor >= min && valor <= max) return valor;
                JOptionPane.showMessageDialog(null, "Valor fuera de rango (" + min + "-" + max + ").");
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Entrada inválida. Ingresa un número.");
            }
        }
    }

    private static int buscarPorNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) return -1;
        nombre = nombre.toLowerCase();
        for (int i = 0; i < numPersonajes; i++) {
            if (personajes[i] != null && personajes[i].getNombre().equals(nombre)) return i;
        }
        return -1;
    }

    private static int buscarPorIdONombre(String input) {
        if (input == null || input.trim().isEmpty()) return -1;
        try {
            int id = Integer.parseInt(input);
            for (int i = 0; i < numPersonajes; i++) {
                if (personajes[i] != null && personajes[i].getId() == id) return i;
            }
        } catch (NumberFormatException e) {
            return buscarPorNombre(input);
        }
        return -1;
    }
}