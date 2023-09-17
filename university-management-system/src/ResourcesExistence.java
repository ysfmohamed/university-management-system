import java.util.Map;

public class ResourcesExistence {
    private static ResourcesExistence resources = new ResourcesExistence();

    private ResourcesExistence() {}

    public static ResourcesExistence getInstance() {
        return resources;
    }

    public boolean checkCoursesExistence(Map<String, Course> coursesTableData) {
        return coursesTableData.values().size() > 0;
    }

    public boolean checkUsersExistence(Map<String, User> usersTableData) {
        return usersTableData.values().size() > 0;
    }
}
