import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class Course {
    private String id;
    private String name;
    private int creditHours;
    private int maxStudents;
    private int price;
    private String specialization;
    private List<String> studentEmails;
    private List<String> teacherEmails;

    public Course() {}

    @Override
    public String toString() {
        return String.format("[ID = %s, Name = %s, Credit Hours: %d, Max Students: %d, Price: %d, Specialization: %s]", id, name, creditHours, maxStudents, price, specialization);
    }
}
