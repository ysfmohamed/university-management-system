import java.util.*;
import java.util.stream.Collectors;

// TODO Restructure the engine to deal with intermediate layers

public class UMSEngine {
    private boolean isSemesterRunning = false;
    private Map<String, Request> requestsTableData;
    private Map<String, User> usersTableData;
    private Map<String, Course> coursesTableData;

    private Map<String, Set<String>> usersPerCoursesTableData;

    private User loggedInUser;
    private Student loggedInStudent;
    private Teacher loggedInTeacher;

    public void loadDataToMemory() {
        requestsTableData = DataStore.buildRequestsData();
        usersTableData = DataStore.buildUsersData();
        coursesTableData = DataStore.buildCoursesData();
        usersPerCoursesTableData = DataStore.buildUserPerCourses();
    }

    public void run() {
        System.out.println("\tUniversity Management System\t");

        loadDataToMemory();

        while(true) {
            int authType = getAuthTypeInput();

            switch(authType) {
                case 1:
                    System.out.println("Please log in");
                    handleLogin();
                    break;

                case 2:
                    System.out.println("Please sign up");
                    handleSignUp();
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
            // logged in as an admin
            showAdminPanel();
        }

        else if(usersTableData.containsKey(givenEmail) && usersTableData.get(givenEmail).getPassword().equals(givenPassword)) {
            loggedInUser = usersTableData.get(givenEmail);
            checkTheInstanceOfUser(loggedInUser);

        } else {
            System.out.println("Email or Password is incorrect.");
            return;
        }
    }

    private void checkTheInstanceOfUser(User user) {
        if(loggedInUser instanceof Student) {
            loggedInStudent = (Student) loggedInUser;
            System.out.println(loggedInStudent.toString());
            showStudentPanel();
        } else {
            loggedInTeacher = (Teacher) loggedInUser;
            System.out.println(loggedInTeacher.toString());
            showTeacherPanel();
        }
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

    private void showTeacherPanel() {
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

    private void handleTeacherWithdrawingCourse() {
        if(isSemesterRunning) {
            System.out.println("You cannot withdraw from a course while the semester is running.");
            return;
        }

        showTeacherCourses();

        System.out.print("Enter the course id you want to withdraw from (or cancel operation by choosing x): ");
        Scanner scanner = new Scanner(System.in);
        String courseId = scanner.nextLine();

        if(courseId.equals("x")) {
            return;
        }

        if(coursesTableData.containsKey(courseId) == false) {
            System.out.println("Course ID you entered is not exist, enter an existent one please.");
            return;
        }

        Course course = coursesTableData.get(courseId);

        if (loggedInTeacher.withdrawFromCourse(course, usersPerCoursesTableData) == false) {
            return;
        }

        List<String> studentEmails = course.getStudentEmails();

        if(studentEmails != null && studentEmails.size() > 0) {
            refundStudents(course);
            removeCourseFromStudentsPerCourse(courseId, studentEmails);
            DataStore.saveUser(usersTableData);
            System.out.println("All students registered on that course have been refunded and removed from the course.");
        }

        System.out.println("Withdraw operation is completed.");
        DataStore.saveCourse(coursesTableData);
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

    private void showAllStudentsOfTeacher() {
        if (checkCoursesExistence() == false) {
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

    private boolean checkCoursesExistence() {
        return coursesTableData.values().size() > 0;
    }

    private List<Course> filterCoursesOnCriteria() {
        String loggedInTeacherSpecialization = loggedInTeacher.getSpecialization();

        return coursesTableData
                .values()
                .stream()
                .filter(course -> course.getSpecialization().equals(loggedInTeacherSpecialization))
                .collect(Collectors.toList());
    }

    private void showAvailableCourses() {
        if (checkCoursesExistence() == false) {
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

    private void showTeacherCourses() {
        if(checkCoursesExistence() == false) {
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

        if(loggedInTeacher.registerInCourse(usersPerCoursesTableData, coursesTableData, null, courseId)) {
            if(usersPerCoursesTableData.get(loggedInUser.getEmail()) == null) {
                Set<String> courseIds = new HashSet<>();
                courseIds.add(courseId);
                usersPerCoursesTableData.put(loggedInUser.getEmail(), courseIds);
            }
            else {
                usersPerCoursesTableData.get(loggedInUser.getEmail()).add(courseId);
            }
            System.out.println("You have been registered to this course successfully");
        }
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

    private void showStudentPanel() {
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

        if(loggedInStudent.registerInCourse(usersPerCoursesTableData, coursesTableData, usersTableData, courseId)) {
            if(usersPerCoursesTableData.get(loggedInUser.getEmail()) == null) {
                Set<String> courseIds = new HashSet<>();
                courseIds.add(courseId);
                usersPerCoursesTableData.put(loggedInUser.getEmail(), courseIds);
            }
            else {
                usersPerCoursesTableData.get(loggedInUser.getEmail()).add(courseId);
            }
            System.out.println("You have been registered to the course successfully");
        }
    }

    private void showStudentCourses() {
        Set<String> studentCourses = usersPerCoursesTableData.get(loggedInUser.getEmail());

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

    private void requestFinancialAid() {
        Request currentReq = requestsTableData.get(loggedInUser.getEmail());

        if(currentReq != null && currentReq.getType().equals(RequestType.FINANCIAL_AID) == false) {
            System.out.println("You are not authorized to request a financial aid. Make an account first");
            return;
        }

        else if(currentReq != null && currentReq.getType().equals(RequestType.FINANCIAL_AID)) {
            System.out.println("You already asked for financial aid, you cannot ask for financial aid twice.");
            return;
        }

        Request requestedFinancialAid = loggedInStudent.requestFinancialAid(requestsTableData);

        if (requestedFinancialAid != null) {
            requestsTableData.put(loggedInUser.getEmail(), requestedFinancialAid);
            System.out.println("Your financial aid request is sent and being reviewed by the Admin.");
        }

        else {
            System.out.println("Error happened while submitting your financial aid request.");
        }
    }
    private boolean confirmAuthContinuity() {
        System.out.print("Do you want to authenticate again (Y/N)? ");
        Scanner scanner = new Scanner(System.in);
        char confirm = scanner.next().charAt(0);

        return confirm == 'Y' || confirm == 'y';
    }

    public boolean confirmSignUpContinuity() {
        System.out.print("Do you want to try to make an account again (Y/N)? ");
        Scanner scanner = new Scanner(System.in);
        char confirm = scanner.next().charAt(0);

        return confirm == 'Y' || confirm == 'y';
    }

    private void handleSignUp() {
        while(true) {
            int userToBeRegisteredType = userToBeRegisteredTypeInput();

            switch(userToBeRegisteredType) {
                case 1:
                    System.out.println("Student wants to create an account.");
                    handleStudentAccountCreation();
                    break;
                case 2:
                    System.out.println("Teacher want to create an account.");
                    handleTeacherAccountCreation();
                    break;
                case 3:
                    break;
                default:
                    System.out.println("Please choose the correct number.");
            }

            if(confirmSignUpContinuity() == false) {
                break;
            }
        }
    }

    private int userToBeRegisteredTypeInput() {
        System.out.println("1. Are you student? ");
        System.out.println("2. Are you teacher? ");
        System.out.println("3. Go back");
        System.out.print("[Choose 1, 2 or 3]: ");
        Scanner scanner = new Scanner(System.in);
        int userToBeRegisteredType = scanner.nextInt();

        return userToBeRegisteredType;
    }

    private boolean confirmUserCreationContinuity() {
        System.out.print("Are you sure you want to continue in creating an account (Y/N)? ");
        char confirm = new Scanner(System.in).next().charAt(0);
        return confirm == 'Y' || confirm == 'y';
    }

    private void handleStudentAccountCreation() {
        System.out.println("CREATE STUDENT ACCOUNT");

        if(confirmUserCreationContinuity() == false) {
            return;
        }

        String studentToBeCreatedId = UUID.randomUUID().toString();

        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter your name: ");
        String studentToBeCreatedName = scanner.nextLine();

        System.out.print("Enter your email address: ");
        String studentToBeCreatedEmail = scanner.nextLine();

        try {
            checkTheExistenceOfUser(studentToBeCreatedEmail);
        }
        catch(EmailAlreadyExistsException ex) {
            System.out.println(ex.getMessage());
            return;
        }

        System.out.print("Enter your password: ");
        String studentToBeCreatedPassword = scanner.nextLine();

        System.out.print("Enter your gender [false for male and true for female]: ");
        boolean studentToBeCreatedGender = scanner.nextBoolean();
        scanner.nextLine();

        System.out.print("Enter your seatNumber: ");
        String studentToBeCreatedSeatNumber = scanner.nextLine();

        System.out.print("Enter your balance: ");
        int studentToBeCreatedBalance = scanner.nextInt();

        System.out.println();

        User user = new Student(studentToBeCreatedId, studentToBeCreatedName, studentToBeCreatedEmail, studentToBeCreatedPassword, OrdinaryUserType.STUDENT, studentToBeCreatedGender, studentToBeCreatedSeatNumber, studentToBeCreatedBalance);

        Request request = new Request(user, RequestType.USER_REGISTRATION);

        if(DataStore.saveRequest(requestsTableData, request)) {
            requestsTableData.put(studentToBeCreatedEmail, request);
            System.out.println("Your account creation request is being reviewed by the Admin.");
        }
        else {
            System.out.println("Your account creation request has been failed.");
        }
    }

    private void handleTeacherAccountCreation() {
        System.out.println("CREATE TEACHER ACCOUNT");

        if(confirmUserCreationContinuity() == false) {
            return;
        }

        String teacherToBeCreatedId = UUID.randomUUID().toString();

        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter your name: ");
        String teacherToBeCreatedName = scanner.nextLine();

        System.out.print("Enter your email address: ");
        String teacherToBeCreatedEmail = scanner.nextLine();

        try {
            checkTheExistenceOfUser(teacherToBeCreatedEmail);
        }
        catch(EmailAlreadyExistsException ex) {
            System.out.println(ex.getMessage());
            return;
        }

        System.out.print("Enter your password: ");
        String teacherToBeCreatedPassword = scanner.nextLine();

        System.out.print("Enter your gender [0 for male and 1 for female]: ");
        boolean teacherToBeCreatedGender = scanner.nextBoolean();
        scanner.nextLine();

        System.out.print("Enter your specialization: ");
        String teacherToBeCreatedSpecialization = scanner.nextLine();

        System.out.println();

        User user = new Teacher(teacherToBeCreatedId, teacherToBeCreatedName, teacherToBeCreatedEmail, teacherToBeCreatedPassword, OrdinaryUserType.TEACHER, teacherToBeCreatedGender, teacherToBeCreatedSpecialization);

        Request request = new Request(user, RequestType.USER_REGISTRATION);

        if(DataStore.saveRequest(requestsTableData, request)) {
            requestsTableData.put(teacherToBeCreatedEmail, request);
            System.out.println("Your account creation request is being reviewed by the Admin.");
        }
        else {
            System.out.println("Your account creation request has been failed.");
        }
    }

    public void checkTheExistenceOfUser(String userToBeCreatedEmail) throws EmailAlreadyExistsException {
        if (usersTableData.containsKey(userToBeCreatedEmail) || requestsTableData.containsKey(userToBeCreatedEmail)) {
            throw new EmailAlreadyExistsException("Email is already in use or being reviewed by the Admin. Please try to sign up again with a NEW EMAIL.");
        }
    }

    private boolean confirmContinuity() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Do you want to make other operations (Y/N)? ");
        char confirm = scanner.next().charAt(0);

        return confirm == 'Y' || confirm == 'y';
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

    private void showAdminPanel() {
//        Thread semester = new Thread();

        while(true) {
            int adminCommandType = getAdminCommandInput();

            switch (adminCommandType) {
                case 1:
                    System.out.println("Start semester");
                    handleStartingSemester(); // TODO
                    break;

                case 2:
                    System.out.println("Resuming semester");
                    handleResumingSemester(); // TODO
                    break;

                case 3:
                    System.out.println("Show Current Requests");
                    handleCurrentRequests(); // TODO
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

    private void filterCoursesWithNoTeacher() {
        if(checkUsersExistence() == false) {
            System.out.println("There are no users.");
            return;
        }

        if(checkCoursesExistence() == false) {
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
//            DataStore.saveUser(usersTableData);
//            DataStore.saveCourse(coursesTableData);
            removeCourseFromStudentsPerCourse(course.getId(), course.getStudentEmails());
        }

        DataStore.saveUser(usersTableData);
        DataStore.saveCourse(coursesTableData);
    }

    private void filterStudentsOnCreditHours() {
        if(checkUsersExistence() == false) {
            System.out.println("There are no users.");
            return;
        }

        if(checkCoursesExistence() == false) {
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
            DataStore.saveUser(usersTableData);
            DataStore.saveCourse(coursesTableData);
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

    private void handleStartingSemester() {
        filterCoursesWithNoTeacher();
        filterStudentsOnCreditHours();

        isSemesterRunning = true;
        Semester semester = new Semester();

        Thread thread = new Thread(semester);
        thread.start();
    }

    private void handleResumingSemester() {
        Thread.currentThread().notify();
    }

    private boolean checkUsersExistence() {
        return usersTableData.values().size() > 0;
    }

//    private List<User> getAllUsers() {
//        return new ArrayList<>(usersTableData.values());
//    }
//
//    private List<User> getUsersByType(String userType) {
//        List<User> users = getAllUsers();
//
//        if(userType.equals(OrdinaryUserType.STUDENT)) {
//            return users.stream().filter(user -> user instanceof Student).collect(Collectors.toList());
//        }
//        else {
//            return users.stream().filter(user -> user instanceof Teacher).collect(Collectors.toList());
//        }
//    }

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

    private void handleModifyingAccountInfo() {
        if(isSemesterRunning) {
            System.out.println("You cannot modify account info while the semester is running.");
            return;
        }

        if(checkUsersExistence() == false) {
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
                DataStore.saveUser(usersTableData, teacherToBeModified);
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
                DataStore.saveUser(usersTableData, studentToBeModified);
                break;
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
            Admin.accept(usersTableData, requestsTableData, userRequest);
            usersTableData.put(userEmail, userRequest.getUser());
            requestsTableData.remove(userEmail);
        }
        else {
            requestsTableData.remove(userEmail);
            Admin.reject(requestsTableData, userRequest);
        }
    }

    private List<Request> getRegistrationRequests() {
        return requestsTableData
                .values()
                .stream()
                .filter(request -> request.getType().equals(RequestType.USER_REGISTRATION))
                .collect(Collectors.toList());
    }

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
            DataStore.saveUser(usersTableData, userRequest.getUser());
            DataStore.deleteRequest(requestsTableData, userRequest);
            System.out.println("Financial aid is completed, and the student's balance increased by 3500.");
        }
        else {
            requestsTableData.remove(userEmail);
            Admin.reject(requestsTableData, userRequest);
        }
    }

    private boolean confirmHandlingRequestContinuity() {
        System.out.print("Do you want to handle another request (Y/N)? ");
        Scanner scanner = new Scanner(System.in);
        int confirm = scanner.next().charAt(0);

        return confirm == 'Y' || confirm == 'y';
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

    private void showAllCourses() {
        System.out.println("Current Courses: ");

        int i = 1;
        for(Course course: coursesTableData.values()) {
            System.out.println(i + ". " + course.toString());
            i++;
        }
    }

    private Course getTheCourseToBeModified() {
        System.out.print("Please enter the course id you want to modify or (cancel by choosing x): ");
        Scanner scanner = new Scanner(System.in);
        String courseIdToBeModified = scanner.nextLine();

        if(courseIdToBeModified.equals("x")) {
            return null;
        }

        return coursesTableData.get(courseIdToBeModified);
    }

    private void handleModifyingCourse() {
        if(isSemesterRunning) {
            System.out.println("You cannot modify course while the semester is running.");
            return;
        }

        if (checkCoursesExistence() == false) {
            System.out.println("There are no courses.");
            return;
        }

        showAllCourses();

        Course courseToBeModified = getTheCourseToBeModified();

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
                    break;

                case 2:
                    System.out.print("Enter new course credit hours value: ");
                    int newCourseCreditHours = scanner.nextInt();
                    scanner.nextLine();
                    courseToBeModified.setCreditHours(newCourseCreditHours);
                    courseToBeModified.setPrice(newCourseCreditHours * 1150);
                    break;

                    case 3:
                        System.out.print("Enter new course max students value: ");
                        int newCourseMaxStudents = scanner.nextInt();
                        scanner.nextLine();
                        courseToBeModified.setMaxStudents(newCourseMaxStudents);
                    break;

                case 5:
                    System.out.print("Enter new course specialization value: ");
                    String newCourseSpecialization = scanner.nextLine();
                    courseToBeModified.setSpecialization(newCourseSpecialization);
                    break;

                case 6:
                    break;

                default:
                    System.out.println("Please choose the correct number.");
            }

            if (confirmModificationContinuity() == false) {
                DataStore.saveCourse(coursesTableData, courseToBeModified);
                break;
            }
        }
    }

    private boolean confirmModificationContinuity() {
        System.out.print("Do you want to make another modification (Y/N)? ");
        Scanner scanner = new Scanner(System.in);
        char confirm = scanner.next().charAt(0);

        return confirm =='Y' || confirm =='y';
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

        // save the course in the courses.json file
        Admin.addCourse(coursesTableData, newCourse);

        // make sure to add the course in the coursesTableData HashMap
        coursesTableData.put(newCourse.getId(), newCourse);
        System.out.println("Courses entries size after adding course: " + coursesTableData.size());
    }

    private int getAdminModificationInput() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("What do you want to modify? ");
        System.out.println("1. Modify name? ");
        System.out.println("2. Modify Credit Hours? ");
        System.out.println("3. Modify Max Students? ");
        //System.out.println("4. Modify Price? ");
        System.out.println("4. Modify Specialization? ");
        System.out.println("5. Go back");
        System.out.print("[Choose 1, 2, 3, 4, or 5]: ");
        int modify = scanner.nextInt();

        return modify;
    }
}
