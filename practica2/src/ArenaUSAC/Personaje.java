// Clase que representa a un personaje (Pokémon) con atributos según el enunciado
public class Personaje {
    private int id; // ID único
    private String nombre; // Nombre normalizado a minúsculas
    private String arma; // Arma o tipo de ataque
    private int hp; // Puntos de vida (100-500)
    private int ataque; // Ataque (10-100)
    private int velocidad; // Velocidad (1-10)
    private int agilidad; // Agilidad (1-10)
    private int defensa; // Defensa (1-50)
    private int batallasGanadas; // Contador de victorias
    private int batallasPerdidas; // Contador de derrotas

    // Constructor para inicializar atributos
    public Personaje(int id, String nombre, String arma, int hp, int ataque, int velocidad, int agilidad, int defensa) {
        this.id = id;
        this.nombre = nombre.toLowerCase();
        this.arma = arma;
        this.hp = hp;
        this.ataque = ataque;
        this.velocidad = velocidad;
        this.agilidad = agilidad;
        this.defensa = defensa;
        this.batallasGanadas = 0;
        this.batallasPerdidas = 0;
    }

    // Getters para acceder a atributos
    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public int getHp() { return hp; }
    public int getAtaque() { return ataque; }
    public int getVelocidad() { return velocidad; }
    public int getAgilidad() { return agilidad; }
    public int getDefensa() { return defensa; }
    public int getBatallasGanadas() { return batallasGanadas; }
    public int getBatallasPerdidas() { return batallasPerdidas; }

    // Setters para modificación
    public void setHp(int hp) { this.hp = hp; }
    public void setArma(String arma) { this.arma = arma; }

    // Método para esquivar ataque basado en agilidad
    public boolean esquivar() {
        return Math.random() * 10 < agilidad;
    }

    // Método para recibir daño, reduciendo por defensa
    public void recibirDanio(int danio) {
        int danioReducido = Math.max(0, danio - defensa);
        hp -= danioReducido;
        if (hp < 0) hp = 0;
    }

    // Actualizar estadísticas de batallas
    public void ganarBatalla() { batallasGanadas++; }
    public void perderBatalla() { batallasPerdidas++; }

    // Método para mostrar información del personaje
    @Override
    public String toString() {
        return "ID: " + id + ", Nombre: " + nombre + ", Arma: " + arma + ", HP: " + hp +
               ", Ataque: " + ataque + ", Velocidad: " + velocidad + ", Agilidad: " + agilidad +
               ", Defensa: " + defensa;
    }

    Object getArma() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    Object getArma() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    String getArma() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    String getArma() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}