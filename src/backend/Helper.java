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
 * This is a utility class that provides static methods for common operations
 * such as importing and exporting data, validation, and student creation.
 * 
 * Following the Single Responsibility Principle, this class handles operations
 * that don't belong in the Student model or UI classes.
 */
public class Helper {
    // Date format pattern used throughout the application for consistency
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    /**
     * Exports a list of students to a CSV file.
     * The CSV format makes the data accessible to spreadsheet programs and other systems.
     * 
     * @param students The list of students to export
     * @param filePath The path to the CSV file
     * @return true if export was successful, false otherwise
     */
    public static boolean exportStudentsToCSV(List<CStudent> students, String filePath) {
        // Basic validation to prevent processing empty data
        if (students == null || students.isEmpty()) {
            return false;
        }
        
        // Use try-with-resources to ensure the writer is closed properly
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            // Write header line with column names
            writer.println("ID,Name,Surname,Country,DateOfBirth,IsStudyAbroad,GPA,Major,EnrollmentDate,Email,PhoneNumber");
            
            // Write each student's data as a CSV row
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
            // Log the error and return false to indicate failure
            System.err.println("Error exporting students: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Imports students from a CSV file, showing dialogs for duplicate IDs.
     * This is the most comprehensive import method that handles UI interactions.
     * 
     * @param filePath The path to the CSV file
     * @param existingStudents List of existing students to check for duplicate IDs
     * @param parent The parent component for displaying dialog boxes
     * @return List of imported students, or empty list if import failed
     */
    public static List<CStudent> importStudentsFromCSV(String filePath, List<CStudent> existingStudents, Component parent) {
        List<CStudent> importedStudents = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            // Skip header row (column names)
            String line = reader.readLine();
            
            // Process each data row in the CSV file
            int lineNumber = 1;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                try {
                    // Parse the line into a CStudent object
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
                    // Log error but continue processing other lines
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
     * Handles duplicate IDs by prompting the user to provide a new ID.
     * This method manages the UI interaction for resolving duplicate ID conflicts.
     * 
     * @param student The student with a potentially duplicate ID
     * @param importedStudents Already imported students in this batch
     * @param existingStudents Existing students in the database
     * @param parent The parent component for dialog boxes
     * @return The student with a unique ID, or null if the user chose to skip
     */
    private static CStudent handleDuplicateIDs(CStudent student, List<CStudent> importedStudents, 
                                              List<CStudent> existingStudents, Component parent) {
        String studentId = student.getId();
        boolean isDuplicate = false;
        String duplicateSource = "";
        
        // First, check if this ID already exists in the current import batch
        for (CStudent s : importedStudents) {
            if (s.getId().equals(studentId)) {
                isDuplicate = true;
                duplicateSource = "import batch";
                break;
            }
        }
        
        // Then check existing database if not already found as duplicate
        if (!isDuplicate && existingStudents != null) {
            for (CStudent s : existingStudents) {
                if (s.getId().equals(studentId)) {
                    isDuplicate = true;
                    duplicateSource = "existing database";
                    break;
                }
            }
        }
        
        // If duplicate found, enter a resolution loop until a unique ID is provided or user cancels
        while (isDuplicate) {
            // Prepare the message explaining the duplicate situation
            String message = "Duplicate student ID '" + studentId + "' found in " + duplicateSource + ".\n" +
                            "Student: " + student.getName() + " " + student.getSurname() + "\n" +
                            "Please choose an option:";
            
            // Options for resolving the duplicate ID
            String[] options = {"Enter Manual ID", "Generate Auto ID", "Skip Student"};
            int choice = JOptionPane.showOptionDialog(
                parent, message, "Duplicate ID",
                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE,
                null, options, options[0]);
            
            if (choice == 0) {
                // User chose to manually enter a new ID
                String newId = JOptionPane.showInputDialog(parent, 
                    "Enter a new unique ID:", "Duplicate ID", JOptionPane.QUESTION_MESSAGE);
                
                // If user cancels or enters blank, return to options
                if (newId == null || newId.trim().isEmpty()) {
                    continue;
                }
                
                studentId = newId.trim();
            } else if (choice == 1) {
                // User chose to auto-generate ID
                studentId = generateUniqueId();
            } else {
                // User chose to skip this student
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
     * Validates a student ID to ensure it's not null or empty.
     * 
     * @param id The ID to validate
     * @return true if the ID is valid, false otherwise
     */
    public static boolean isValidStudentId(String id) {
        return id != null && !id.trim().isEmpty();
    }
    
    /**
     * Simplified import method for backward compatibility.
     * This overload doesn't check for duplicates or show UI prompts.
     * 
     * @param filePath The path to the CSV file
     * @return List of imported students
     */
    public static List<CStudent> importStudentsFromCSV(String filePath) {
        return importStudentsFromCSV(filePath, null, null);
    }
    
    /**
     * Import method that checks for duplicates but doesn't show UI prompts.
     * This is useful for non-GUI contexts that still need duplicate checking.
     * 
     * @param filePath The path to the CSV file
     * @param existingStudents List of existing students to check for duplicate IDs
     * @return List of imported students
     */
    public static List<CStudent> importStudentsFromCSV(String filePath, List<CStudent> existingStudents) {
        return importStudentsFromCSV(filePath, existingStudents, null);
    }
    
    /**
     * Parses a single line from a CSV file into a CStudent object.
     * This method handles missing fields and formatting issues.
     * 
     * @param line A line from the CSV file
     * @return A CStudent object or null if required fields are missing
     */
    private static CStudent parseStudentFromCSV(String line) {
        String[] data = line.split(",");
        
        // Extract required fields with safety checks
        String id = (data.length > 0) ? data[0].trim() : "";
        String name = (data.length > 1) ? data[1].trim() : "";
        String surname = (data.length > 2) ? data[2].trim() : "";
        
        // Skip if required fields are missing
        if (id.isEmpty() || name.isEmpty() || surname.isEmpty()) {
            System.err.println("Skipping incomplete record (missing ID, name, or surname): " + line);
            return null;
        }
        
        // Extract and process remaining fields with safety checks
        String country = (data.length > 3) ? data[3].trim() : "";
        
        // Parse date of birth with fallback to current date if invalid
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
        
        // Parse GPA with fallback and range validation
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
        
        // Create and return the student with all available data
        return new CStudent(
            id, name, surname, country, dob, isStudyAbroad,
            gpa, major, enrollmentDate, email, phone
        );
    }

    /**
     * Generates a unique ID based on the current timestamp.
     * This ensures uniqueness even when multiple IDs are generated in quick succession.
     * 
     * @return A string ID in the format S + timestamp in milliseconds
     */
    public static String generateUniqueId() {
        return "S" + System.currentTimeMillis();
    }

    /**
     * Checks if a student ID is a duplicate in a list of students.
     * This is useful for validation before adding or updating students.
     * 
     * @param id The ID to check
     * @param students The list of students to check against
     * @return true if the ID exists in the list, false otherwise
     */
    public static boolean isDuplicateStudentId(String id, List<CStudent> students) {
        // Stream-based approach to check if any student has matching ID
        return students.stream().anyMatch(s -> s.getId().equals(id));
    }

    /**
     * Checks if a student ID is a duplicate in a list, excluding a specific index.
     * This is useful when updating an existing student to ignore its own ID.
     * 
     * @param id The ID to check
     * @param students The list of students to check against
     * @param excludeIndex The index to exclude from the check
     * @return true if the ID exists in the list (excluding the specified index), false otherwise
     */
    public static boolean isDuplicateStudentId(String id, List<CStudent> students, int excludeIndex) {
        for (int i = 0; i < students.size(); i++) {
            // Skip the student at the exclude index (typically the one being updated)
            if (i != excludeIndex && students.get(i).getId().equals(id)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Creates or updates a student with validated data from form fields.
     * This method centralizes validation logic and student creation/update.
     * 
     * @param student The student to update (or null to create a new one)
     * @param id The student ID
     * @param name The student name
     * @param surname The student surname
     * @param country The student country
     * @param dobText The date of birth as text
     * @param isStudyAbroad Whether the student is studying abroad
     * @param gpaText The GPA as text
     * @param major The student major
     * @param enrollmentDateText The enrollment date as text
     * @param email The student email
     * @param phone The student phone number
     * @return The updated or created student
     * @throws IllegalArgumentException If any validation fails
     */
    public static CStudent createOrUpdateStudent(CStudent student, String id, String name, String surname,
                                              String country, String dobText, boolean isStudyAbroad,
                                              String gpaText, String major, String enrollmentDateText,
                                              String email, String phone) throws IllegalArgumentException {
        // Validate required fields
        if (!isValidStudentId(id) || name.isEmpty() || surname.isEmpty()) {
            throw new IllegalArgumentException("ID, Name and Surname are required fields and cannot be blank.");
        }
        
        // Create a new student if none was provided
        if (student == null) {
            student = new CStudent();
        }
        
        // Parse and validate dates
        LocalDate dob = null;
        LocalDate enrollmentDate = null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        try {
            if (!dobText.isEmpty()) {
                dob = LocalDate.parse(dobText, formatter);
            }
            
            if (!enrollmentDateText.isEmpty()) {
                enrollmentDate = LocalDate.parse(enrollmentDateText, formatter);
            }
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format. Please use YYYY-MM-DD format.");
        }
        
        // Parse and validate GPA
        double gpa = 0.0;
        try {
            if (!gpaText.isEmpty()) {
                gpa = Double.parseDouble(gpaText);
                if (gpa < 0 || gpa > 4.0) {
                    throw new IllegalArgumentException("GPA must be between 0.0 and 4.0");
                }
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid GPA format. Please enter a number.");
        }
        
        // Set student fields with validated data
        student.setId(id);
        student.setName(name);
        student.setSurname(surname);
        student.setCountry(country);
        student.setDateOfBirth(dob != null ? dob : LocalDate.now());
        student.setStudyAbroad(isStudyAbroad);
        student.setGpa(gpa);
        student.setMajor(major);
        student.setEnrollmentDate(enrollmentDate != null ? enrollmentDate : LocalDate.now());
        student.setEmail(email);
        student.setPhoneNumber(phone);
        
        return student;
    }
}
