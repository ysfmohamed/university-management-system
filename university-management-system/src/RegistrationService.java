import java.util.Map;
import java.util.Scanner;
import java.util.UUID;

public class RegistrationService {
    private static RegistrationService registrationService = new RegistrationService();
    private Map<String, User> usersTableData;
    private Map<String, Request> requestsTableData;
    private DataStorage dataStorage;

    public RegistrationService() {
        dataStorage = DataStorage.getInstance();
    }

    public static RegistrationService getInstance() {
        return registrationService;
    }

    public void setUsersTableData(Map<String, User> usersTableData) {
        this.usersTableData = usersTableData;
    }

    public void setRequestsTableData(Map<String, Request> requestsTableData) {
        this.requestsTableData = requestsTableData;
    }

    public void handleSignUp() {
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


        if (checkTheExistenceOfUser(studentToBeCreatedEmail) == false) {
            System.out.println("Email is already used or being reviewed by Admin, please register with a new email.");
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

        dataStorage.saveRequest(requestsTableData, request);
        requestsTableData.put(studentToBeCreatedEmail, request);
        System.out.println("Your account creation request is being reviewed by the Admin.");
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

        if (checkTheExistenceOfUser(teacherToBeCreatedEmail) == false) {
            System.out.println("Email is already used or being reviewed by Admin, please register with a new email.");
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

        dataStorage.saveRequest(requestsTableData, request);
        requestsTableData.put(teacherToBeCreatedEmail, request);
        System.out.println("Your account creation request is being reviewed by the Admin.");
    }

    public boolean checkTheExistenceOfUser(String userToBeCreatedEmail) {
        if (usersTableData.containsKey(userToBeCreatedEmail) || requestsTableData.containsKey(userToBeCreatedEmail)) {
            return false;
        }

        return true;
    }
    private boolean confirmUserCreationContinuity() {
        System.out.print("Are you sure you want to continue in creating an account (Y/N)? ");
        char confirm = new Scanner(System.in).next().charAt(0);
        return confirm == 'Y' || confirm == 'y';
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

    public boolean confirmSignUpContinuity() {
        System.out.print("Do you want to try to make an account again (Y/N)? ");
        Scanner scanner = new Scanner(System.in);
        char confirm = scanner.next().charAt(0);

        return confirm == 'Y' || confirm == 'y';
    }
}
