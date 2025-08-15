import java.util.ArrayList;
import java.util.Scanner;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Main {
    private static ArrayList<String> historialPeleas = new ArrayList<>();
    private static int contadorID = 1;
    private static Scanner sc = new Scanner(System.in);
    private static ArrayList<String[]> personajes = new ArrayList<>();
    public static void main(String[] args) {
        int opc;
        boolean exit = false;
while(!exit){
        System.out.println("");
        System.out.println("");
        System.out.println("---Bienvenido a WWJD Game---");
        System.out.println("---Menu Principal---");
        System.out.println("---Seleccione una de las siguientes opciones---");
        System.out.println("1. Agregar Personaje");
        System.out.println("2. Modificar Personaje");
        System.out.println("3. Eliminar Personaje");
        System.out.println("4. Datos del personaje");
        System.out.println("5. Ver listado de personajes");
        System.out.println("6. Combate entre personajes");
        System.out.println("7. Historial de peleas");
        System.out.println("8. Datos del estudiannte");
        System.out.println("9. Salir del menu");
        opc = sc.nextInt();
        sc.nextLine();
        switch  (opc) {
            case 1:
                System.out.println("");
           pers();
           break;
           case 2:
               System.out.println("");
               modif();
               break;
               case 3:
                   System.out.println("");
                   eliminar();
                   break;
            case 4:
                System.out.println("");
                dtspersonaje();
                break;
            case 5:
                System.out.println("");
                listpersonaje(personajes);
                break;
            case 6:
                System.out.println("");
                peleas();
                break;
            case 7:
                System.out.println("");
                historial();
                break;
            case 8:
                System.out.println("");
                estudiante();
            case 9:
                exit = true;

                break;
        }
}
//nombre, arma, habilidades y nivel de poder, y registrar peleas entre los personajes con la
        //fecha y hora correspondiente.
        }
    static void pers() {
        if (personajes.size() >= 25) {
            System.out.println("Haz llegado al numero maximo de personajes.");
        }
        String arma;
        int nivelPoder;
        System.out.println("Agregando personaje...");
        //nombre
        System.out.print("Ingrese el nombre: ");
        String nombre = sc.nextLine().trim();
        for(String[] p: personajes){
            if(p[1] !=null && p[1].equalsIgnoreCase(nombre)){
                System.out.print("Este nombre ya existe");
                return;
            }
        }
        System.out.print("Ingrese la arma: ");
        arma = sc.nextLine();
        do {
            System.out.print("Ingrese el nivel de poder (1 - 100): ");
            nivelPoder = sc.nextInt();
            System.out.print("");
            if (nivelPoder < 1 || nivelPoder > 100) {
                System.out.println("El nivel del poder no es valido");
            }
        } while (nivelPoder < 1 || nivelPoder > 100);
        ArrayList<String> habilidades = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            System.out.print("Ingrese habilidad #" + (i + 1) + " (o escriba 'Sin habilidades' para terminar): ");
            String habilidadinput = sc.nextLine();
            if (habilidadinput.equalsIgnoreCase("Sin habilidades")) {
                break;
            }
            habilidades.add(habilidadinput);
        }
        String habilidadestexto = String.join(", ", habilidades);
        String[] personaje = new String[5];
        personaje[0] = String.valueOf(contadorID);
        personaje[1] = nombre;
        personaje[2] = arma;
        personaje[3] = habilidadestexto;
        personaje[4] = String.valueOf(nivelPoder);
        personajes.add(personaje);
        System.out.println("Personaje guardado con ID: " + contadorID);
        System.out.println("Total de personajes: " + personajes.size() + "/25");
        contadorID++; //
    }

    static void modif() {
        String nuevaarma, habilidad, respuesta, idb;
        int nuevoPoder;
        System.out.println("Modificando personaje...");
        System.out.print("Ingrese el ID del personaje: ");
         idb = sc.nextLine().trim();
        String[] personaje = null;
        for (String[] p : personajes) {
            if (p[0].equals(idb)) {
                personaje = p;
                break;
            }
        }
        if (personaje == null) {
            System.out.println("No existe un personaje con ese ID ");
            return;
        }
        // muestro los datos del personaje
        System.out.println("Datos del personaje:");
        System.out.println("ID: " + personaje[0]);
        System.out.println("Nombre: " + personaje[1]);
        System.out.println("Arma: " + personaje[2]);
        System.out.println("Nivel de poder: " + personaje[4]);
        System.out.println("Habilidades: " + personaje[3]);
        System.out.print("Nueva arma (Enter para no cambiar): ");
        nuevaarma = sc.nextLine().trim();
        if (!nuevaarma.isEmpty()) {
            personaje[2] = nuevaarma;
        }
        // Modificar nivel de poder
        System.out.print("⚡ Nuevo nivel de poder (1–100, 0 para no cambiar): ");
        nuevoPoder = sc.nextInt();
        sc.nextLine();
        if (nuevoPoder >= 1 && nuevoPoder <= 100) {
            personaje[4] = String.valueOf(nuevoPoder);
        }
        // Modificar habilidades
        System.out.print("¿Desea modificar habilidades? (s/n): ");
        respuesta = sc.nextLine().trim();
        if (respuesta.equalsIgnoreCase("s")) {
            ArrayList<String> nuevasHabilidades = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                System.out.print("Ingrese habilidad #" + (i + 1) + " (o escriba 'Sin habilidades' para terminar): ");
                habilidad = sc.nextLine().trim();
                if (habilidad.equalsIgnoreCase("Sin habilidades")) break;
                nuevasHabilidades.add(habilidad);
            }
            personaje[3] = String.join(", ", nuevasHabilidades);
        }
        System.out.println("Personaje modificado con ID: " + idb);
    }
    static void eliminar() {
        System.out.println("Eliminar personaje...");
        System.out.print("Ingrese el ID del personaje a eliminar: ");
        String idEliminar = sc.nextLine().trim();
        boolean eliminado = false;
        for (int i = 0; i < personajes.size(); i++) {
            String[] personaje = personajes.get(i);
            if (personaje[0].equals(idEliminar)) {
                personajes.remove(i);
                System.out.println("Personaje con ID " + idEliminar + " eliminado exitosamente.");
                eliminado = true;
                break;
            }
        }
        if (!eliminado) {
            System.out.println("No se encontró un personaje con ese ID.");
        }
    }
    static void dtspersonaje() {
        System.out.println("Consultar datos de personaje....");
        System.out.print("Ingrese el ID del personaje a consultar: ");
        String idConsulta = sc.nextLine().trim();
        boolean encontrado = false;
        for (String[] personaje : personajes) {
            if (personaje[0].equals(idConsulta)) {
                System.out.println("Personaje encontrado:");
                System.out.println("ID: " + personaje[0]);
                System.out.println("Nombre: " + personaje[1]);
                System.out.println("Arma: " + personaje[2]);
                System.out.println("Nivel de poder: " + personaje[4]);
                System.out.println("Habilidades: " + personaje[3]);
                encontrado = true;
                break;
            }
        }
        if (!encontrado) {
            System.out.println("No hay personaje con ese ID ");
        }
    }
    static void listpersonaje(ArrayList<String[]> personajes) {
        System.out.println("Mostrando base de datos de personajes...");
        for (String[] p : personajes) {
            String id = p[0];
            String nombre = p[1];
            String poder = p[4];
            System.out.println("Id de personaje  " + id);
            System.out.println("Nombre de personaje  "+ nombre);
            System.out.println("Poder del personaje  "+ poder );
        }
    }
    static void peleas() {

        String id1, id2;
        System.out.println("Personajes por pelear");

        System.out.print("Ingrese el ID del primer personaje: ");
         id1 = sc.nextLine().trim();

        // Solicitar ID del segundo personaje
        System.out.print("Ingrese el ID del segundo personaje: ");
         id2 = sc.nextLine().trim();
        // Buscar ambos personajes
        String[] personaje1 = null;
        String[] personaje2 = null;

        for (String[] p : personajes) {
            if (p[0].equals(id1)) {
                personaje1 = p;
            }
            if (p[0].equals(id2)) {
                personaje2 = p;
            }
        }
        if (personaje1 == null || personaje2 == null) {
            System.out.println("Uno o ambos personajes no existen.");
            return;
        }

        System.out.println("Personaje 1:");
        System.out.println("Nombre: " + personaje1[1]);
        System.out.println("Poder: " + personaje1[4]);
        System.out.println("Personaje 2:");
        System.out.println("Nombre: " + personaje2[1]);
        System.out.println("Poder: " + personaje2[4]);
        // Registrar fecha y hora
        LocalDateTime ahora = LocalDateTime.now();
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        String fechaHora = ahora.format(formato);
        System.out.println("La pelea se llevó a cabo el: " + fechaHora);
        int poder1 = Integer.parseInt(personaje1[4]);
        int poder2 = Integer.parseInt(personaje2[4]);
        if (poder1 > poder2) {
            System.out.println("  Ganador: " + personaje1[1]);
        } else if (poder2 > poder1) {
            System.out.println(" Ganador: " + personaje2[1]);
        } else {
            System.out.println("¡Empate!");
        }
        String resultado;
        if (poder1 > poder2) {
            resultado = "Ganador: " + personaje1[1];
        } else if (poder2 > poder1) {
            resultado = "Ganador: " + personaje2[1];
        } else {
            resultado = "Empate";
        }
        String registro = fechaHora + " | " +
                personaje1[1] + " (ID " + personaje1[0] + ") vs " +
                personaje2[1] + " (ID " + personaje2[0] + ") → " + resultado;
        historialPeleas.add(registro);
    }
    static void historial() {
        System.out.println("Historial de peleas:");
        if (historialPeleas.isEmpty()) {
            System.out.println("No se han registrado peleas aún.");
            return;
        }
        for (String pelea : historialPeleas) {
            System.out.println(pelea);
        }
    }
    static void estudiante() {
        System.out.println("Soy William René Toledo Corado");
        System.out.println("Mi Carné es: 202210198");
        System.out.println("Este es el primer programa que hago en Java");
        System.out.println("Use un switch para seleccionar los subprocesos");
        System.out.println("Use un while para poder repetir los ciclos");

    }

}