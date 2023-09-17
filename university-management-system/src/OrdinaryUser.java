import lombok.Data;

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
}
