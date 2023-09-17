import java.util.*;
import java.util.stream.Collectors;

public class TeacherService {
    private static TeacherService teacherService = new TeacherService();

    private Map<String, User> usersTableData;
    private Map<String, Course> coursesTableData;
    private Map<String, Set<String>> usersPerCoursesTableData;
    private Teacher loggedInTeacher;
    private DataStorage dataStorage;
    private ResourcesExistence resources;

    private TeacherService() {
        dataStorage = DataStorage.getInstance();
        resources = ResourcesExistence.getInstance();
    }

    public static TeacherService getInstance() {
        return teacherService;
    }

    public void setLoggedInTeacher(Teacher loggedInTeacher) {
        this.loggedInTeacher = loggedInTeacher;
    }

    public void setCoursesTableData(Map<String, Course> coursesTableData) {
        this.coursesTableData = coursesTableData;
    }

    public void setUsersTableData(Map<String, User> usersTableData) {
        this.usersTableData = usersTableData;
    }

    public void setUsersPerCoursesTableData(Map<String, Set<String>> usersPerCoursesTableData) {
        this.usersPerCoursesTableData = usersPerCoursesTableData;
    }

    private boolean confirmContinuity() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Do you want to make other operations (Y/N)? ");
        char confirm = scanner.next().charAt(0);

