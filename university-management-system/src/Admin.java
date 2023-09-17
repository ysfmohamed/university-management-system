import java.util.List;
import java.util.Map;

public class Admin extends User {
    @Override
    public boolean signup(User user) {
        throw new UnsupportedOperationException("Admin cannot create an account");
    }
}
