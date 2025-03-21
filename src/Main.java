import gui.StudentSecretariatGUI;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Main application entry point for the Student Secretariat system.
 * This class initializes and launches the GUI application.
 * 
 * The Student Secretariat system is an educational application for managing student records
 * in a university setting. It allows for adding, editing, searching, and managing students,
 * as well as importing and exporting student data.
 */
public class Main {
    /**
     * The main method that serves as the entry point for the application.
     * It sets up the look and feel and launches the GUI on the Event Dispatch Thread.
     * 
     * @param args Command line arguments (not used in this application)
     */
    public static void main(String[] args) {
        // Set the system look and feel to match the native OS appearance
        try {
            // The system look and feel makes the application blend in with other
            // native applications on the user's operating system
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Print stack trace but continue with default look and feel if there's an error
            e.printStackTrace();
        }
        
        // Launch the GUI using SwingUtilities.invokeLater to ensure
        // it's created on the Event Dispatch Thread
        // This is a best practice for Swing applications to avoid threading issues
        SwingUtilities.invokeLater(() -> {
            // Create a new instance of the main application GUI
            StudentSecretariatGUI gui = new StudentSecretariatGUI();
            
            // Display the GUI to the user
            // This makes the JFrame visible on screen
            gui.setVisible(true);
        });
    }
}