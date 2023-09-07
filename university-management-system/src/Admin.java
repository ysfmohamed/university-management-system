import java.util.List;
import java.util.Map;

public class Admin extends User {
    @Override
    public boolean signup(User user) {
        throw new UnsupportedOperationException("Admin cannot create an account");
    }

    // TODO
    public static void accept(Map<String, User> oldUsersTableData, Map<String, Request> oldRequestTableData, Request userRequest) {
        if (DataStore.saveUser(oldUsersTableData, userRequest.getUser())) {
            System.out.println("User with \"" + userRequest.getUser().getEmail() + "\" email has been added successfully to the users file.");

            if(DataStore.deleteRequest(oldRequestTableData, userRequest)) {
                System.out.println("User request with \"" + userRequest.getUser().getEmail() + "\" email has been deleted from the request file.");
            }
            else {
                System.out.println("Error happened while deleting user request with \"" + userRequest.getUser().getEmail() + "\" email from the requests file.");
            }
        }
        else {
            System.out.println("Error happened while adding user with \"" + userRequest.getUser().getEmail() + "\" email to the users file.");
        }
    }

    public static void reject(Map<String, Request> oldRequestTableData, Request userRequest) {
        if(DataStore.deleteRequest(oldRequestTableData, userRequest)) {
            System.out.println("User request with email + \"" +  userRequest.getUser().getEmail() + "\" has been rejected and deleted from the requests file.");
        }
        else {
            System.out.println("Error happened while deleting user request with \"" + userRequest.getUser().getEmail() + "\" email from the requests file.");
        }
    }

    public static void addCourse(Map<String, Course> oldCoursesTableData, Course newCourse) {
        if (DataStore.saveCourse(oldCoursesTableData, newCourse)) {
            System.out.println(newCourse.getName() + " course has been added to the courses file successfully.");
        }
        else {
            System.out.println("Error happened while adding " + newCourse.getName() + " course to the courses file.");
        }
    }

    public static void modifyCourse(Map<String, Course> oldCoursesTableData, Course newCourse) {
        if (DataStore.saveCourse(oldCoursesTableData, newCourse)) {
            System.out.println(newCourse.getName() + " course has been updated successfully.");
        }
        else {
            System.out.println("Error happened while updating " + newCourse.getName() + " course to the courses file.");
        }
    }
}
