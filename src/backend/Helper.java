package backend;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.awt.Component;
import javax.swing.JOptionPane;

/**
 * Helper class for the Secretariat application.
 * Provides utility functions for importing and exporting student data.
 */
public class Helper {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    /**
     * Exports a list of students to a CSV file
     * 
     * @param students The list of students to export
     * @param filePath The path to the CSV file
     * @return true if export was successful, false otherwise
     */
    public static boolean exportStudentsToCSV(List<CStudent> students, String filePath) {
        if (students == null || students.isEmpty()) {
            return false;
        }
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            // Write header
            writer.println("ID,Name,Surname,Country,DateOfBirth,IsStudyAbroad,GPA,Major,EnrollmentDate,Email,PhoneNumber");
            
            // Write each student's data
            for (CStudent student : students) {
                StringBuilder line = new StringBuilder();
                line.append(student.getId()).append(",");
                line.append(student.getName()).append(",");
                line.append(student.getSurname()).append(",");
                line.append(student.getCountry()).append(",");
                line.append(student.getDateOfBirth().format(DATE_FORMATTER)).append(",");
                line.append(student.isStudyAbroad()).append(",");
                line.append(student.getGpa()).append(",");
                line.append(student.getMajor()).append(",");
                line.append(student.getEnrollmentDate().format(DATE_FORMATTER)).append(",");
                line.append(student.getEmail()).append(",");
                line.append(student.getPhoneNumber());
                
                writer.println(line.toString());
            }
            return true;
        } catch (IOException e) {
            System.err.println("Error exporting students: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Imports students from a CSV file, showing dialogs for duplicate IDs
     * 
     * @param filePath The path to the CSV file
     * @param existingStudents List of existing students to check for duplicate IDs
     * @param parent The parent component for displaying dialog boxes
     * @return List of imported students, or empty list if import failed
     */
    public static List<CStudent> importStudentsFromCSV(String filePath, List<CStudent> existingStudents, Component parent) {
        List<CStudent> importedStudents = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            // Skip header row
            String line = reader.readLine();
            
            // Process data rows
            int lineNumber = 1;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                try {
                    CStudent student = parseStudentFromCSV(line);
                    if (student != null) {
                        // Handle duplicate IDs by prompting for new IDs
                        student = handleDuplicateIDs(student, importedStudents, existingStudents, parent);
                        
                        // If the student is not null after handling duplicates, add to the list
                        if (student != null) {
                            importedStudents.add(student);
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Error parsing line " + lineNumber + ": " + line);
                    System.err.println("Error details: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
        
        return importedStudents;
    }
    
    /**
     * Handles duplicate IDs by prompting the user to provide a new ID
     */
    private static CStudent handleDuplicateIDs(CStudent student, List<CStudent> importedStudents, 
                                              List<CStudent> existingStudents, Component parent) {
        String studentId = student.getId();
        boolean isDuplicate = false;
        String duplicateSource = "";
        
        // Check for duplicate in already imported students
        for (CStudent s : importedStudents) {
            if (s.getId().equals(studentId)) {
                isDuplicate = true;
                duplicateSource = "import batch";
                break;
            }
        }
        
        // Check for duplicate in existing students
        if (!isDuplicate && existingStudents != null) {
            for (CStudent s : existingStudents) {
                if (s.getId().equals(studentId)) {
                    isDuplicate = true;
                    duplicateSource = "existing database";
                    break;
                }
            }
        }
        
        // If duplicate found, prompt for a new ID
        while (isDuplicate) {
            String message = "Duplicate student ID '" + studentId + "' found in " + duplicateSource + ".\n" +
                            "Student: " + student.getName() + " " + student.getSurname() + "\n" +
                            "Please choose an option:";
            
            String[] options = {"Enter Manual ID", "Generate Auto ID", "Skip Student"};
            int choice = JOptionPane.showOptionDialog(
                parent, message, "Duplicate ID",
                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE,
                null, options, options[0]);
            
            if (choice == 0) {
                // Manual ID entry
                String newId = JOptionPane.showInputDialog(parent, 
                    "Enter a new unique ID:", "Duplicate ID", JOptionPane.QUESTION_MESSAGE);
                
                // If user cancels or enters blank, return to options
                if (newId == null || newId.trim().isEmpty()) {
                    continue;
                }
                
                studentId = newId.trim();
            } else if (choice == 1) {
                // Auto-generate ID
                studentId = generateUniqueId();
            } else {
                // Skip student
                return null;
            }
            
            // Check if the new ID is unique
            isDuplicate = false;
            
            // Check against imported students
            for (CStudent s : importedStudents) {
                if (s.getId().equals(studentId)) {
                    isDuplicate = true;
                    duplicateSource = "import batch";
                    break;
                }
            }
            
            // Check against existing students
            if (!isDuplicate && existingStudents != null) {
                for (CStudent s : existingStudents) {
                    if (s.getId().equals(studentId)) {
                        isDuplicate = true;
                        duplicateSource = "existing database";
                        break;
                    }
                }
            }
            
            // If unique, update the student's ID
            if (!isDuplicate) {
                student.setId(studentId);
            }
        }
        
        return student;
    }

    /**
     * Validates a student ID
     * @param id The ID to validate
     * @return true if the ID is valid, false otherwise
     */
    public static boolean isValidStudentId(String id) {
        return id != null && !id.trim().isEmpty();
    }
    
    /**
     * Overloaded method for compatibility
     */
    public static List<CStudent> importStudentsFromCSV(String filePath) {
        return importStudentsFromCSV(filePath, null, null);
    }
    
    /**
     * Overloaded method for compatibility
     */
    public static List<CStudent> importStudentsFromCSV(String filePath, List<CStudent> existingStudents) {
        return importStudentsFromCSV(filePath, existingStudents, null);
    }
    
    /**
     * Parses a single line from a CSV file into a CStudent object
     * 
     * @param line A line from the CSV file
     * @return A CStudent object or null if required fields are missing
     */
    private static CStudent parseStudentFromCSV(String line) {
        String[] data = line.split(",");
        
        // Handle case where there might be fewer fields than expected
        String id = (data.length > 0) ? data[0].trim() : "";
        String name = (data.length > 1) ? data[1].trim() : "";
        String surname = (data.length > 2) ? data[2].trim() : "";
        
        // Skip if required fields are missing
        if (id.isEmpty() || name.isEmpty() || surname.isEmpty()) {
            System.err.println("Skipping incomplete record (missing ID, name, or surname): " + line);
            return null;
        }
        
        String country = (data.length > 3) ? data[3].trim() : "";
        
        // Parse date of birth with fallback
        LocalDate dob;
        try {
            dob = (data.length > 4 && !data[4].trim().isEmpty()) ? 
                  LocalDate.parse(data[4].trim(), DATE_FORMATTER) : LocalDate.now();
        } catch (DateTimeParseException e) {
            System.err.println("Invalid date of birth format, using current date: " + 
                              (data.length > 4 ? data[4] : ""));
            dob = LocalDate.now();
        }
        
        // Parse boolean with fallback
        boolean isStudyAbroad = false;
        if (data.length > 5 && !data[5].trim().isEmpty()) {
            try {
                isStudyAbroad = Boolean.parseBoolean(data[5].trim());
            } catch (Exception e) {
                System.err.println("Invalid study abroad value, using false: " + data[5]);
            }
        }
        
        // Parse GPA with fallback
        double gpa = 0.0;
        if (data.length > 6 && !data[6].trim().isEmpty()) {
            try {
                gpa = Double.parseDouble(data[6].trim());
                if (gpa < 0 || gpa > 4.0) {
                    System.err.println("GPA out of range [0-4.0], clamping: " + gpa);
                    gpa = Math.max(0, Math.min(4.0, gpa));
                }
            } catch (NumberFormatException e) {
                System.err.println("Invalid GPA format, using 0.0: " + data[6]);
            }
        }
        
        String major = (data.length > 7) ? data[7].trim() : "";
        
        // Parse enrollment date with fallback
        LocalDate enrollmentDate;
        try {
            enrollmentDate = (data.length > 8 && !data[8].trim().isEmpty()) ? 
                            LocalDate.parse(data[8].trim(), DATE_FORMATTER) : LocalDate.now();
        } catch (DateTimeParseException e) {
            System.err.println("Invalid enrollment date format, using current date: " + 
                              (data.length > 8 ? data[8] : ""));
            enrollmentDate = LocalDate.now();
        }
        
        String email = (data.length > 9) ? data[9].trim() : "";
        String phone = (data.length > 10) ? data[10].trim() : "";
        
        // Create and return the student
        return new CStudent(
            id, name, surname, country, dob, isStudyAbroad,
            gpa, major, enrollmentDate, email, phone
        );
    }

    /**
     * Generates a unique ID based on the current timestamp
     * @return A string ID in the format S + timestamp
     */
    public static String generateUniqueId() {
        return "S" + System.currentTimeMillis();
    }
}
