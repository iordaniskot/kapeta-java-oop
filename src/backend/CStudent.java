package backend;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * This class represents a student in the Secretariat application.
 * It contains all the student's personal and academic information.
 * 
 * The class implements Serializable to allow for saving/loading
 * student data to/from files or for network transmission.
 */
public class CStudent implements Serializable {
    // Serialization version UID ensures compatibility between different versions of the class
    private static final long serialVersionUID = 1L;
    
    // Core identifying and personal information fields
    private String id;          // Unique identifier for each student
    private String name;        // Student's first name
    private String surname;     // Student's last name/family name
    private String country;     // Student's country of origin
    private LocalDate dateOfBirth; // Student's birth date (using modern Java time API)
    private boolean isStudyAbroad; // Flag indicating if this is an exchange/study abroad student
    
    // Academic information fields
    private double gpa;         // Grade Point Average (typically 0.0-4.0)
    private String major;       // Student's main field of study
    private LocalDate enrollmentDate; // When the student first enrolled
    private String email;       // Student's contact email
    private String phoneNumber; // Student's contact phone number

    /**
     * Default constructor that initializes all fields with empty or default values.
     * This is useful when creating a new student record from scratch.
     */
    public CStudent() {
        // Initialize with empty/default values
        this.id = "";
        this.name = "";
        this.surname = "";
        this.country = "";
        this.dateOfBirth = LocalDate.now(); // Current date as placeholder
        this.isStudyAbroad = false;
        this.gpa = 0.0;
        this.major = "";
        this.enrollmentDate = LocalDate.now(); // Current date as placeholder
        this.email = "";
        this.phoneNumber = "";
    }

    /**
     * Parameterized constructor with required fields only.
     * Other fields will be set to default values and can be updated later.
     * 
     * @param id Unique student identifier
     * @param name Student's first name
     * @param surname Student's last name
     * @param country Student's country of origin
     * @param dateOfBirth Student's date of birth
     * @param isStudyAbroad Whether this is an international/exchange student
     */
    public CStudent(String id, String name, String surname, String country, 
                   LocalDate dateOfBirth, boolean isStudyAbroad) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.country = country;
        this.dateOfBirth = dateOfBirth;
        this.isStudyAbroad = isStudyAbroad;
        // Set default values for optional fields
        this.gpa = 0.0;
        this.major = "";
        this.enrollmentDate = LocalDate.now();
        this.email = "";
        this.phoneNumber = "";
    }

    /**
     * Full parameterized constructor for creating a complete student record.
     * 
     * @param id Unique student identifier
     * @param name Student's first name
     * @param surname Student's last name
     * @param country Student's country of origin
     * @param dateOfBirth Student's date of birth
     * @param isStudyAbroad Whether this is an international/exchange student
     * @param gpa Student's Grade Point Average
     * @param major Student's field of study
     * @param enrollmentDate Date when student enrolled
     * @param email Student's email address
     * @param phoneNumber Student's phone number
     */
    public CStudent(String id, String name, String surname, String country, 
                   LocalDate dateOfBirth, boolean isStudyAbroad, double gpa,
                   String major, LocalDate enrollmentDate, String email, String phoneNumber) {
        // Initialize all fields with provided values
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.country = country;
        this.dateOfBirth = dateOfBirth;
        this.isStudyAbroad = isStudyAbroad;
        this.gpa = gpa;
        this.major = major;
        this.enrollmentDate = enrollmentDate;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }
    
    // Getters and setters for all properties
    // These provide encapsulation - controlled access to the class's private fields

    /**
     * Gets the student's unique identifier
     * @return The student's ID
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the student's unique identifier
     * @param id New ID value
     */
    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public boolean isStudyAbroad() {
        return isStudyAbroad;
    }

    public void setStudyAbroad(boolean studyAbroad) {
        isStudyAbroad = studyAbroad;
    }

    public double getGpa() {
        return gpa;
    }

    public void setGpa(double gpa) {
        this.gpa = gpa;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public LocalDate getEnrollmentDate() {
        return enrollmentDate;
    }

    public void setEnrollmentDate(LocalDate enrollmentDate) {
        this.enrollmentDate = enrollmentDate;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * Calculate the age of the student based on the date of birth
     * Note: This is a simple calculation and doesn't account for exact days
     * 
     * @return the age in years
     */
    public int calculateAge() {
        return LocalDate.now().getYear() - dateOfBirth.getYear();
    }

    /**
     * Calculate how many years the student has been enrolled
     * Note: This is a simple calculation and doesn't account for exact days
     * 
     * @return years since enrollment
     */
    public int calculateYearsEnrolled() {
        return LocalDate.now().getYear() - enrollmentDate.getYear();
    }

    /**
     * Checks if this student is equal to another object.
     * Students are considered equal if they have the same ID,
     * as the ID is designed to be unique for each student.
     * 
     * @param o The object to compare with
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;  // Same object reference
        if (o == null || getClass() != o.getClass()) return false; // Not the same type
        CStudent student = (CStudent) o;
        return Objects.equals(id, student.id); // Compare by ID only
    }

    /**
     * Generates a hash code for this student based on their ID.
     * This is consistent with the equals method.
     * 
     * @return A hash code value for this student
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /**
     * Returns a string representation of the student in the format:
     * "surname, name (id)"
     * 
     * @return A concise string representation
     */
    @Override
    public String toString() {
        return surname + ", " + name + " (" + id + ")";
    }
    
    /**
     * Returns a detailed string representation of the student including all fields
     * This is useful for displays that need to show comprehensive student information
     * 
     * @return detailed information about the student
     */
    public String getDetailedInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("Student ID: ").append(id).append("\n");
        sb.append("Name: ").append(name).append(" ").append(surname).append("\n");
        sb.append("Country: ").append(country).append("\n");
        sb.append("Date of Birth: ").append(dateOfBirth).append("\n");
        sb.append("Study Abroad: ").append(isStudyAbroad ? "Yes" : "No").append("\n");
        sb.append("GPA: ").append(gpa).append("\n");
        sb.append("Major: ").append(major).append("\n");
        sb.append("Enrollment Date: ").append(enrollmentDate).append("\n");
        sb.append("Contact: ").append(email).append(", ").append(phoneNumber);
        return sb.toString();
    }
}
