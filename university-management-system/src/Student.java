import lombok.Data;

@Data
public class Student extends OrdinaryUser {
    private String seatNumber;
    private int balance;

    public Student() {}

    public Student(String id, String name, String email, String password, OrdinaryUserType type, boolean gender, String seatNumber, int balance) {
        super(id, name, email, password, type, gender);
        this.seatNumber = seatNumber;
        this.balance = balance;
    }

    @Override
    public String toString() {
        return String.format("[ID = %s, Name = %s, Email = %s, Password = %s, Type = %s, Gender = %s, Seat Number = %s, Balance = %d]", this.getId(), this.getName(), this.getEmail(), this.getPassword(), this.getType(), this.isGender(), this.getSeatNumber(), this.getBalance());
    }

    public String studentDetails() {
        return String.format("[ID = %s, Name = %s, Email = %s, Type = %s, Gender = %s, Seat Number = %s]", this.getId(), this.getName(), this.getEmail(), this.getType(), this.isGender(), this.getSeatNumber());
    }
}
