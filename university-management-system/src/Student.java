import jdk.swing.interop.SwingInterOpUtils;
import lombok.Data;

import java.util.Map;
import java.util.Set;

@Data
public class Student extends OrdinaryUser {
    private String seatNumber;
    private int balance;

    public Student() {}

    public Student(String id, String name, String email, String password, OrdinaryUserType type, boolean gender, String seatNumber, int balance) {
        super(id, name, email, password, type, gender);
        this.seatNumber = seatNumber;
        this.balance = balance;
    }

    public Request requestFinancialAid(Map<String, Request> requestsTableData) {
        Request request = new Request(this, RequestType.FINANCIAL_AID);
        DataStore.saveRequest(requestsTableData, request);
        return request;
    }

    @Override
    public boolean registerInCourse(Map<String, Set<String>> userPerCoursesTableData, Map<String, Course> coursesTableData, Map<String, User> usersTableData, String courseId) {
        //Do some important checks
        // check whether the user is already in the course or not
        // check the threshold of the course
        // check the balance of the user
        Course course = coursesTableData.get(courseId);
        System.out.println("Student ID: " + this.getId());

        if(userPerCoursesTableData.get(this.getEmail()) != null && userPerCoursesTableData.get(this.getEmail()).contains(courseId)) {
            System.out.println("You are already enrolled in the course, you cannot enroll twice.");
            return false;
        }

        if(course.getMaxStudents() == course.getStudentEmails().size()) {
            System.out.println("Course has reached its threshold, you cannot enroll.");
            return false;
        }

        if(this.getBalance() < course.getPrice()) {
            System.out.println("Your balance is smaller than the amount you should pay.");
            return false;
        }

        this.setBalance(this.getBalance() - course.getPrice());
        coursesTableData.get(courseId).getStudentEmails().add(this.getEmail());

        DataStore.saveUser(usersTableData, this);
        DataStore.saveCourse(coursesTableData);

        return true;
    }

    @Override
    public String toString() {
        return String.format("[ID = %s, Name = %s, Email = %s, Password = %s, Type = %s, Gender = %s, Seat Number = %s, Balance = %d]", this.getId(), this.getName(), this.getEmail(), this.getPassword(), this.getType(), this.isGender(), this.getSeatNumber(), this.getBalance());
    }

    public String studentDetails() {
        return String.format("[ID = %s, Name = %s, Email = %s, Type = %s, Gender = %s, Seat Number = %s]", this.getId(), this.getName(), this.getEmail(), this.getType(), this.isGender(), this.getSeatNumber());
    }
}
