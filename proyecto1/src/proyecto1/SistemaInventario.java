/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package proyecto1;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
/**
 *
 * @author willi
 */

public class SistemaInventario {
    private static final int MAX_PRODUCTOS = 100; // Tamaño fijo del array
    private static final int MAX_VENTAS = 1000; // Tamaño fijo para ventas
    private static final int MAX_BITACORA = 1000; // Tamaño fijo para bitácora
    private static Producto[] inventario = new Producto[MAX_PRODUCTOS];
    private static Venta[] ventas = new Venta[MAX_VENTAS];
    private static BitacoraEntry[] bitacora = new BitacoraEntry[MAX_BITACORA];
    private static int numProductos = 0;
    private static int numVentas = 0;
    private static int numBitacora = 0;
    private static final String ARCHIVO_INVENTARIO = "INVENTARIO.txt";
    private static final String ARCHIVO_VENTAS = "ventas.txt";
    private static final String PERSONA = "Usuario"; // Fijo, o puedes pedir input
    private static final String NOMBRE_ESTUDIANTE = "Tu Nombre";
    private static final String ID_ESTUDIANTE = "Tu ID";

    public static void main(String[] args) {
        cargarInventario();
        cargarVentas();
        Scanner scanner = new Scanner(System.in);
        int opcion;

        do {
            System.out.println("\nMenú de Gestión de productos");
            System.out.println("1. Agregar Producto");
            System.out.println("2. Buscar Producto");
            System.out.println("3. Eliminar Producto");
            System.out.println("4. Registrar Venta");
            System.out.println("5. Generar Reportes");
            System.out.println("6. Ver Datos del Estudiante");
            System.out.println("7. Bitácora");
            System.out.println("8. Salir");
            System.out.print("Seleccione una opción: ");
            opcion = scanner.nextInt();
            scanner.nextLine(); // Limpiar buffer

            switch (opcion) {
                case 1:
                    agregarProducto(scanner);
                    break;
                case 2:
                    buscarProducto(scanner);
                    break;
                case 3:
                    eliminarProducto(scanner);
                    break;
                case 4:
                    registrarVenta(scanner);
                    break;
                case 5:
                    generarReportes(scanner);
                    break;
                case 6:
                    verDatosEstudiante();
                    break;
                case 7:
                    mostrarBitacora();
                    break;
                case 8:
                    guardarInventario();
                    guardarVentas();
                    System.out.println("Saliendo del sistema...");
                    break;
                default:
                    System.out.println("Opción inválida.");
                    registrarBitacora("Menú", false);
            }
        } while (opcion != 8);

        scanner.close();
    }

    private static void agregarProducto(Scanner scanner) {
        if (numProductos >= MAX_PRODUCTOS) {
            System.out.println("Inventario lleno.");
            registrarBitacora("Agregar Producto", false);
            return;
        }

        Producto producto = new Producto();

        System.out.print("Nombre del producto: ");
        producto.nombre = scanner.nextLine();

        System.out.print("Categoría: ");
        producto.categoria = scanner.nextLine();

        System.out.print("Precio: ");
        producto.precio = scanner.nextDouble();
        if (producto.precio <= 0) {
            System.out.println("Precio debe ser positivo.");
            registrarBitacora("Agregar Producto", false);
            return;
        }

        System.out.print("Cantidad en stock: ");
        producto.cantidad = scanner.nextInt();
        scanner.nextLine(); // Limpiar buffer
        if (producto.cantidad <= 0) {
            System.out.println("Cantidad debe ser positiva.");
            registrarBitacora("Agregar Producto", false);
            return;
        }

        System.out.print("Código único: ");
        producto.codigo = scanner.nextLine();

        // Validar código único
        for (int i = 0; i < numProductos; i++) {
            if (inventario[i].activo && inventario[i].codigo.equals(producto.codigo)) {
                System.out.println("Código ya existe.");
                registrarBitacora("Agregar Producto", false);
                return;
            }
        }

        inventario[numProductos] = producto;
        numProductos++;
        System.out.println("Producto agregado.");
        registrarBitacora("Agregar Producto", true);
    }

