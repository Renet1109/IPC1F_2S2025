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

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public int getHp() { return hp; }
    public int getAtaque() { return ataque; }
    public int getVelocidad() { return velocidad; }
    public int getAgilidad() { return agilidad; }
    public int getDefensa() { return defensa; }
    public int getBatallasGanadas() { return batallasGanadas; }
    public int getBatallasPerdidas() { return batallasPerdidas; }

    public void setHp(int hp) { this.hp = hp; }
    public void setArma(String arma) { this.arma = arma; }

    public boolean esquivar() {
        return Math.random() * 10 < agilidad;
    }

    public void recibirDanio(int danio) {
        int danioReducido = Math.max(0, danio - defensa);
        hp -= danioReducido;
        if (hp < 0) hp = 0;
    }

    public void ganarBatalla() { batallasGanadas++; }
    public void perderBatalla() { batallasPerdidas++; }

    @Override
    public String toString() {
        return "ID: " + id + ", Nombre: " + nombre + ", Arma: " + arma + ", HP: " + hp + ", Ataque: " + ataque +
               ", Velocidad: " + velocidad + ", Agilidad: " + agilidad + ", Defensa: " + defensa;
    }
}