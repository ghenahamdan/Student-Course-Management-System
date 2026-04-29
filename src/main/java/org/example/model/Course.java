package org.example.model;

import org.example.CourseFullException;
import org.example.DuplicateEnrollmentException;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Course {
    private String code;
    private String title;
    private int maxCapacity;
    private Set<Student> enrolledStudents;
    private Map<Student, String> studentGrades;

    //variables in the constructor are unique to each object
    public Course(String code, String title, int maxCapacity) {
        this.code = code;
        this.title = title;
        this.maxCapacity = maxCapacity;
        this.enrolledStudents = new HashSet<>();
        this.studentGrades= new HashMap<>();

    }

    // add student to course
    public void addStudentCourse(Student student) throws CourseFullException, DuplicateEnrollmentException {
        if (isFull()) {
            throw new CourseFullException("Course " + code + " is full!");
        }
        // Check if student already enrolled
        if (enrolledStudents.contains(student)) {
            throw new DuplicateEnrollmentException("Student " + student.getName() + " already enrolled in " + title);
        }
        enrolledStudents.add(student); //student added to set
    }

    public boolean isFull() {
        return enrolledStudents.size() >= maxCapacity;
    }

    // Getters
    public String getCode() {
        return code;
    }

    public String getTitle() {
        return title;
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }

    public Set<Student> getEnrolledStudents() {
        return enrolledStudents;
    }

    public String getGrade(Student student) {
        return studentGrades.get(student);
    }

    //setters
    // this checks if the student is enrolled in the course before setting a grade
    public void setGrade(Student student, String grade) {
        if (enrolledStudents.contains(student)) {
            studentGrades.put(student, grade);

        }
    }
}