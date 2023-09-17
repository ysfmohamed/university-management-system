import java.util.*;
import java.util.concurrent.*;

public class DataStorage {
    private static DataStorage dataStorage = new DataStorage();
    private FileHandler fileHandler;

    private ScheduledExecutorService executor = Executors.newScheduledThreadPool(6);

    private DataStorage() {
        fileHandler = FileHandler.getInstance();
    }

    public static DataStorage getInstance() {
        return dataStorage;
    }

    public Map<String, Request> buildRequestsData() {
        List<Request> requests = fileHandler.readRequestsFile();

        Map<String, Request> requestsTable = new HashMap<>();

        for(Request request: requests) {
            requestsTable.put(request.getUser().getEmail(), request);
        }

        return requestsTable;
    }

    public Map<String, User> buildUsersData() {
        List<User> users =  fileHandler.readUsersFile();
        Map<String, User> usersTable = new HashMap<>();

        for(User user: users) {
            usersTable.put(user.getEmail(), user);
        }

        return usersTable;
    }

    public Map<String, Course> buildCoursesData() {
        List<Course> courses = fileHandler.readCoursesFile();
        Map<String, Course> coursesTable = new HashMap<>();

        for(Course course: courses) {
            coursesTable.put(course.getId(), course);
        }

        return coursesTable;
    }

    public void saveRequest(Map<String, Request> oldRequestsTableData, Request requestToBeAdded) {
//        executor.schedule(() -> FileHandler.saveRequest(oldRequestsTableData, requestToBeAdded),10, TimeUnit.SECONDS);
//        return true;
        fileHandler.saveRequest(oldRequestsTableData, requestToBeAdded);
    }

    public void deleteRequest(Map<String, Request> oldRequestsTableData, Request requestToBeDeleted) {
//        executor.schedule(() -> FileHandler.deleteRequest(oldRequestsTableData, requestToBeDeleted), 10, TimeUnit.SECONDS);
//        return true;
        fileHandler.deleteRequest(oldRequestsTableData, requestToBeDeleted);
    }

    public void saveCourse(Map<String, Course> oldCoursesTableData, Course courseToBeSaved) {
//        executor.schedule(() -> FileHandler.saveCourse(oldCoursesTableData, courseToBeSaved), 10, TimeUnit.SECONDS);
//        return true;
        fileHandler.saveCourse(oldCoursesTableData, courseToBeSaved);
    }

    public void saveCourse(Map<String, Course> oldCoursesTableData) {
//        executor.schedule(() -> FileHandler.saveCourse(oldCoursesTableData), 10, TimeUnit.SECONDS);
//        return true;
        fileHandler.saveCourse(oldCoursesTableData);
    }

    public void saveUser(Map<String, User> oldUsersTableData, User userToBeSaved) {
//        executor.schedule(() -> FileHandler.saveUser(oldUsersTableData, userToBeSaved), 10, TimeUnit.SECONDS);
//        return true;
        fileHandler.saveUser(oldUsersTableData, userToBeSaved);
    }

    public void saveUser(Map<String, User> oldUsersTableData) {
//        executor.schedule(() -> FileHandler.saveUser(oldUsersTableData), 10, TimeUnit.SECONDS);
//        return true;
        fileHandler.saveUser(oldUsersTableData);
    }

    public Map<String, Set<String>> buildUserPerCourses() {
        List<Course> courses = fileHandler.readCoursesFile();

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

    private void buildStudentPerCourses(Map<String, Set<String>> userPerCoursesTable, String studentEmail, String courseId) {
        if(!userPerCoursesTable.containsKey(studentEmail)) {
            Set<String> coursesIds = new HashSet<>();
            coursesIds.add(courseId);
            userPerCoursesTable.put(studentEmail, coursesIds);
        }
        else {
            userPerCoursesTable.get(studentEmail).add(courseId);
        }
    }

    private void buildTeacherPerCourses(Map<String, Set<String>> userPerCoursesTable, String teacherEmail, String courseId) {
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
