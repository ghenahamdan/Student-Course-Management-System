package org.example;

import org.example.model.Course;
import org.example.model.Student;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CourseManager {
    private Map<Integer, Student> students; // stores all students (key = student ID)
    private Map<String, Course> courses; // stores all courses (key = course code)
    //used map for faster access


    //Creates empty HashMaps ready to store data.
    public CourseManager() {
        this.students = new HashMap<Integer, Student>();
        this.courses = new HashMap<>();
    }

    // Add student to system
    public void addStudent(Student student) {
        //Check if ID already in use
        if (students.containsKey(student.getId())) {
            System.out.println("Student with ID " + student.getId() + " already exists!");
        } else {
            //Add student to map
            students.put(student.getId(), student);
            System.out.println("Student added: " + student.getName());
        }
    }

    public void addCourse(Course course) {
        //Check if code already in use
        if (courses.containsKey(course.getCode())) {
            System.out.println("Course with code " + course.getCode() + " already exists!");
        } else {
            //Add course to map
            courses.put(course.getCode(), course);
            System.out.println("Course added: " + course.getTitle());
        }
    }

    // Enroll student into course
    public synchronized void enrollStudent(Integer studentId, String courseCode) throws CourseFullException, DuplicateEnrollmentException {
        // Find student
        Student student = students.get(studentId);
        if (student == null) {
            System.out.println("Student with ID " + studentId + " not found!");
            return;
        }

        // Find course
        Course course = courses.get(courseCode);
        if (course == null) {
            System.out.println("Course with code " + courseCode + " not found!");
            return;
        }

        // Try to add student to course (may throw exception)
        course.addStudentCourse(student);
        System.out.println("Student " + student.getName() + " enrolled in " + course.getTitle());
    }

    // Stream 1: get all students sorted alphabetically by name
    public List<Student> getAllStudentsSorted() {
        return students.values() // Get all students
                .stream() // Start stream
                .sorted(Comparator.comparing(Student::getName)) // Sort by name
                .collect(Collectors.toList()); // Convert back to List
    }

    // Stream 2: get courses with more than X students enrolled
    public List<Course> getCoursesWithMoreThan(int studentCount) {
        return courses.values() //get all courses
                .stream()
                .filter(course -> course.getEnrolledStudents().size() > studentCount)
                // Keep only courses with > X students
                .collect(Collectors.toList());
    }

    // Stream 3: counts unique students across all courses (student in 2 courses counted once).
    public long getTotalUniqueStudentsEnrolled() {
        return courses.values() //get all courses
                .stream()
                .flatMap(course -> course.getEnrolledStudents().stream())
                // combine all students into one stream
                .distinct() // remove duplicates (same student in multiple courses)
                .count(); // Count unique students
    }

    // Helper method to view all courses
    public void viewAllCourses() {
        if (courses.isEmpty()) {
            System.out.println("No courses available.");
            return;
        }

        System.out.println("\n--- All Courses ---");
        for (Course course : courses.values()) {
            System.out.println(course.getCode() + ": " + course.getTitle() +
                    " (Enrolled: " + course.getEnrolledStudents().size() +
                    "/" + course.getMaxCapacity() + ")");
        }
    }

    // Helper method to view all students
    public void viewAllStudents() {
        if (students.isEmpty()) {
            System.out.println("No students available.");
            return;
        }

        System.out.println("\n--- All Students ---");
        for (Student student : students.values()) {
            System.out.println("ID: " + student.getId() + " | Name: " + student.getName() +
                    " | Email: " + student.getEmail());
        }
    }

    // set grade for student
    public void setGrade(Integer studentId, String courseCode, String grade) {
        Student student = students.get(studentId);
        Course course = courses.get(courseCode);

        //check if student and course exist
        if (student == null) {
            System.out.println("Student not found!");
            return;
        }
        if (course == null) {
            System.out.println("Course not found!");
            return;
        }

        course.setGrade(student, grade);
        System.out.println("Grade " + grade + " assigned to " + student.getName() + " for " + course.getTitle());
    }

    public String getGrade(Integer studentId, String courseCode) {
        Student student = students.get(studentId);
        Course course = courses.get(courseCode);

        if (student == null || course == null) {
            return "Student or Course not found!";
        }

        String grade = course.getGrade(student);
        return grade == null ? "No grade assigned yet" : grade;
    }

    public void viewAllGradesForStudent(Integer studentId) {
        Student student = students.get(studentId);
        if (student == null) {
            System.out.println("Student with ID " + studentId + " not found!");
            return;
        }

        System.out.println("\n--- Grades for " + student.getName() + " ---");

        boolean hasGrades = false;
        for (Course course : courses.values()) {
            if (course.getEnrolledStudents().contains(student)) {
                String grade = course.getGrade(student);
                System.out.println(course.getCode() + ": " + course.getTitle() + " → " +
                        (grade == null ? "No grade assigned" : grade));
                hasGrades = true;
            }
        }

        if (!hasGrades) {
            System.out.println("Student is not enrolled in any courses.");
        }
    }

    // Getters
    public Map<Integer, Student> getStudents() {
        return students;
    }

    public Map<String, Course> getCourses() {
        return courses;
    }

    //load daa for testing
    public void loadSampleData() {
        // Sample students
        Student s1 = new Student(1, "Alice Johnson", "alice@email.com");
        Student s2 = new Student(2, "Bob Smith", "bob@email.com");
        Student s3 = new Student(3, "Charlie Brown", "charlie@email.com");
        Student s4 = new Student(4, "Diana Prince", "diana@email.com");

        students.put(s1.getId(), s1);
        students.put(s2.getId(), s2);
        students.put(s3.getId(), s3);
        students.put(s4.getId(), s4);


        // Sample courses
        Course c1 = new Course("CS101", "Introduction to Java", 3);
        Course c2 = new Course("CS102", "Data Structures", 2);
        Course c3 = new Course("MATH101", "Calculus I", 4);

        courses.put(c1.getCode(), c1);
        courses.put(c2.getCode(), c2);
        courses.put(c3.getCode(), c3);

        // enroll students in course for testing
        try {
            // CS101 has capacity 3
            c1.addStudentCourse(s1);  // Alice in Java
            c1.addStudentCourse(s2);  // Bob in Java

            // CS102 has capacity 2
            c2.addStudentCourse(s1);  // Alice also in Data Structures
            c2.addStudentCourse(s3);  // Charlie in Data Structures

            // MATH101 has capacity 4
            c3.addStudentCourse(s4);  // Diana in Calculus

            c1.setGrade(s1, "A");      // Alice in CS101: A
            c1.setGrade(s2, "B+");     // Bob in CS101: B+

            c2.setGrade(s1, "A-");     // Alice in CS102: A-
            c2.setGrade(s3, "C+");     // Charlie in CS102: C+

            c3.setGrade(s4, "B");      // Diana in MATH101: B

        } catch (CourseFullException | DuplicateEnrollmentException e) {
            System.out.println("Error in pre-enrollment: " + e.getMessage());
        }
    }
}