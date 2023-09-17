import lombok.Data;

@Data
public class Teacher extends OrdinaryUser {
    private String specialization;

    public Teacher() {}

    public Teacher(String id, String name, String email, String password, OrdinaryUserType type, boolean gender, String specialization) {
        super(id, name, email, password, type, gender);
        this.specialization = specialization;
    }

    @Override
    public String toString() {
        return String.format("[ID = %s, Name = %s, Email = %s, Password = %s, Type = %s, Gender = %s, Specialization = %s]", this.getId(), this.getName(), this.getEmail(), this.getPassword(), this.getType(), this.isGender(), this.getSpecialization());
    }
}
