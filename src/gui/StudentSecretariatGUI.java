package gui;

import backend.CStudent;
import backend.Helper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Main GUI class for the Student Secretariat Application
 */
public class StudentSecretariatGUI extends JFrame {
    private List<CStudent> students;
    private CStudent currentStudent;
    private int currentStudentIndex = -1;
    
    // GUI Components
    private JTable studentTable;
    private DefaultTableModel tableModel;
    private JPanel detailsPanel;
    
    // Text fields for student details
    private JTextField idField;
    private JTextField nameField;
    private JTextField surnameField;
    private JTextField countryField;
    private JTextField dateOfBirthField;
    private JCheckBox studyAbroadCheckBox;
    private JTextField gpaField;
    private JTextField majorField;
    private JTextField enrollmentDateField;
    private JTextField emailField;
    private JTextField phoneField;
    
    // Search components
    private JTextField searchField;
    private JComboBox<String> searchCriteriaComboBox;
    
    // Status display
    private JLabel statusLabel;
    
    /**
     * Constructor - creates the GUI and initializes data
     */
    public StudentSecretariatGUI() {
        students = new ArrayList<>();
        
        // Set up the JFrame
        setTitle("Student Secretariat Application");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        
        // Create the menu bar
        createMenuBar();
        
        // Create the student list panel (left side)
        createStudentListPanel();
        
        // Create the details panel (right side)
        createDetailsPanel();
        
        // Create the search panel (top)
        createSearchPanel();
        
        // Create the button panel (bottom)
        createButtonPanel();
        
        // Status bar at the bottom
        statusLabel = new JLabel("Ready");
        statusLabel.setBorder(new EmptyBorder(5, 10, 5, 10));
        add(statusLabel, BorderLayout.PAGE_END);
        
        // Center the window on screen
        setLocationRelativeTo(null);
    }
    
    /**
     * Creates the menu bar with File and Edit menus
     */
    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        // File Menu
        JMenu fileMenu = new JMenu("File");
        JMenuItem newMenuItem = new JMenuItem("New");
        JMenuItem exportMenuItem = new JMenuItem("Export");
        JMenuItem importMenuItem = new JMenuItem("Import");
        JMenuItem exitMenuItem = new JMenuItem("Exit");
        
        newMenuItem.addActionListener(e -> createNewStudent());
        exportMenuItem.addActionListener(e -> exportStudents());
        importMenuItem.addActionListener(e -> importStudents());
        exitMenuItem.addActionListener(e -> System.exit(0));
        
        fileMenu.add(newMenuItem);
        fileMenu.add(exportMenuItem);
        fileMenu.add(importMenuItem);
        fileMenu.addSeparator();
        fileMenu.add(exitMenuItem);
        
        // Edit Menu
        JMenu editMenu = new JMenu("Edit");
        JMenuItem saveMenuItem = new JMenuItem("Save");
        JMenuItem deleteMenuItem = new JMenuItem("Delete");
        
        saveMenuItem.addActionListener(e -> saveCurrentStudent());
        deleteMenuItem.addActionListener(e -> deleteCurrentStudent());
        
