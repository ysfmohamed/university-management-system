import lombok.Data;

@Data
public class Request {
    private User user;
    private RequestType type;
//    private RequestStatus status;

    public Request() {}

    public Request(User user, RequestType type) {
        this.user = user;
        this.type = type;
    }

    @Override
    public String toString() {
        return String.format("[Requester ID: %s, Requester Email: %s, Requester Name: %s, Request Type: %s]", user.getId(), user.getEmail(), user.getName(), type);
    }
}
