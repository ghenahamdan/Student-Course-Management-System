package org.example;

//runnable means a task that can be run by a thread.
public class EnrollmentTask implements Runnable {
    private CourseManager manager;
    private Integer studentId;
    private String courseCode;

    public EnrollmentTask(CourseManager manager, Integer studentId, String courseCode) {
        this.manager = manager;
        this.studentId = studentId;
        this.courseCode = courseCode;
    }

    @Override
    public void run() {
        System.out.println(Thread.currentThread().getName() + " attempting to enroll student " + studentId);

        try {
            manager.enrollStudent(studentId, courseCode);
            System.out.println(Thread.currentThread().getName() + " SUCCESS: Student " + studentId + " enrolled");
        } catch (CourseFullException e) {
            System.out.println(Thread.currentThread().getName() + " FAILED: " + e.getMessage());
        } catch (DuplicateEnrollmentException e) {
            System.out.println(Thread.currentThread().getName() + " FAILED: " + e.getMessage());
        }
    }
}