        editMenu.add(saveMenuItem);
        editMenu.add(deleteMenuItem);
        
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        
        setJMenuBar(menuBar);
    }
    
    /**
     * Creates the panel containing the student list as a JTable
     */
    private void createStudentListPanel() {
        // Create table model with column names
        tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tableModel.addColumn("ID");
        tableModel.addColumn("Name");
        tableModel.addColumn("Surname");
        tableModel.addColumn("Country");
        
        // Create table and add to a scroll pane
        studentTable = new JTable(tableModel);
        studentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        studentTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && studentTable.getSelectedRow() >= 0) {
                selectStudent(studentTable.getSelectedRow());
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(studentTable);
        scrollPane.setPreferredSize(new Dimension(400, 500));
        
        add(scrollPane, BorderLayout.WEST);
    }
    
    /**
     * Creates the panel with form fields for student details
     */
    private void createDetailsPanel() {
        detailsPanel = new JPanel();
        detailsPanel.setLayout(new GridBagLayout());
        detailsPanel.setBorder(BorderFactory.createTitledBorder("Student Details"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Create ID field with generate button
        gbc.gridx = 0;
        gbc.gridy = 0;
        detailsPanel.add(new JLabel("Student ID:"), gbc);
        
        JPanel idPanel = new JPanel(new BorderLayout(5, 0));
        idField = new JTextField(20);
        JButton generateIdButton = new JButton("Generate ID");
        generateIdButton.addActionListener(e -> generateAndSetId());
        idPanel.add(idField, BorderLayout.CENTER);
        idPanel.add(generateIdButton, BorderLayout.EAST);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        detailsPanel.add(idPanel, gbc);
        gbc.weightx = 0.0;
        gbc.fill = GridBagConstraints.NONE;
        
        // Create other form fields
        nameField = createFormField(detailsPanel, "Name:", 1, gbc);
        surnameField = createFormField(detailsPanel, "Surname:", 2, gbc);
        countryField = createFormField(detailsPanel, "Country:", 3, gbc);
        dateOfBirthField = createFormField(detailsPanel, "Date of Birth (YYYY-MM-DD):", 4, gbc);
        
        // Study Abroad checkbox
        gbc.gridx = 0;
        gbc.gridy = 5;
        detailsPanel.add(new JLabel("Study Abroad:"), gbc);
        
        gbc.gridx = 1;
        studyAbroadCheckBox = new JCheckBox();
        detailsPanel.add(studyAbroadCheckBox, gbc);
        
        // Additional fields
        gpaField = createFormField(detailsPanel, "GPA:", 6, gbc);
        majorField = createFormField(detailsPanel, "Major:", 7, gbc);
        enrollmentDateField = createFormField(detailsPanel, "Enrollment Date (YYYY-MM-DD):", 8, gbc);
        emailField = createFormField(detailsPanel, "Email:", 9, gbc);
        phoneField = createFormField(detailsPanel, "Phone:", 10, gbc);
        
        // Add a prominent save button directly in the form
        gbc.gridx = 0;
        gbc.gridy = 11;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(15, 5, 5, 5);
        JButton saveDetailsButton = new JButton("Save Student");
        saveDetailsButton.setFont(saveDetailsButton.getFont().deriveFont(Font.BOLD));
        saveDetailsButton.setBackground(new Color(100, 180, 100));
        saveDetailsButton.addActionListener(e -> saveCurrentStudent());
        detailsPanel.add(saveDetailsButton, gbc);
        
        add(detailsPanel, BorderLayout.CENTER);
    }
    
    /**
     * Helper method to create form fields with labels
     */
    private JTextField createFormField(JPanel panel, String labelText, int row, GridBagConstraints gbc) {
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel(labelText), gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        JTextField textField = new JTextField(20);
        panel.add(textField, gbc);
        gbc.weightx = 0.0;
        gbc.fill = GridBagConstraints.NONE;
        
        return textField;
    }
    
    /**
     * Creates the panel with action buttons at the bottom of the form
     */
    private void createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBorder(BorderFactory.createTitledBorder("Actions"));
        
        JButton browseButton = new JButton("Browse");
        JButton newButton = new JButton("New");
        JButton selectButton = new JButton("Select");
        JButton loadButton = new JButton("Load");
        JButton saveButton = new JButton("Save");
        saveButton.setFont(saveButton.getFont().deriveFont(Font.BOLD));
        saveButton.setBackground(new Color(100, 180, 100));
        saveButton.setForeground(Color.WHITE);
        
        JButton findButton = new JButton("Find");
        JButton exportButton = new JButton("Export");
        
        // Add action listeners
        browseButton.addActionListener(e -> refreshStudentList());
        newButton.addActionListener(e -> createNewStudent());
        selectButton.addActionListener(e -> {
            int row = studentTable.getSelectedRow();
            if (row >= 0) {
                selectStudent(row);
            } else {
                JOptionPane.showMessageDialog(this, "Please select a student from the list first.");
            }
        });
        loadButton.addActionListener(e -> loadCurrentStudent());
        saveButton.addActionListener(e -> saveCurrentStudent());
        findButton.addActionListener(e -> findStudents());
        exportButton.addActionListener(e -> exportStudents());
        
        buttonPanel.add(browseButton);
        buttonPanel.add(newButton);
        buttonPanel.add(selectButton);
        buttonPanel.add(loadButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(findButton);
        buttonPanel.add(exportButton);
        
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Creates the search panel
     */
    private void createSearchPanel() {
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBorder(BorderFactory.createTitledBorder("Search"));
        
        searchPanel.add(new JLabel("Search by:"));
        
        searchCriteriaComboBox = new JComboBox<>(new String[]{"Surname", "Name", "Country", "ID"});
        searchPanel.add(searchCriteriaComboBox);
        
        searchField = new JTextField(20);
        searchPanel.add(searchField);
        
        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(e -> findStudents());
        searchPanel.add(searchButton);
        
        JButton resetButton = new JButton("Reset");
        resetButton.addActionListener(e -> {
            searchField.setText("");
            refreshStudentList();
        });
        searchPanel.add(resetButton);
        
        add(searchPanel, BorderLayout.NORTH);
    }
    
    /**
     * Creates a new student and makes it the current student
     */
    private void createNewStudent() {
        clearFormFields();
        currentStudent = new CStudent();
        currentStudentIndex = -1;
        statusLabel.setText("Creating new student. Fill in details and click Save.");
    }
    
    /**
     * Selects a student from the list and makes it the current student
     */
    private void selectStudent(int index) {
        if (index >= 0 && index < students.size()) {
            currentStudent = students.get(index);
            currentStudentIndex = index;
            loadCurrentStudent();
            statusLabel.setText("Selected student: " + currentStudent.getName() + " " + currentStudent.getSurname());
        }
    }
    
    /**
     * Loads the current student's data into the form fields
     */
    private void loadCurrentStudent() {
        if (currentStudent == null) {
            JOptionPane.showMessageDialog(this, "No student selected.");
            return;
        }
        
        idField.setText(currentStudent.getId());
        nameField.setText(currentStudent.getName());
        surnameField.setText(currentStudent.getSurname());
        countryField.setText(currentStudent.getCountry());
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        dateOfBirthField.setText(currentStudent.getDateOfBirth() != null ? 
                                currentStudent.getDateOfBirth().format(formatter) : "");
        studyAbroadCheckBox.setSelected(currentStudent.isStudyAbroad());
        gpaField.setText(String.valueOf(currentStudent.getGpa()));
        majorField.setText(currentStudent.getMajor());
        enrollmentDateField.setText(currentStudent.getEnrollmentDate() != null ? 
                                   currentStudent.getEnrollmentDate().format(formatter) : "");
        emailField.setText(currentStudent.getEmail());
        phoneField.setText(currentStudent.getPhoneNumber());
        
        statusLabel.setText("Loaded student data.");
    }
    
    /**
     * Saves the form field data to the current student
     */
    private void saveCurrentStudent() {
        if (currentStudent == null) {
            currentStudent = new CStudent();
        }
        
        // Gather form field data
        String newId = idField.getText().trim();
        String name = nameField.getText().trim();
        String surname = surnameField.getText().trim();
        String country = countryField.getText().trim();
        String dobText = dateOfBirthField.getText().trim();
        boolean isStudyAbroad = studyAbroadCheckBox.isSelected();
        String gpaText = gpaField.getText().trim();
        String major = majorField.getText().trim();
        String enrollmentDateText = enrollmentDateField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        
        // Validate and save the data using the Helper class
        try {
            // First check if this is a duplicate ID (if editing an existing student)
            boolean isDuplicate = false;
            if (currentStudentIndex == -1) { // New student
                isDuplicate = Helper.isDuplicateStudentId(newId, students);
            } else { // Existing student
                isDuplicate = Helper.isDuplicateStudentId(newId, students, currentStudentIndex);
            }
            
            if (isDuplicate) {
                JOptionPane.showMessageDialog(this, 
                    "Student ID '" + newId + "' already exists. IDs must be unique.", 
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Let the Helper class validate and process the data
            CStudent updatedStudent = Helper.createOrUpdateStudent(
                currentStudent, newId, name, surname, country, 
                dobText, isStudyAbroad, gpaText, major, 
                enrollmentDateText, email, phone
            );
            
            // Update the current student reference
            currentStudent = updatedStudent;
            
            // Add to list if new
            if (currentStudentIndex == -1) {
                students.add(currentStudent);
                statusLabel.setText("New student added successfully.");
            } else {
                students.set(currentStudentIndex, currentStudent);
                statusLabel.setText("Student updated successfully.");
            }
            
            // Refresh table
            refreshStudentList();
        } catch (IllegalArgumentException e) {
            // Display validation error
            JOptionPane.showMessageDialog(this, e.getMessage(), 
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Deletes the current student from the list
     */
    private void deleteCurrentStudent() {
        if (currentStudent == null || currentStudentIndex == -1) {
            JOptionPane.showMessageDialog(this, "No student selected to delete.");
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
                                                "Are you sure you want to delete this student?",
                                                "Confirm Delete", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            students.remove(currentStudentIndex);
            refreshStudentList();
            clearFormFields();
            currentStudent = null;
            currentStudentIndex = -1;
            statusLabel.setText("Student deleted successfully.");
        }
    }
    
    /**
     * Searches for students based on the search criteria
     */
    private void findStudents() {
        String searchText = searchField.getText().trim().toLowerCase();
        if (searchText.isEmpty()) {
            refreshStudentList();
            return;
        }
        
        String criterion = (String) searchCriteriaComboBox.getSelectedItem();
        List<CStudent> filteredStudents = new ArrayList<>();
        
        for (CStudent student : students) {
            boolean match = false;
            
            switch (criterion) {
                case "Surname":
                    match = student.getSurname().toLowerCase().startsWith(searchText);
                    break;
                case "Name":
                    match = student.getName().toLowerCase().startsWith(searchText);
                    break;
                case "Country":
                    match = student.getCountry().toLowerCase().startsWith(searchText);
                    break;
                case "ID":
                    match = student.getId().toLowerCase().startsWith(searchText);
                    break;
            }
            
            if (match) {
                filteredStudents.add(student);
            }
        }
        
        updateStudentTable(filteredStudents);
        statusLabel.setText("Found " + filteredStudents.size() + " students matching criteria.");
    }
    
    /**
     * Updates the table with the given list of students
     */
    private void updateStudentTable(List<CStudent> studentList) {
        tableModel.setRowCount(0);
        
        for (CStudent student : studentList) {
            tableModel.addRow(new Object[]{
                student.getId(),
                student.getName(),
                student.getSurname(),
                student.getCountry()
            });
        }
        
        if (!studentList.isEmpty()) {
            studentTable.setRowSelectionInterval(0, 0);
        }
    }
    
    /**
     * Refreshes the student list in the table
     */
    private void refreshStudentList() {
        updateStudentTable(students);
    }
    
    /**
     * Clears all form fields
     */
    private void clearFormFields() {
        idField.setText("");
        nameField.setText("");
        surnameField.setText("");
        countryField.setText("");
        dateOfBirthField.setText("");
        studyAbroadCheckBox.setSelected(false);
        gpaField.setText("0.0");
        majorField.setText("");
        enrollmentDateField.setText("");
        emailField.setText("");
        phoneField.setText("");
    }
    
    /**
     * Exports students to a CSV file
     */
    private void exportStudents() {
        if (students.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No students to export.");
            return;
        }
        
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export Students");
        fileChooser.setFileFilter(new FileNameExtensionFilter("CSV files (*.csv)", "csv"));
        
        int userSelection = fileChooser.showSaveDialog(this);
        
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            String filePath = fileToSave.getAbsolutePath();
            
            if (!filePath.endsWith(".csv")) {
                filePath += ".csv";
            }
            
            boolean success = Helper.exportStudentsToCSV(students, filePath);
            
            if (success) {
                statusLabel.setText("Students exported successfully to " + filePath);
            } else {
                JOptionPane.showMessageDialog(this, "Error exporting students to file.",
                                             "Export Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Imports students from a CSV file
     */
    private void importStudents() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Import Students");
        fileChooser.setFileFilter(new FileNameExtensionFilter("CSV files (*.csv)", "csv"));
        
        int userSelection = fileChooser.showOpenDialog(this);
        
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToLoad = fileChooser.getSelectedFile();
            
            try {
                // Use the Helper class to import students, passing current students and this frame as parent
                // This will handle duplicate IDs by showing dialog prompts
                List<CStudent> importedStudents = Helper.importStudentsFromCSV(fileToLoad.getAbsolutePath(), students, this);
                
                if (importedStudents.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "No valid students found in file or all students were skipped.",
                                                "Import Notice", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                
                // Ask if user wants to append or replace
                String[] options = {"Replace existing", "Append to existing"};
                int choice = JOptionPane.showOptionDialog(this, 
                                                       "Import " + importedStudents.size() + " students?",
                                                       "Import Options",
                                                       JOptionPane.YES_NO_OPTION,
                                                       JOptionPane.QUESTION_MESSAGE,
                                                       null,
                                                       options,
                                                       options[0]);
                if (choice == 0) { // Replace
                    students.clear();
                }
                
                // Add all imported students
                students.addAll(importedStudents);
                
                refreshStudentList();
                statusLabel.setText("Imported " + importedStudents.size() + " students successfully.");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error importing students: " + e.getMessage(),
                                             "Import Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Generates a unique ID based on the current timestamp and sets it in the ID field
     */
    private void generateAndSetId() {
        String generatedId = Helper.generateUniqueId();
        idField.setText(generatedId);
        statusLabel.setText("Generated new ID: " + generatedId);
    }
    
 
}
