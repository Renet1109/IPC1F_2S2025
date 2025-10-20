

package proyecto2;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

//import java.util.Scanner();

public class Proyecto2 {
private JFrame loginframe;
 private JTextField usernameField;
  private JPasswordField passwordField;
  
  public Proyecto2(){
    JFrame loginFrame = new JFrame("Login");
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginFrame.setSize(300, 200);
        loginFrame.setLocationRelativeTo(null);
        loginFrame.setLayout(new GridLayout(3, 2, 10, 10));

        // Componentes de la interfaz
        JLabel usernameLabel = new JLabel("Usuario:");
        usernameField = new JTextField();
        JLabel passwordLabel = new JLabel("Contraseña:");
        passwordField = new JPasswordField();
        JButton loginButton = new JButton("Iniciar Sesión");

        // Agregar componentes a la ventana
        loginFrame.add(usernameLabel);
        loginFrame.add(usernameField);
        loginFrame.add(passwordLabel);
        loginFrame.add(passwordField);
        loginFrame.add(new JLabel()); // Espacio vacío
        loginFrame.add(loginButton);

        // Acción del botón de login
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                // Validación simple (para demostración)
                if (username.equals("admin") && password.equals("admin123*")) {
                    // Cerrar ventana de login
                    loginFrame.dispose();
                    // Abrir ventana principal
                    showMainWindow();
                  
                } 
              
                    
                      
                
                
                
                
                else {
                    JOptionPane.showMessageDialog(loginFrame, 
                        "Usuario o contraseña incorrectos", 
                        "Error de Login", 
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        loginFrame.setVisible(true);
    }

    private void showMainWindow() {
        // Configuración de la ventana principal
        JFrame mainFrame = new JFrame("Ventana Principal");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(400, 300);
        mainFrame.setLocationRelativeTo(null);
        
        // Contenido de la ventana principal
        JLabel welcomeLabel = new JLabel("Bienvenido señor administrador", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        mainFrame.add(welcomeLabel);

        mainFrame.setVisible(true);
    }
    /*private void showvendedorwindow() {
        // Configuración de la ventana principal
        JFrame mainFrame = new JFrame("Ventana Principal");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(400, 300);
        mainFrame.setLocationRelativeTo(null);
        
        // Contenido de la ventana principal
        JLabel welcomeLabel = new JLabel("¡Bienvenido vendedor !", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        mainFrame.add(welcomeLabel);

        mainFrame.setVisible(true);
    }
    private void showclientewindow() {
        // Configuración de la ventana principal
        JFrame mainFrame = new JFrame("Ventana Principal");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(400, 300);
        mainFrame.setLocationRelativeTo(null);
        
        // Contenido de la ventana principal
        JLabel welcomeLabel = new JLabel("Bienvenido gracias por escogernos para hacer sus compras", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        mainFrame.add(welcomeLabel);

        mainFrame.setVisible(true);
    }*/

    public static void main(String[] args) {
        // Ejecutar en el hilo de despacho de eventos
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Proyecto2();
            }
        });
    }
}