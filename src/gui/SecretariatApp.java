package gui;

import javax.swing.SwingUtilities;

/**
 * Main application class to start the Secretariat application
 */
public class SecretariatApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            StudentSecretariatGUI gui = new StudentSecretariatGUI();
            gui.setVisible(true);
        });
    }
}
