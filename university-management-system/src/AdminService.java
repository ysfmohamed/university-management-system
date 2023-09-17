import java.util.*;
import java.util.stream.Collectors;

public class AdminService {
    private static AdminService adminService = new AdminService();
    private boolean isSemesterRunning = false;
    private Map<String, Request> requestsTableData;
    private Map<String, User> usersTableData;
    private Map<String, Course> coursesTableData;

    private Map<String, Set<String>> usersPerCoursesTableData;

    private boolean isCourseModified = false;

    private DataStorage dataStorage;
    private ResourcesExistence resources;

    private AdminService() {
        dataStorage = DataStorage.getInstance();
        resources = ResourcesExistence.getInstance();
    }

    public static AdminService getInstance() {
        return adminService;
    }

    public void setCoursesTableData(Map<String, Course> coursesTableData) {
        this.coursesTableData = coursesTableData;
    }

    public void setUsersTableData(Map<String, User> usersTableData) {
        this.usersTableData = usersTableData;
    }

    public void setRequestsTableData(Map<String, Request> requestsTableData) {
        this.requestsTableData = requestsTableData;
    }

    public void setUsersPerCoursesTableData(Map<String, Set<String>> usersPerCoursesTableData) {
        this.usersPerCoursesTableData = usersPerCoursesTableData;
    }

    private int getAdminCommandInput() {
        System.out.println("\tWelcome Admin\t");
        System.out.println("1. Start semester");
        System.out.println("2. Pause semester");
        System.out.println("3. Show Current Requests");
        System.out.println("4. Add Course");
        System.out.println("5. Modify Course Info");
        System.out.println("6. Modify Account Info");
        System.out.println("7. Go back");
        System.out.print("[Choose 1, 2, 3, 4, 5, 6 or 7]: ");

        Scanner scanner = new Scanner(System.in);
        int adminBehaviorType = scanner.nextInt();

        return adminBehaviorType;
    }

    private boolean confirmContinuity() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Do you want to make other operations (Y/N)? ");
        char confirm = scanner.next().charAt(0);

