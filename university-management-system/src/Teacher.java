import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Data
public class Teacher extends OrdinaryUser {
    private String specialization;

    public Teacher() {}

    public Teacher(String id, String name, String email, String password, OrdinaryUserType type, boolean gender, String specialization) {
        super(id, name, email, password, type, gender);
        this.specialization = specialization;
    }

    public boolean withdrawFromCourse(Course course, Map<String, Set<String>> usersPerCoursesTableData) {
        List<String> teacherEmails = course.getTeacherEmails();

        if(teacherEmails == null || teacherEmails.size() == 0) {
            System.out.println("This course doesn't have teacher.");
            return false;
        }

        String teacherEmail = teacherEmails.get(0);
        usersPerCoursesTableData.get(teacherEmail).remove(course.getId());

        course.getTeacherEmails().clear();

        return true;
    }

    @Override
    public boolean registerInCourse(Map<String, Set<String>> userPerCoursesTableData, Map<String, Course> coursesTableData, Map<String, User> usersTableData, String courseId) {
        Course course = coursesTableData.get(courseId);
        List<String> teachersOnCourse = course.getTeacherEmails();

        System.out.println("Teacher ID: " + this.getId());

        Set<String> teacherCourses = userPerCoursesTableData.get(this.getId());

        if(teacherCourses != null && teacherCourses.size() == 3) {
            System.out.println("You reached the threshold, you cannot be assigned to that course.");
            return false;
        }

        if(teachersOnCourse != null && teachersOnCourse.size() > 0 && teachersOnCourse.get(0).equals(this.getEmail())) {
            System.out.println("You are already registered on that course, you cannot register on the same course twice.");
            return false;
        }

        if(teachersOnCourse != null && teachersOnCourse.size() > 0) {
            System.out.println("This course is already assigned to another teacher, course cannot have more than one teacher.");
            return false;
        }

        course.getTeacherEmails().add(this.getEmail());

        DataStore.saveCourse(coursesTableData);

        return true;
    }

    @Override
    public String toString() {
        return String.format("[ID = %s, Name = %s, Email = %s, Password = %s, Type = %s, Gender = %s, Specialization = %s]", this.getId(), this.getName(), this.getEmail(), this.getPassword(), this.getType(), this.isGender(), this.getSpecialization());
    }
}