    private static void buscarProducto(Scanner scanner) {
        System.out.println("Criterio de búsqueda (1: Nombre, 2: Categoría, 3: Código): ");
        int criterio = scanner.nextInt();
        scanner.nextLine();
        String busqueda = "";

        switch (criterio) {
            case 1:
                System.out.print("Nombre: ");
                busqueda = scanner.nextLine();
                break;
            case 2:
                System.out.print("Categoría: ");
                busqueda = scanner.nextLine();
                break;
            case 3:
                System.out.print("Código: ");
                busqueda = scanner.nextLine();
                break;
            default:
                System.out.println("Criterio inválido.");
                registrarBitacora("Buscar Producto", false);
                return;
        }

        boolean encontrado = false;
        for (int i = 0; i < numProductos; i++) {
            if (inventario[i].activo) {
                boolean match = false;
                if (criterio == 1 && inventario[i].nombre.contains(busqueda)) match = true;
                if (criterio == 2 && inventario[i].categoria.contains(busqueda)) match = true;
                if (criterio == 3 && inventario[i].codigo.equals(busqueda)) match = true;

                if (match) {
                    System.out.println("Nombre: " + inventario[i].nombre + ", Categoría: " + inventario[i].categoria +
                            ", Precio: " + inventario[i].precio + ", Cantidad: " + inventario[i].cantidad +
                            ", Código: " + inventario[i].codigo);
                    encontrado = true;
                }
            }
        }

        if (!encontrado) {
            System.out.println("No se encontró ningún producto.");
            registrarBitacora("Buscar Producto", false);
        } else {
            registrarBitacora("Buscar Producto", true);
        }
    }

    private static void eliminarProducto(Scanner scanner) {
        System.out.print("Código del producto a eliminar: ");
        String codigo = scanner.nextLine();

        boolean encontrado = false;
        for (int i = 0; i < numProductos; i++) {
            if (inventario[i].activo && inventario[i].codigo.equals(codigo)) {
                System.out.print("Confirmar eliminación (s/n): ");
                String confirm = scanner.nextLine();
                if (confirm.equalsIgnoreCase("s")) {
                    inventario[i].activo = false;
                    System.out.println("Producto eliminado.");
                    registrarBitacora("Eliminar Producto", true);
                } else {
                    registrarBitacora("Eliminar Producto", false);
                }
                encontrado = true;
                break;
            }
        }

        if (!encontrado) {
            System.out.println("Producto no encontrado.");
            registrarBitacora("Eliminar Producto", false);
        }
    }

    private static void registrarVenta(Scanner scanner) {
        if (numVentas >= MAX_VENTAS) {
            System.out.println("Registro de ventas lleno.");
            registrarBitacora("Registrar Venta", false);
            return;
        }

        Venta venta = new Venta();

        System.out.print("Código del producto: ");
        venta.codigoProducto = scanner.nextLine();

        Producto producto = null;
        for (int i = 0; i < numProductos; i++) {
            if (inventario[i].activo && inventario[i].codigo.equals(venta.codigoProducto)) {
                producto = inventario[i];
                break;
            }
        }

        if (producto == null) {
            System.out.println("Producto no encontrado.");
            registrarBitacora("Registrar Venta", false);
            return;
        }

        System.out.print("Cantidad vendida: ");
        venta.cantidadVendida = scanner.nextInt();
        scanner.nextLine();

        if (venta.cantidadVendida <= 0 || venta.cantidadVendida > producto.cantidad) {
            System.out.println("Cantidad inválida o stock insuficiente.");
            registrarBitacora("Registrar Venta", false);
            return;
        }

        producto.cantidad -= venta.cantidadVendida;
        venta.total = venta.cantidadVendida * producto.precio;
        venta.fechaHora = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date());

