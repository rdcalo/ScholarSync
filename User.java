	package scholar;

import java.util.HashSet;
import java.util.Set;

public class User {
    private int id;
    private String firstName;
    private String middleName;
    private String lastName;
    private String email;
    private String password;
    private String school;
    private String trackStrand;
    private double gpa;
    private String monthlyIncome;
    private String collegeCourse;
    private String studentId;

    // ✅ New field to track applied scholarships
    private Set<String> appliedScholarships = new HashSet<>();

    // Default constructor
    public User() {
    }

    // Constructor with all fields
    public User(String firstName, String middleName, String lastName, String email,
                String password, String school, String trackStrand, double gpa,
                String monthlyIncome, String collegeCourse, String studentId) {
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.school = school;
        this.trackStrand = trackStrand;
        this.gpa = gpa;
        this.monthlyIncome = monthlyIncome;
        this.collegeCourse = collegeCourse;
        this.studentId = studentId;
    }

    // Getters and Setters (existing)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getTrackStrand() {
        return trackStrand;
    }

    public void setTrackStrand(String trackStrand) {
        this.trackStrand = trackStrand;
    }

    public double getGpa() {
        return gpa;
    }

    public void setGpa(double gpa) {
        this.gpa = gpa;
    }

    public String getMonthlyIncome() {
        return monthlyIncome;
    }

    public void setMonthlyIncome(String monthlyIncome) {
        this.monthlyIncome = monthlyIncome;
    }

    public String getCollegeCourse() {
        return collegeCourse;
    }

    public void setCollegeCourse(String collegeCourse) {
        this.collegeCourse = collegeCourse;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

        public boolean hasApplied(String scholarshipTitle) {
        return appliedScholarships.contains(scholarshipTitle);
    }

    public void apply(String scholarshipTitle) {
        appliedScholarships.add(scholarshipTitle);
    }

    public Set<String> getAppliedScholarships() {
        return appliedScholarships;
    }
}
