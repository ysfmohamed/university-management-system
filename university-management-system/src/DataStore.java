import java.util.*;
import java.util.concurrent.*;

// TODO removing all boolean return
public class DataStore {
    private static ScheduledExecutorService executor = Executors.newScheduledThreadPool(6);
    public static Map<String, Request> buildRequestsData() {
        List<Request> requests = FileHandler.readRequestsFile();

        Map<String, Request> requestsTable = new HashMap<>();

        for(Request request: requests) {
            requestsTable.put(request.getUser().getEmail(), request);
        }

        return requestsTable;
    }

    public static Map<String, User> buildUsersData() {
        List<User> users =  FileHandler.readUsersFile();
        Map<String, User> usersTable = new HashMap<>();

        for(User user: users) {
            usersTable.put(user.getEmail(), user);
        }

        return usersTable;
    }

    public static Map<String, Course> buildCoursesData() {
        List<Course> courses = FileHandler.readCoursesFile();
        Map<String, Course> coursesTable = new HashMap<>();

        for(Course course: courses) {
            coursesTable.put(course.getId(), course);
        }

        return coursesTable;
    }

    public static boolean saveRequest(Map<String, Request> oldRequestsTableData, Request requestToBeAdded) {
        executor.schedule(() -> FileHandler.saveRequest(oldRequestsTableData, requestToBeAdded), 10, TimeUnit.SECONDS);
        return true;
//        return FileHandler.saveRequest(oldRequestsTableData, requestToBeAdded);
    }

    public static boolean deleteRequest(Map<String, Request> oldRequestsTableData, Request requestToBeDeleted) {
        executor.schedule(() -> FileHandler.deleteRequest(oldRequestsTableData, requestToBeDeleted), 10, TimeUnit.SECONDS);
        return true;
//        return FileHandler.deleteRequest(oldRequestsTableData, requestToBeDeleted);
    }

    public static boolean saveCourse(Map<String, Course> oldCoursesTableData, Course courseToBeSaved) {
        Future<Boolean> bol = executor.schedule(() -> FileHandler.saveCourse(oldCoursesTableData, courseToBeSaved), 10, TimeUnit.SECONDS);
        return true;
//        return FileHandler.saveCourse(oldCoursesTableData, courseToBeSaved);
    }

    public static boolean saveCourse(Map<String, Course> oldCoursesTableData) {
        executor.schedule(() -> FileHandler.saveCourse(oldCoursesTableData), 10, TimeUnit.SECONDS);
        return true;
//        return FileHandler.saveCourse(oldCoursesTableData);
    }

    public static boolean saveUser(Map<String, User> oldUsersTableData, User userToBeSaved) {
        executor.schedule(() -> FileHandler.saveUser(oldUsersTableData, userToBeSaved), 10, TimeUnit.SECONDS);
        return true;
//        return FileHandler.saveUser(oldUsersTableData, userToBeSaved);
    }

    public static boolean saveUser(Map<String, User> oldUsersTableData) {
        executor.schedule(() -> FileHandler.saveUser(oldUsersTableData), 10, TimeUnit.SECONDS);
        return true;
//        return FileHandler.saveUser(oldUsersTableData);
    }

    public static Map<String, Set<String>> buildUserPerCourses() {
        List<Course> courses = FileHandler.readCoursesFile();

        Map<String, Set<String>> userPerCoursesTable = new HashMap<>();

        // loop over each course element in the courses array list.
        for(Course course: courses) {
            // loop over each studentId element in the studentIds list.
            for(String studentEmail: course.getStudentEmails()) {
                buildStudentPerCourses(userPerCoursesTable, studentEmail, course.getId());
            }

            // loop over each teacherId element in the teacherIds list.
            for(String teacherEmail: course.getTeacherEmails()) {
                buildTeacherPerCourses(userPerCoursesTable, teacherEmail, course.getId());
            }
        }

        return userPerCoursesTable;
    }

    private static void buildStudentPerCourses(Map<String, Set<String>> userPerCoursesTable, String studentEmail, String courseId) {
        if(!userPerCoursesTable.containsKey(studentEmail)) {
            Set<String> coursesIds = new HashSet<>();
            coursesIds.add(courseId);
            userPerCoursesTable.put(studentEmail, coursesIds);
        }
        else {
            userPerCoursesTable.get(studentEmail).add(courseId);
        }
    }

    private static void buildTeacherPerCourses(Map<String, Set<String>> userPerCoursesTable, String teacherEmail, String courseId) {
        if(!userPerCoursesTable.containsKey(teacherEmail)) {
            Set<String> coursesIds = new HashSet<>();
            coursesIds.add(courseId);
            userPerCoursesTable.put(teacherEmail, coursesIds);
        }
        else {
            userPerCoursesTable.get(teacherEmail).add(courseId);
        }
    }
}
