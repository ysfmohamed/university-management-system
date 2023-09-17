import java.util.*;

public class UMSEngine {
    private static UMSEngine umsEngine = new UMSEngine();
    private Map<String, Request> requestsTableData;
    private Map<String, User> usersTableData;
    private Map<String, Course> coursesTableData;
    private Map<String, Set<String>> usersPerCoursesTableData;
    private User loggedInUser;
    private Student loggedInStudent;
    private Teacher loggedInTeacher;

    private DataStorage dataStorage;

    private UMSEngine() {}

    public static UMSEngine getInstance() {
        return umsEngine;
    }

    public void loadDataToMemory() {
        dataStorage = DataStorage.getInstance();

        requestsTableData = dataStorage.buildRequestsData();
        usersTableData = dataStorage.buildUsersData();
        coursesTableData = dataStorage.buildCoursesData();
        usersPerCoursesTableData = dataStorage.buildUserPerCourses();
    }

    public void run() {
        System.out.println("\tUniversity Management System\t");

        loadDataToMemory();

        RegistrationService registrationService = RegistrationService.getInstance();
        registrationService.setUsersTableData(usersTableData);
        registrationService.setRequestsTableData(requestsTableData);

        while(true) {
            int authType = getAuthTypeInput();

            switch(authType) {
                case 1:
                    System.out.println("Please log in");
                    handleLogin();
                    break;

                case 2:
                    System.out.println("Please sign up");
                    registrationService.handleSignUp();
                    break;

                case 3:
                    break;

                default:
                    System.out.println("Please make sure to choose the correct number.");
            }

            if(confirmAuthContinuity() == false) {
                break;
            }
        }
    }

    private int getAuthTypeInput() {
        System.out.println("1. Log in");
        System.out.println("2. Sign up");
        System.out.println("3. Close");
        System.out.print("[Choose 1, 2 or 3]: ");

        Scanner scanner = new Scanner(System.in);
        int authType = scanner.nextInt();

        return authType;
    }

    private void handleLogin() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter your email: ");
        String givenEmail = scanner.nextLine();

        System.out.print("Enter your password: ");
        String givenPassword = scanner.nextLine();

        if(givenEmail.equals("admin@gmail.com") && givenPassword.equals("admin")) {
            AdminService adminService = AdminService.getInstance();
            adminService.setUsersTableData(usersTableData);
            adminService.setCoursesTableData(coursesTableData);
            adminService.setRequestsTableData(requestsTableData);
            adminService.setUsersPerCoursesTableData(usersPerCoursesTableData);

            adminService.showAdminPanel();
        }

        else if(usersTableData.containsKey(givenEmail) && usersTableData.get(givenEmail).getPassword().equals(givenPassword)) {
            loggedInUser = usersTableData.get(givenEmail);
            checkTheInstanceOfUser();

        }
        else {
            System.out.println("Email or Password is incorrect.");
            return;
        }
    }

    private void checkTheInstanceOfUser() {
        if(loggedInUser instanceof Student) {
            loggedInStudent = (Student) loggedInUser;
            System.out.println(loggedInStudent.toString());

            StudentService studentService = StudentService.getInstance();
            studentService.setLoggedInStudent(loggedInStudent);
            studentService.setUsersTableData(usersTableData);
            studentService.setCoursesTableData(coursesTableData);
            studentService.setUsersPerCoursesTableData(usersPerCoursesTableData);
            studentService.setRequestsTableData(requestsTableData);

            studentService.showStudentPanel();
        }
        else {
            loggedInTeacher = (Teacher) loggedInUser;
            System.out.println(loggedInTeacher.toString());

            TeacherService teacherService = TeacherService.getInstance();
            teacherService.setLoggedInTeacher(loggedInTeacher);
            teacherService.setUsersTableData(usersTableData);
            teacherService.setCoursesTableData(coursesTableData);
            teacherService.setUsersPerCoursesTableData(usersPerCoursesTableData);

            teacherService.showTeacherPanel();
        }
    }

    private boolean confirmAuthContinuity() {
        System.out.print("Do you want to authenticate again (Y/N)? ");
        Scanner scanner = new Scanner(System.in);
        char confirm = scanner.next().charAt(0);

        return confirm == 'Y' || confirm == 'y';
    }
}