        return confirm == 'Y' || confirm == 'y';
    }

    public void showAdminPanel() {

        while(true) {
            int adminCommandType = getAdminCommandInput();

            switch (adminCommandType) {
                case 1:
                    System.out.println("Start semester");
                    //handleStartingSemester(); // TODO
                    break;

                case 2:
                    System.out.println("Resuming semester");
                    //handleResumingSemester(); // TODO
                    break;

                case 3:
                    System.out.println("Show Current Requests");
                    handleCurrentRequests();
                    break;

                case 4:
                    System.out.println("Add Course");
                    handleAddingCourses();
                    break;

                case 5:
                    System.out.println("Modify Courses Info");
                    handleModifyingCourse();
                    break;

                case 6:
                    System.out.println("Modify Account Info");
                    handleModifyingAccountInfo();
                    break;

                case 7:
                    break;

                default:
                    System.out.println("Please choose the correct number.");
            }

            if(confirmContinuity() == false) {
                break;
            }
        }
    }

    private void handleAddingCourses() {
        System.out.println("Please enter the information of the new course");

        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter Course ID: ");
        String courseId = scanner.nextLine();

        System.out.print("Enter Course Name: ");
        String courseName = scanner.nextLine();

        System.out.print("Enter Course Specialization: ");
        String courseSpecialization = scanner.nextLine();

        System.out.print("Enter Course Credit Hours: ");
        int courseCreditHours = scanner.nextInt();
        int coursePrice = courseCreditHours * 1150;

        System.out.print("Enter Course Max Students: ");
        int courseMaxStudents = scanner.nextInt();

        System.out.println();

        Course newCourse = new Course(courseId, courseName, courseCreditHours, courseMaxStudents, coursePrice, courseSpecialization, new ArrayList<String>(), new ArrayList<String>());

        dataStorage.saveCourse(coursesTableData, newCourse);
        coursesTableData.put(newCourse.getId(), newCourse);

        System.out.println(newCourse.getName() + " course has been added to the courses file successfully.");
    }

    private String getTheCourseToBeModified() {
        System.out.print("Please enter the course id you want to modify or (cancel by choosing x): ");
        Scanner scanner = new Scanner(System.in);
        String courseIdToBeModified = scanner.nextLine();

        return courseIdToBeModified;
    }

    private int getAdminModificationInput() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("What do you want to modify? ");
        System.out.println("1. Modify name? ");
        System.out.println("2. Modify Credit Hours? ");
        System.out.println("3. Modify Max Students? ");
        System.out.println("4. Modify Specialization? ");
        System.out.println("5. Go back");
        System.out.print("[Choose 1, 2, 3, 4, or 5]: ");
        int modify = scanner.nextInt();

        return modify;
    }

    private boolean confirmModificationContinuity() {
        System.out.print("Do you want to make another modification (Y/N)? ");
        Scanner scanner = new Scanner(System.in);
        char confirm = scanner.next().charAt(0);

        return confirm =='Y' || confirm =='y';
    }

    private void handleModifyingCourse() {
        if(isSemesterRunning) {
            System.out.println("You cannot modify course while the semester is running.");
            return;
        }

        if (resources.checkCoursesExistence(coursesTableData) == false) {
            System.out.println("There are no courses.");
            return;
        }

        showAllCourses();

        String courseId = getTheCourseToBeModified();
        if(courseId.equals("x")) {
            return;
        }

        Course courseToBeModified = coursesTableData.get(courseId);

        if(courseToBeModified == null) {
            System.out.println("The course id you entered is not exist. ");
            return;
        }

        Scanner scanner = new Scanner(System.in);

        while(true) {
            int modify = getAdminModificationInput();

            switch(modify) {
                case 1:
                    System.out.print("Enter new course name value: ");
                    String newCourseName = scanner.nextLine();
                    courseToBeModified.setName(newCourseName);
                    isCourseModified = true;
                    break;

                case 2:
                    System.out.print("Enter new course credit hours value: ");
                    int newCourseCreditHours = scanner.nextInt();
                    scanner.nextLine();
                    courseToBeModified.setCreditHours(newCourseCreditHours);
                    courseToBeModified.setPrice(newCourseCreditHours * 1150);
                    isCourseModified = true;
                    break;

                case 3:
                    System.out.print("Enter new course max students value: ");
                    int newCourseMaxStudents = scanner.nextInt();
                    scanner.nextLine();
                    courseToBeModified.setMaxStudents(newCourseMaxStudents);
                    isCourseModified = true;
                    break;

                case 4:
                    System.out.print("Enter new course specialization value: ");
                    String newCourseSpecialization = scanner.nextLine();
                    courseToBeModified.setSpecialization(newCourseSpecialization);
                    isCourseModified = true;
                    break;

                case 5:
                    break;

                default:
                    System.out.println("Please choose the correct number.");
            }

            if (confirmModificationContinuity() == false) {
                if(isCourseModified) {
                    dataStorage.saveCourse(coursesTableData, courseToBeModified);
                    isCourseModified = false;
                }
                break;
            }
        }
    }

    // SHOW
    private void showRegistrationRequests(List<Request> requests) {
        requests.forEach(request -> System.out.println(request.toString()));
    }

    private List<Request> getFinancialAidRequest() {
        return requestsTableData
                .values()
                .stream()
                .filter(request -> request.getType().equals(RequestType.FINANCIAL_AID))
                .collect(Collectors.toList());
    }

    private void showFinancialAidRequests(List<Request> requests) {
        requests.forEach(request -> System.out.println(request.toString()));
    }

    private int getShowRequestTypeInput() {
        System.out.println("1. Do you want to see the registration requests?");
        System.out.println("2. Do you want to see the financial aid requests?");
        System.out.println("3. Go back");
        System.out.print("[Choose 1, 2 or 3]: ");
        Scanner scanner = new Scanner(System.in);
        int requestType = scanner.nextInt();

        return requestType;
    }

    private void handleFinancialAidRequests() {
        List<Request> requests = getFinancialAidRequest();
        if(requests == null || requests.size() == 0) {
            System.out.println("There are no financial aid requests.");
            return;
        }

        showFinancialAidRequests(requests);

        System.out.print("Enter user's email you want to handle his/her financial aid request (or cancel the operation by choosing x): ");
        Scanner scanner = new Scanner(System.in);
        String userEmail = scanner.nextLine();

        if(userEmail.equals("x")) {
            return;
        }

        Request userRequest = requestsTableData.get(userEmail);

        if(userRequest == null) {
            System.out.println("This user doesn't exist, please choose user from the list above.");
            return;
        }

        System.out.print("Do you want to accept his/her financial aid (Y/N)? ");
        char confirm = scanner.next().charAt(0);

        if(confirm == 'Y' || confirm == 'y') {
            Student student = (Student) usersTableData.get(userEmail);
            student.setBalance(student.getBalance() + 3500);
            requestsTableData.remove(userEmail);
            dataStorage.saveUser(usersTableData, userRequest.getUser());
            dataStorage.deleteRequest(requestsTableData, userRequest);
            System.out.println("Financial aid is completed, and the student's balance increased by 3500.");
        }
        else {
            requestsTableData.remove(userEmail);
            dataStorage.deleteRequest(requestsTableData, userRequest);
        }
    }

    private List<Request> getRegistrationRequests() {
        return requestsTableData
                .values()
                .stream()
                .filter(request -> request.getType().equals(RequestType.USER_REGISTRATION))
                .collect(Collectors.toList());
    }

    private void handleRegistrationRequests() {
        List<Request> requests = getRegistrationRequests();

        if(requests == null || requests.size() == 0) {
            System.out.println("There are no registration requests.");
            return;
        }

        showRegistrationRequests(requests);

        // choose the user's email you want to handle
        System.out.print("Enter user's email you want to handle his/her registration request or (discard the operation by writing x): ");
        Scanner scanner = new Scanner(System.in);
        String userEmail = scanner.nextLine();

        if(userEmail.equals("x")) {
            return;
        }

        Request userRequest = requestsTableData.get(userEmail);

        if(userRequest == null) {
            System.out.println("This user doesn't exist, please choose user from the list above.");
            return;
        }

        System.out.print("Do you want to accept his/her registration account (Y/N)? ");
        char confirm = scanner.next().charAt(0);

        if(confirm == 'Y' || confirm == 'y') {
            dataStorage.saveUser(usersTableData, userRequest.getUser());
            dataStorage.deleteRequest(requestsTableData, userRequest);

            usersTableData.put(userEmail, userRequest.getUser());
            requestsTableData.remove(userEmail);

            System.out.println("User with \"" + userRequest.getUser().getEmail() + "\" email has been added successfully to the users file and removed from requests file.");
        }
        else {
            requestsTableData.remove(userEmail);
            dataStorage.deleteRequest(requestsTableData, userRequest);
            System.out.println("User request with email + \"" +  userRequest.getUser().getEmail() + "\" has been rejected and deleted from the requests file.");
        }
    }

    private void handleCurrentRequests() {
        while(true) {
            int showRequestType = getShowRequestTypeInput();

            switch(showRequestType) {
                case 1:
                    handleRegistrationRequests();
                    break;

                case 2:
                    handleFinancialAidRequests();
                    break;

                case 3:
                    break;

                default:
                    System.out.println("Please choose the correct number.");
            }

            if(confirmHandlingRequestContinuity() == false) {
                break;
            }
        }
    }

    private boolean confirmHandlingRequestContinuity() {
        System.out.print("Do you want to handle another request (Y/N)? ");
        Scanner scanner = new Scanner(System.in);
        int confirm = scanner.next().charAt(0);

        return confirm == 'Y' || confirm == 'y';
    }

    private void showAllCourses() {
        System.out.println("Current Courses: ");

        int i = 1;
        for(Course course: coursesTableData.values()) {
            System.out.println(i + ". " + course.toString());
            i++;
        }
    }

    private void handleModifyingAccountInfo() {
        if(isSemesterRunning) {
            System.out.println("You cannot modify account info while the semester is running.");
            return;
        }

        if(resources.checkUsersExistence(usersTableData) == false) {
            System.out.println("There are no users.");
            return;
        }

        showUsers();

        System.out.print("Enter email of user you want to modify his information (or cancel operation by choosing x): ");
        Scanner scanner = new Scanner(System.in);
        String chosenUserEmail = scanner.nextLine();

        if(chosenUserEmail.equals("x")) {
            return;
        }

        if(usersTableData.containsKey(chosenUserEmail) == false) {
            System.out.println("The user you chose is not existed.");
            return;
        }

        User chosenUser = usersTableData.get(chosenUserEmail);

        if(chosenUser instanceof Student) {
            Student chosenStudent = (Student) chosenUser;
            handleModifyingStudentInfo(chosenStudent);
        }
        else {
            Teacher chosenTeacher = (Teacher) chosenUser;
            handleModifyingTeacherInfo(chosenTeacher);
        }
    }

    private void showUsers() {
        for(User user: usersTableData.values()) {
            if(user instanceof Student) {
                Student student = (Student) user;
                System.out.println(student.toString());
            }
            else {
                Teacher teacher = (Teacher) user;
                System.out.println(teacher.toString());
            }
        }
    }

    private int getStudentFieldInput() {
        System.out.println("1. Do you want to modify balance? ");
        System.out.println("2. Do you want to modify seat number? ");
        System.out.println("3. Go back");
        System.out.print("[Choose 1, 2 or 3]: ");
        Scanner scanner = new Scanner(System.in);
        int studentFieldToBeModified = scanner.nextInt();

        return studentFieldToBeModified;
    }

    private void handleModifyingStudentInfo(Student studentToBeModified) {
        Scanner scanner = new Scanner(System.in);

        while(true) {
            int studentFieldToBeModified = getStudentFieldInput();

            switch (studentFieldToBeModified) {
                case 1:
                    System.out.print("Enter the new balance: ");
                    int newBalance = scanner.nextInt();
                    scanner.nextLine();
                    studentToBeModified.setBalance(newBalance);
                    break;

                case 2:
                    System.out.print("Enter the new seat number: ");
                    String newSeatNumber = scanner.nextLine();
                    studentToBeModified.setSeatNumber(newSeatNumber);
                    break;

                case 3:
                    break;

                default:
                    System.out.println("Please choose the correct number.");
            }

            if(confirmModificationContinuity() == false) {
                dataStorage.saveUser(usersTableData, studentToBeModified);
                break;
            }
        }
    }

    private int getTeacherFieldInput() {
        System.out.println("1. Do you want to modify specialization? ");
        System.out.println("2. Go back");
        System.out.print("[Choose 1 or 2]: ");
        Scanner scanner = new Scanner(System.in);
        int teacherFieldToBeModified = scanner.nextInt();

        return teacherFieldToBeModified;
    }

    private void handleModifyingTeacherInfo(Teacher teacherToBeModified) {
        Scanner scanner = new Scanner(System.in);

        while(true) {
            int teacherFieldToBeModified = getTeacherFieldInput();

            switch (teacherFieldToBeModified) {
                case 1:
                    System.out.print("Enter the new specialization: ");
                    String newSpecialization = scanner.nextLine();
                    teacherToBeModified.setSpecialization(newSpecialization);
                    break;

                case 2:
                    break;

                default:
                    System.out.println("Please choose the correct number.");
            }

            if(confirmModificationContinuity() == false) {
                dataStorage.saveUser(usersTableData, teacherToBeModified);
                break;
            }
        }
    }

    // TO BE USED IN SEMESTER
    private void filterCoursesWithNoTeacher() {
        if(resources.checkUsersExistence(usersTableData) == false) {
            System.out.println("There are no users.");
            return;
        }

        if(resources.checkCoursesExistence(coursesTableData) == false) {
            System.out.println("There are no courses");
            return;
        }

        List<Course> courses = coursesTableData
                .values()
                .stream()
                .filter(course -> {
                    return course.getStudentEmails() != null &&
                            course.getStudentEmails().size() > 0 &&
                            course.getTeacherEmails() != null &&
                            course.getTeacherEmails().size() == 0;
                } ).collect(Collectors.toList());

        if(courses.size() == 0) {
            System.out.println("All courses are assigned to teachers.");
            return;
        }

        for(Course course: courses) {
            refundStudents(course);
            removeCourseFromStudentsPerCourse(course.getId(), course.getStudentEmails());
        }

        dataStorage.saveUser(usersTableData);
        dataStorage.saveCourse(coursesTableData);
    }

    private void refundStudents(Course course) {
        List<String> studentsToBeRefund = course.getStudentEmails();

        for(String studentEmail: studentsToBeRefund) {
            Student student = (Student) usersTableData.get(studentEmail);
            student.setBalance(student.getBalance() + course.getPrice());
        }

        course.getStudentEmails().clear();
    }

    private void removeCourseFromStudentsPerCourse(String courseIdToBeRemoved, List<String> studentEmails) {
        for(String studentEmail: studentEmails) {
            usersPerCoursesTableData.get(studentEmail).remove(courseIdToBeRemoved);
        }
    }

    // TO BE USED IN SEMESTER
    private void filterStudentsOnCreditHours() {
        if(resources.checkUsersExistence(usersTableData) == false) {
            System.out.println("There are no users.");
            return;
        }

        if(resources.checkCoursesExistence(coursesTableData) == false) {
            System.out.println("There are no courses");
            return;
        }

        boolean changeHappened = false;
        for(Map.Entry<String, Set<String>> user: usersPerCoursesTableData.entrySet()) {
            if(usersTableData.get(user.getKey()) instanceof Student) {
                int userCreditHour = calculateTotalCreditHours(user.getValue());
                if (userCreditHour < 9) {
                    refundStudent(user.getKey(), user.getValue());
                    removeStudentFromCourse(user.getKey(), user.getValue());
                    usersPerCoursesTableData.remove(user.getKey());
                    changeHappened = true;
                }
            }
        }

        if(changeHappened) {
            dataStorage.saveUser(usersTableData);
            dataStorage.saveCourse(coursesTableData);
        }
    }

    private void refundStudent(String studentEmail, Set<String> coursesIds) {
        Student student = (Student) usersTableData.get(studentEmail);

        int coursePrice = 0;
        for(String courseId: coursesIds) {
            coursePrice = coursePrice + coursesTableData.get(courseId).getPrice();
        }

        student.setBalance(student.getBalance() + coursePrice);
    }

    private void removeStudentFromCourse(String studentEmail, Set<String> coursesIds) {
        List<String> studentsEmails;
        for(String courseId: coursesIds) {
            studentsEmails = coursesTableData.get(courseId).getStudentEmails();
            studentsEmails.removeIf((email) -> email.equals(studentEmail));
        }
    }

    private int calculateTotalCreditHours(Set<String> coursesIds) {
        int sum = 0;
        for(String courseId: coursesIds) {
            sum = sum + coursesTableData.get(courseId).getCreditHours();
        }

        return sum;
    }
}
