import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MenuPrincipal extends JFrame {
    public MenuPrincipal() {
        setTitle("Juego de Dominadas 3D - Menú Principal");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JLabel tituloLabel = new JLabel("Juego de Dominadas 3D", SwingConstants.CENTER);
        tituloLabel.setFont(new Font("Arial", Font.BOLD, 32));
        add(tituloLabel, BorderLayout.NORTH);

        JButton comenzarButton = new JButton("Comenzar Juego");
        comenzarButton.setFont(new Font("Arial", Font.BOLD, 24));
        comenzarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Dominadas3D juego = new Dominadas3D();
                juego.setVisible(true);
                dispose();
            }
        });

        JTextArea reglasTextArea = new JTextArea();
        reglasTextArea.setText("Reglas del Juego:\n\n" +
                "1. Mueve el pie con el ratón para mantener el balón en el aire.\n" +
                "2. Cada toque incrementa el contador de toques.\n" +
                "3. Si el balón toca el suelo, el juego termina.\n" +
                "4. Presiona 'P' para pausar o reanudar el juego.");
        reglasTextArea.setFont(new Font("Arial", Font.PLAIN, 18));
        reglasTextArea.setEditable(false);
        reglasTextArea.setLineWrap(true);
        reglasTextArea.setWrapStyleWord(true);

        JPanel centralPanel = new JPanel();
        centralPanel.setLayout(new BorderLayout());
        centralPanel.add(comenzarButton, BorderLayout.NORTH);
        centralPanel.add(new JScrollPane(reglasTextArea), BorderLayout.CENTER);

        add(centralPanel, BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        MenuPrincipal menu = new MenuPrincipal();
        menu.setVisible(true);
    }
}