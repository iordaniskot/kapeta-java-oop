import gui.StudentSecretariatGUI;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {
    public static void main(String[] args) {
        // Set the system look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Launch the GUI using SwingUtilities.invokeLater to ensure
        // it's created on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            StudentSecretariatGUI gui = new StudentSecretariatGUI();
            

            // Display the GUI
            gui.setVisible(true);
        });
    }
}