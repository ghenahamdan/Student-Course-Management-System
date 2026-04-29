package org.example;

import org.example.model.Course;
import org.example.model.Student;

import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        CourseManager manager = new CourseManager();
        Scanner scanner = new Scanner(System.in);
        int choice;

        // Load sample data
        manager.loadSampleData();
        System.out.println();

        System.out.println("=== Welcome to Student Course Management System ===\n");
        do {
            // Display menu
            System.out.println("\n===== MENU =====");
            System.out.println("1. Add Student");
            System.out.println("2. Add Course");
            System.out.println("3. Enroll Student into Course");
            System.out.println("4. View All Students");
            System.out.println("5. View All Courses");
            System.out.println("6. View Students Sorted by Name (Stream)");
            System.out.println("7. View Courses with More Than X Students (Stream)");
            System.out.println("8. View Total Unique Students Enrolled (Stream)");
            System.out.println("9. Set Grade for Student in Course");
            System.out.println("10. View all Grades of a Student");
            System.out.println("11. Simulate Concurrent Enrollment (Multithreading)");
            System.out.println("0. Exit");
            System.out.print("Enter your choice: ");

            choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    // Add Student
                    System.out.println("\n--- Add New Student ---");
                    System.out.print("Enter student ID: ");
                    int id = scanner.nextInt();
                    scanner.nextLine();

                    System.out.print("Enter student name: ");
                    String name = scanner.nextLine();

                    System.out.print("Enter student email: ");
                    String email = scanner.nextLine();

                    //create a new student object using the constructor
                    Student student = new Student(id, name, email);
                    //pass that student object to the manager to store it
                    manager.addStudent(student);
                    break;

                case 2:
                    // Add course
                    System.out.println("\n--- Add New Course ---");
                    System.out.print("Enter course code: ");
                    String code = scanner.nextLine();

                    System.out.print("Enter course title: ");
                    String title = scanner.nextLine();

                    System.out.print("Enter max capacity: ");
                    int capacity = scanner.nextInt();
                    scanner.nextLine();

                    //create a new course object using the constructor
                    Course course = new Course(code, title, capacity);
                    //pass that course object to the manager to store it
                    manager.addCourse(course);
                    break;

                case 3:
                    // Enroll Student
                    System.out.println("\n--- Enroll Student ---");
                    System.out.print("Enter student ID: ");
                    int studentId = scanner.nextInt();
                    scanner.nextLine();

                    System.out.print("Enter course code: ");
                    String courseCode = scanner.nextLine();

                    try {
                        manager.enrollStudent(studentId, courseCode);
                    } catch (CourseFullException | DuplicateEnrollmentException e) {
                        System.out.println("Enrollment failed: " + e.getMessage());
                    }
                    break;

                case 4:
                    // View All Students
                    manager.viewAllStudents();
                    break;

                case 5:
                    // View All Courses
                    manager.viewAllCourses();
                    break;

                case 6:
                    // View Students Sorted by Name (Stream)
                    System.out.println("\n--- Students Sorted by Name ---");
                    List<Student> sortedStudents = manager.getAllStudentsSorted();
                    if (sortedStudents.isEmpty()) {
                        System.out.println("No students found.");
                    } else {
                        for (Student s : sortedStudents) {
                            System.out.println(s.getName() + " (ID: " + s.getId() + ", Email: " + s.getEmail() + ")");
                        }
                    }
                    break;

                case 7:
                    // View Courses with More Than X Students (Stream)
                    System.out.println("\n--- Courses with More Than X Students ---");
                    System.out.print("Enter minimum number of students: ");
                    int minStudents = scanner.nextInt();
                    scanner.nextLine();

                    List<Course> filteredCourses = manager.getCoursesWithMoreThan(minStudents);
                    if (filteredCourses.isEmpty()) {
                        System.out.println("No courses with more than " + minStudents + " student(s).");
                    } else {
                        for (Course c : filteredCourses) {
                            System.out.println(c.getTitle() + " (" + c.getCode() + ") - "
                                    + c.getEnrolledStudents().size() + "/" + c.getMaxCapacity() + " students");
                        }
                    }
                    break;

                case 8:
                    // View Total Unique Students Enrolled (Stream)
                    System.out.println("\n--- Total Unique Students Enrolled ---");
                    long uniqueCount = manager.getTotalUniqueStudentsEnrolled();
                    System.out.println("Total unique students enrolled across all courses: " + uniqueCount);
                    break;
                case 9:
                    // Set grade
                    System.out.print("Enter student ID: ");
                    int gradeStudentId = scanner.nextInt();
                    scanner.nextLine();

                    System.out.print("Enter course code: ");
                    String gradeCourseCode = scanner.nextLine();

                    System.out.print("Enter grade (A, B+, C-, etc.): ");
                    String grade = scanner.nextLine();

                    manager.setGrade(gradeStudentId, gradeCourseCode, grade);
                    break;
                case 10:
                    System.out.print("Enter student ID: ");
                    int viewStudentId = scanner.nextInt();
                    scanner.nextLine();
                    manager.viewAllGradesForStudent(viewStudentId);
                    break;
                case 11:
                    simulateConcurrentEnrollment();
                    break;
                case 0:
                    System.out.println("\nExiting system. Goodbye!");
                    break;

                default:
                    System.out.println("Invalid choice! Please try again.");
                    break;
            }
        } while (choice != 0);

        scanner.close();
    }

    public static void simulateConcurrentEnrollment() {
        System.out.println("\n===== SIMULATING CONCURRENT ENROLLMENT =====");

        CourseManager manager = new CourseManager();
        manager.loadSampleData();

        // a course with small capacity (CS102 has maxCapacity = 2)
        String courseCode = "CS102";

        System.out.println("Course: " + courseCode);
        System.out.println("Max capacity: 2");
        System.out.println("Attempting to enroll 5 students concurrently...\n");

        // Create 5 enrollment tasks
        EnrollmentTask[] tasks = {
                new EnrollmentTask(manager, 1, courseCode),  // Alice
                new EnrollmentTask(manager, 2, courseCode),  // Bob
                new EnrollmentTask(manager, 3, courseCode),  // Charlie
                new EnrollmentTask(manager, 4, courseCode),  // Diana
                new EnrollmentTask(manager, 1, courseCode)   // Alice again (duplicate)
        };

        // Create and start threads
        Thread[] threads = new Thread[5];
        for (int i = 0; i < tasks.length; i++) {
            threads[i] = new Thread(tasks[i], "Thread-" + (i+1));
            threads[i].start();
        }

        // Wait for all threads to finish
        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("\n--- Final Enrollment for " + courseCode + " ---");
        manager.viewAllCourses();
    }
}

