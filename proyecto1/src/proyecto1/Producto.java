/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyecto1;

/**
 *
 * @author willi
 */
public class Producto {
String nombre;
    String categoria;
    double precio;
    int cantidad;
    String codigo;
    boolean activo; // Para marcar si está eliminado o no, sin remover del array

    public Producto() {
        this.activo = true;
    }   
}
