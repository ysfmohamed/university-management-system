import lombok.Data;

import java.util.Map;
import java.util.Set;

@Data
public abstract class OrdinaryUser extends User {
    private OrdinaryUserType type;
    private boolean gender;

    public OrdinaryUser() {}

    public OrdinaryUser(String id, String name, String email, String password, OrdinaryUserType type, boolean gender) {
        super(id, name, email, password);
        this.type = type;
        this.gender = gender;
    }

    @Override
    public boolean signup(User user) {
        return true;
    }

    public abstract boolean registerInCourse(Map<String, Set<String>> userPerCoursesTableData, Map<String, Course> coursesTableData, Map<String, User> usersTableData, String courseId);
}
