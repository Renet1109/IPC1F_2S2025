public class Personaje {
    private int id;
    private String nombre;
    private String arma;
    private int hp;
    private int ataque;
    private int velocidad;
    private int agilidad;
    private int defensa;
    private int batallasGanadas;
    private int batallasPerdidas;

    public Personaje(int id, String nombre, String arma, int hp, int ataque, int velocidad, int agilidad, int defensa) {
        this.id = id;
        this.nombre = nombre.toLowerCase(); // Normalizar para evitar duplicados case-insensitive
        this.arma = arma;
        this.hp = hp;
        this.ataque = ataque;
        this.velocidad = velocidad;
        this.agilidad = agilidad;
        this.defensa = defensa;
        this.batallasGanadas = 0;
        this.batallasPerdidas = 0;
    }

    // Getters y setters
    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public int getHp() { return hp; }
    public void setHp(int hp) { this.hp = hp; }
    public int getAtaque() { return ataque; }
    public int getVelocidad() { return velocidad; }
    public int getAgilidad() { return agilidad; }
    public int getDefensa() { return defensa; }
    public void setArma(String arma) { this.arma = arma; }
    public void ganarBatalla() { batallasGanadas++; }
    public void perderBatalla() { batallasPerdidas++; }
    public int getBatallasGanadas() { return batallasGanadas; }
    public int getBatallasPerdidas() { return batallasPerdidas; }

    // Método para esquivar
    public boolean esquivar() {
        return Math.random() * 10 < agilidad; // Probabilidad basada en agilidad
    }

    // Recibir daño
    public void recibirDanio(int danio) {
        int danioReducido = Math.max(0, danio - defensa); // No negativo
        hp -= danioReducido;
        if (hp < 0) hp = 0;
    }

    @Override
    public String toString() {
        return "ID: " + id + ", Nombre: " + nombre + ", Arma: " + arma + ", HP: " + hp + ", Ataque: " + ataque +
               ", Velocidad: " + velocidad + ", Agilidad: " + agilidad + ", Defensa: " + defensa;
    }

    Object getArma() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    void ganarBatalla(int parseInt) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    void perderBatalla(int parseInt) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}