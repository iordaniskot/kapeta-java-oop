package backend;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * This class represents a student in the Secretariat application.
 * It contains all the student's personal and academic information.
 */
public class CStudent implements Serializable {
    private static final long serialVersionUID = 1L;
    
    // Required fields as specified
    private String id;
    private String name;
    private String surname;
    private String country;
    private LocalDate dateOfBirth;
    private boolean isStudyAbroad;
    
    // Additional fields
    private double gpa;
    private String major;
    private LocalDate enrollmentDate;
    private String email;
    private String phoneNumber;

    /**
     * Default constructor
     */
    public CStudent() {
        this.id = "";
        this.name = "";
        this.surname = "";
        this.country = "";
        this.dateOfBirth = LocalDate.now();
        this.isStudyAbroad = false;
        this.gpa = 0.0;
        this.major = "";
        this.enrollmentDate = LocalDate.now();
        this.email = "";
        this.phoneNumber = "";
    }

    /**
     * Parameterized constructor with required fields
     */
    public CStudent(String id, String name, String surname, String country, 
                   LocalDate dateOfBirth, boolean isStudyAbroad) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.country = country;
        this.dateOfBirth = dateOfBirth;
        this.isStudyAbroad = isStudyAbroad;
        this.gpa = 0.0;
        this.major = "";
        this.enrollmentDate = LocalDate.now();
        this.email = "";
        this.phoneNumber = "";
    }

    /**
     * Full parameterized constructor
     */
    public CStudent(String id, String name, String surname, String country, 
                   LocalDate dateOfBirth, boolean isStudyAbroad, double gpa,
                   String major, LocalDate enrollmentDate, String email, String phoneNumber) {
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
    
    // Getters and setters
    public String getId() {
        return id;
    }

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
     * @return the age in years
     */
    public int calculateAge() {
        return LocalDate.now().getYear() - dateOfBirth.getYear();
    }

    /**
     * Calculate how many years the student has been enrolled
     * @return years since enrollment
     */
    public int calculateYearsEnrolled() {
        return LocalDate.now().getYear() - enrollmentDate.getYear();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CStudent student = (CStudent) o;
        return Objects.equals(id, student.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return surname + ", " + name + " (" + id + ")";
    }
    
    /**
     * Returns a detailed string representation of the student
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