        return confirm == 'Y' || confirm == 'y';
    }

    private int getTeacherCommandInput() {
        System.out.println("1. Do you want to show the available courses?");
        System.out.println("2. Do you want to show the courses you are registered in?");
        System.out.println("3. Do you want to register in course?");
        System.out.println("4. Do you want to view the student details who registered on your courses?");
        System.out.println("5. Do you want to withdraw from a course?");
        System.out.println("6. Go back");
        System.out.print("[Choose 1, 2, 3, 4, 5 or 6]: ");

        Scanner scanner = new Scanner(System.in);
        int teacherCommandType = scanner.nextInt();

        return teacherCommandType;
    }

    public void showTeacherPanel() {
        while(true) {
            int teacherCommandInput = getTeacherCommandInput();

            switch(teacherCommandInput) {
                case 1:
                    showAvailableCourses();
                    break;

                case 2:
                    showTeacherCourses();
                    break;

                case 3:
                    handleTeacherCourseRegistration();
                    break;

                case 4:
                    showAllStudentsOfTeacher();
                    break;

                case 5:
                    handleTeacherWithdrawingCourse();
                    break;

                case 6:
                    break;

                default:
                    System.out.println("Please choose the correct number.");
            }

            if(confirmContinuity() == false) {
                break;
            }
        }
    }

    private void showAvailableCourses() {
        if (resources.checkCoursesExistence(coursesTableData) == false) {
            System.out.println("There are no courses.");
            return;
        }

        List<Course> matchingCourses = filterCoursesOnCriteria();
        if(matchingCourses == null || matchingCourses.size() == 0) {
            System.out.println("There are no courses matching your specialization.");
            return;
        }

        System.out.println("These are the courses that match your specialization:");
        matchingCourses.forEach(course -> System.out.println(course.toString()));
    }

    private List<Course> filterCoursesOnCriteria() {
        String loggedInTeacherSpecialization = loggedInTeacher.getSpecialization();

        return coursesTableData
                .values()
                .stream()
                .filter(course -> course.getSpecialization().equals(loggedInTeacherSpecialization))
                .collect(Collectors.toList());
    }

    private void showTeacherCourses() {
        if(resources.checkCoursesExistence(coursesTableData) == false) {
            System.out.println("There are no courses");
            return;
        }

        Set<String> teacherCourses = usersPerCoursesTableData.get(loggedInTeacher.getEmail());

        if(teacherCourses == null || teacherCourses.size() == 0) {
            System.out.println("You are not registered in any course.");
            return;
        }

        System.out.print("These are the courses you are registered in: ");
        usersPerCoursesTableData.get(loggedInTeacher.getEmail()).forEach(courseId -> {
            System.out.print(courseId + " ");
        });

        System.out.println();
    }

    private void handleTeacherCourseRegistration() {
        showAvailableCourses();

        System.out.print("Enter the course id you want to register in (or cancel operation by choosing x): ");
        Scanner scanner = new Scanner(System.in);
        String courseId = scanner.nextLine();

        if(courseId.equals("x")) {
            return;
        }

        if(coursesTableData.containsKey(courseId) == false) {
            System.out.println("Course id you entered is not exist, enter an existent one please.");
            return;
        }

        Course course = coursesTableData.get(courseId);
        List<String> teachersOnCourse = course.getTeacherEmails();
        Set<String> teacherCourses = usersPerCoursesTableData.get(loggedInTeacher.getId());

        if(teacherCourses != null && teacherCourses.size() == 3) {
            System.out.println("You reached the threshold, you cannot be assigned to that course.");
            return;
        }

        if(teachersOnCourse != null && teachersOnCourse.size() > 0 && teachersOnCourse.get(0).equals(loggedInTeacher.getEmail())) {
            System.out.println("You are already registered on that course, you cannot register on the same course twice.");
            return;
        }

        if(teachersOnCourse != null && teachersOnCourse.size() > 0) {
            System.out.println("This course is already assigned to another teacher, course cannot have more than one teacher.");
            return;
        }

        course.getTeacherEmails().add(loggedInTeacher.getEmail());

        dataStorage.saveCourse(coursesTableData);

        if(usersPerCoursesTableData.get(loggedInTeacher.getEmail()) == null) {
            Set<String> courseIds = new HashSet<>();
            courseIds.add(courseId);
            usersPerCoursesTableData.put(loggedInTeacher.getEmail(), courseIds);
        }
        else {
            usersPerCoursesTableData.get(loggedInTeacher.getEmail()).add(courseId);
        }

        System.out.println("You have been registered to this course successfully");
    }

    private void showAllStudentsOfTeacher() {
        if (resources.checkCoursesExistence(coursesTableData) == false) {
            System.out.println("There are no courses.");
            return;
        }

        Set<String> teacherCourses = usersPerCoursesTableData.get(loggedInTeacher.getEmail());

        if(teacherCourses == null || teacherCourses.size() == 0) {
            System.out.println("You are not registered on any course.");
            return;
        }

        System.out.println("Students registered on your courses: ");

        for(String courseId: teacherCourses) {
            Course course = coursesTableData.get(courseId);
            List<String> studentsOnCourse = course.getStudentEmails();
            if(studentsOnCourse == null || studentsOnCourse.size() == 0) {
                System.out.println(courseId + ": doesn't have any student.");
                System.out.println("==================");
                continue;
            }

            System.out.println(courseId + ": ");
            for(String studentEmail: course.getStudentEmails()) {
                Student user = (Student) usersTableData.get(studentEmail);
                System.out.println(user.studentDetails());
            }
            System.out.println("==================");
        }
    }

    private void handleTeacherWithdrawingCourse() {
        // TO BE HANDELED
//        if(isSemesterRunning) {
//            System.out.println("You cannot withdraw from a course while the semester is running.");
//            return;
//        }

        // TO BE HANDELED
        showTeacherCourses();

        System.out.print("Enter the course id you want to withdraw from (or cancel operation by choosing x): ");
        Scanner scanner = new Scanner(System.in);
        String courseId = scanner.nextLine();

        if(courseId.equals("x")) {
            return;
        }

        if(coursesTableData.containsKey(courseId) == false) {
            System.out.println("Course ID you entered is not exist, please enter an existent one.");
            return;
        }

        if(usersPerCoursesTableData.get(loggedInTeacher.getEmail()).contains(courseId) == false) {
            System.out.println("You are not assigned on that course.");
            return;
        }

//        if (loggedInTeacher.withdrawFromCourse(course, usersPerCoursesTableData) == false) {
//            return;
//        }

        Course course = coursesTableData.get(courseId);
        List<String> teacherEmails = course.getTeacherEmails();

        if(teacherEmails == null || teacherEmails.size() == 0) {
            System.out.println("This course doesn't have teacher.");
            return;
        }

        String teacherEmail = teacherEmails.get(0);

        usersPerCoursesTableData.get(teacherEmail).remove(courseId);
        course.getTeacherEmails().clear();

        List<String> studentEmails = course.getStudentEmails();

        if(studentEmails != null && studentEmails.size() > 0) {
            refundStudents(studentEmails, course.getPrice());
            course.getStudentEmails().clear();

            removeCourseFromStudentsPerCourse(studentEmails, courseId);

            dataStorage.saveUser(usersTableData);

            System.out.println("All students registered on that course have been refunded and removed from the course.");
        }

        System.out.println("Withdraw operation is completed.");
        dataStorage.saveCourse(coursesTableData);
    }

    private void refundStudents(List<String> studentsToBeRefund, int coursePrice) {
        for(String studentEmail: studentsToBeRefund) {
            Student student = (Student) usersTableData.get(studentEmail);
            student.setBalance(student.getBalance() + coursePrice);
        }
    }

    private void removeCourseFromStudentsPerCourse(List<String> studentEmails, String courseIdToBeRemoved) {
        for(String studentEmail: studentEmails) {
            usersPerCoursesTableData.get(studentEmail).remove(courseIdToBeRemoved);
        }
    }
}