        ventas[numVentas] = venta;
        numVentas++;
        System.out.println("Venta registrada. Total: " + venta.total);
        registrarBitacora("Registrar Venta", true);
    }

    private static void generarReportes(Scanner scanner) {
        System.out.println("1. Reporte de Stock");
        System.out.println("2. Reporte de Ventas");
        int tipo = scanner.nextInt();
        scanner.nextLine();

        String fechaHora = new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss").format(new Date());
        String archivo = "";

        if (tipo == 1) {
            archivo = fechaHora + "_Stock.pdf";
            generarPDFStock(archivo);
            registrarBitacora("Generar Reporte Stock", true);
        } else if (tipo == 2) {
            archivo = fechaHora + "_Venta.pdf";
            generarPDFVentas(archivo);
            registrarBitacora("Generar Reporte Ventas", true);
        } else {
            System.out.println("Opción inválida.");
            registrarBitacora("Generar Reportes", false);
        }
    }

    private static void generarPDFStock(String archivo) {
        Document document = new Document();
        try {
            PdfWriter.getInstance(document, new java.io.FileOutputStream(archivo));
            document.open();
            document.add(new Paragraph("Reporte de Stock"));
            for (int i = 0; i < numProductos; i++) {
                if (inventario[i].activo) {
                    document.add(new Paragraph("Nombre: " + inventario[i].nombre + ", Código: " + inventario[i].codigo +
                            ", Categoría: " + inventario[i].categoria + ", Precio: " + inventario[i].precio +
                            ", Cantidad: " + inventario[i].cantidad));
                }
            }
            document.close();
            System.out.println("Reporte generado: " + archivo);
        } catch (DocumentException | IOException e) {
            System.out.println("Error al generar PDF: " + e.getMessage());
            registrarBitacora("Generar PDF Stock", false);
        }
    }

    private static void generarPDFVentas(String archivo) {
        Document document = new Document();
        try {
            PdfWriter.getInstance(document, new java.io.FileOutputStream(archivo));
            document.open();
            document.add(new Paragraph("Reporte de Ventas"));
            for (int i = 0; i < numVentas; i++) {
                document.add(new Paragraph("Código: " + ventas[i].codigoProducto + ", Cantidad: " + ventas[i].cantidadVendida +
                        ", Total: " + ventas[i].total + ", Fecha: " + ventas[i].fechaHora));
            }
            document.close();
            System.out.println("Reporte generado: " + archivo);
        } catch (DocumentException | IOException e) {
            System.out.println("Error al generar PDF: " + e.getMessage());
            registrarBitacora("Generar PDF Ventas", false);
        }
    }

    private static void verDatosEstudiante() {
        System.out.println("Nombre: " + NOMBRE_ESTUDIANTE);
        System.out.println("ID: " + ID_ESTUDIANTE);
        registrarBitacora("Ver Datos Estudiante", true);
    }

    private static void registrarBitacora(String tipoAccion, boolean exitosa) {
        if (numBitacora >= MAX_BITACORA) {
            return; // Silencioso, o loggear error
        }

        BitacoraEntry entry = new BitacoraEntry();
        entry.fechaHora = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date());
        entry.tipoAccion = tipoAccion;
        entry.exitosa = exitosa;
        entry.persona = PERSONA;

        bitacora[numBitacora] = entry;
        numBitacora++;
    }

    private static void mostrarBitacora() {
        System.out.println("Bitácora:");
        for (int i = 0; i < numBitacora; i++) {
            System.out.println("Fecha: " + bitacora[i].fechaHora + ", Acción: " + bitacora[i].tipoAccion +
                    ", Exitosa: " + (bitacora[i].exitosa ? "Sí" : "No") + ", Persona: " + bitacora[i].persona);
        }
        registrarBitacora("Mostrar Bitácora", true);
    }

    private static void cargarInventario() {
        File file = new File(ARCHIVO_INVENTARIO);
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null && numProductos < MAX_PRODUCTOS) {
                String[] parts = line.split(",");
                if (parts.length == 5) {
                    Producto p = new Producto();
                    p.nombre = parts[0];
                    p.categoria = parts[1];
                    p.precio = Double.parseDouble(parts[2]);
                    p.cantidad = Integer.parseInt(parts[3]);
                    p.codigo = parts[4];
                    inventario[numProductos] = p;
                    numProductos++;
                }
            }
        } catch (IOException e) {
            System.out.println("Error al cargar inventario: " + e.getMessage());
        }
    }

    private static void guardarInventario() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ARCHIVO_INVENTARIO))) {
            for (int i = 0; i < numProductos; i++) {
                if (inventario[i].activo) {
                    bw.write(inventario[i].nombre + "," + inventario[i].categoria + "," + inventario[i].precio + "," +
                            inventario[i].cantidad + "," + inventario[i].codigo);
                    bw.newLine();
                }
            }
        } catch (IOException e) {
            System.out.println("Error al guardar inventario: " + e.getMessage());
        }
    }

    private static void cargarVentas() {
        File file = new File(ARCHIVO_VENTAS);
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null && numVentas < MAX_VENTAS) {
                String[] parts = line.split(",");
                if (parts.length == 4) {
                    Venta v = new Venta();
                    v.codigoProducto = parts[0];
                    v.cantidadVendida = Integer.parseInt(parts[1]);
                    v.fechaHora = parts[2];
                    v.total = Double.parseDouble(parts[3]);
                    ventas[numVentas] = v;
                    numVentas++;
                }
            }
        } catch (IOException e) {
            System.out.println("Error al cargar ventas: " + e.getMessage());
        }
    }

    private static void guardarVentas() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ARCHIVO_VENTAS))) {
            for (int i = 0; i < numVentas; i++) {
                bw.write(ventas[i].codigoProducto + "," + ventas[i].cantidadVendida + "," +
                        ventas[i].fechaHora + "," + ventas[i].total);
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error al guardar ventas: " + e.getMessage());
        }
    }
    }

