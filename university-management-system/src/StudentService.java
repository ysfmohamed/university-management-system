import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class StudentService {
    private static StudentService studentService = new StudentService();

    private Map<String, User> usersTableData;
    private Map<String, Course> coursesTableData;
    private Map<String, Set<String>> usersPerCoursesTableData;
    private Map<String, Request> requestsTableData;
    private Student loggedInStudent;
    private DataStorage dataStorage;

    private StudentService() {
        dataStorage = DataStorage.getInstance();
    }

    public static StudentService getInstance() {
        return studentService;
    }

    public void setLoggedInStudent(Student loggedInStudent) {
        this.loggedInStudent = loggedInStudent;
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

    public void setRequestsTableData(Map<String, Request> requestsTableData) {
        this.requestsTableData = requestsTableData;
    }

    private int getStudentCommandInput() {
        System.out.println("1. Do you want to register in course?");
        System.out.println("2. Do you want to show the courses you are enrolled in?");
        System.out.println("3. Do you want to request a financial aid?");
        System.out.println("4. Do you want to show you grades?"); //TODO
        System.out.println("5. Do you want to filter grades of a past semester?"); // TODO
        System.out.println("6. Go back");
        System.out.print("[Choose between 1, 2, 3, 4, 5 or 6]: ");

        Scanner scanner = new Scanner(System.in);
        int studentCommandInput = scanner.nextInt();

        return studentCommandInput;
    }

    private boolean confirmContinuity() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Do you want to make other operations (Y/N)? ");
        char confirm = scanner.next().charAt(0);

        return confirm == 'Y' || confirm == 'y';
    }

    public void showStudentPanel() {
        while(true) {
            int studentCommandInput = getStudentCommandInput();

            switch(studentCommandInput) {
                case 1:
                    handleStudentCourseRegistration();
                    break;

                case 2:
                    showStudentCourses();
                    break;

                case 3:
                    requestFinancialAid();
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

    private void showAllCourses() {
        System.out.println("Current Courses: ");

        int i = 1;
        for(Course course: coursesTableData.values()) {
            System.out.println(i + ". " + course.toString());
            i++;
        }
    }

    private void handleStudentCourseRegistration() {
        showAllCourses();

        System.out.print("Enter the course id you want to register in (or cancel operation by choosing x): ");
        Scanner scanner = new Scanner(System.in);
        String courseId = scanner.nextLine();

        if(courseId.equals("x")) {
            return;
        }

        if(coursesTableData.containsKey(courseId) == false) {
            System.out.println("Course ID you entered is not exist, enter an existent one please.");
            return;
        }

        if(usersPerCoursesTableData.get(loggedInStudent.getEmail()) != null && usersPerCoursesTableData.get(loggedInStudent.getEmail()).contains(courseId)) {
            System.out.println("You are already enrolled in the course, you cannot enroll twice.");
            return;
        }

        Course course = coursesTableData.get(courseId);

        if(course.getMaxStudents() == course.getStudentEmails().size()) {
            System.out.println("Course has reached its threshold, you cannot enroll.");
            return;
        }

        if(loggedInStudent.getBalance() < course.getPrice()) {
            System.out.println("Your balance is smaller than the amount you should pay.");
            return;
        }

        loggedInStudent.setBalance(loggedInStudent.getBalance() - course.getPrice());
        coursesTableData.get(courseId).getStudentEmails().add(loggedInStudent.getEmail());

        dataStorage.saveUser(usersTableData, loggedInStudent);
        dataStorage.saveCourse(coursesTableData);

        if(usersPerCoursesTableData.get(loggedInStudent.getEmail()) == null) {
            Set<String> courseIds = new HashSet<>();
            courseIds.add(courseId);
            usersPerCoursesTableData.put(loggedInStudent.getEmail(), courseIds);
        }
        else {
            usersPerCoursesTableData.get(loggedInStudent.getEmail()).add(courseId);
        }
        System.out.println("You have been registered to the course successfully");
    }

    private void requestFinancialAid() {
        Request currentReq = requestsTableData.get(loggedInStudent.getEmail());

        if(currentReq != null && currentReq.getType().equals(RequestType.FINANCIAL_AID) == false) {
            System.out.println("You are not authorized to request a financial aid. Make an account first");
            return;
        }

        else if(currentReq != null && currentReq.getType().equals(RequestType.FINANCIAL_AID)) {
            System.out.println("You already asked for financial aid, you cannot ask for financial aid twice.");
            return;
        }

        Request requestedFinancialAid = new Request(loggedInStudent, RequestType.FINANCIAL_AID);
        dataStorage.saveRequest(requestsTableData, requestedFinancialAid);

        if (requestedFinancialAid != null) {
            requestsTableData.put(loggedInStudent.getEmail(), requestedFinancialAid);
            System.out.println("Your financial aid request is sent and being reviewed by the Admin.");
        }

        else {
            System.out.println("Error happened while submitting your financial aid request.");
        }
    }

    private void showStudentCourses() {
        Set<String> studentCourses = usersPerCoursesTableData.get(loggedInStudent.getEmail());

        if(studentCourses == null || studentCourses.size() == 0) {
            System.out.println("You are not enrolled in any course.");
            return;
        }

        System.out.print("You are enrolled in the following courses: ");
        studentCourses.forEach(courseId -> {
            System.out.print(courseId + ", ");
        });

        System.out.println();
    }
}
