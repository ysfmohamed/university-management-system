import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// TODO removing all boolean return

public class FileHandler {
    public static List<User> readUsersFile() {
        ObjectMapper mapper = new ObjectMapper();
        List<User> users = new ArrayList<>();

        try{
            users = mapper.readValue(new File("users.json"), new TypeReference<List<User>>(){});
        }
        catch(IOException ioex) {
            ioex.printStackTrace();
        }

        return users;
    }

    public static List<Course> readCoursesFile() {
        ObjectMapper mapper = new ObjectMapper();
        List<Course> courses = new ArrayList<>();

        try{
            courses = mapper.readValue(new File("courses.json"), new TypeReference<List<Course>>() {});
        }
        catch(IOException ioex) {
            ioex.printStackTrace();
        }

        return courses;
    }

    public static List<Request> readRequestsFile() {
        ObjectMapper mapper = new ObjectMapper();
        List<Request> requests = new ArrayList<>();

        try{
            requests = mapper.readValue(new File("requests.json"), new TypeReference<List<Request>>() {});
        }
        catch(IOException ioex) {
            ioex.printStackTrace();
        }

        return requests;
    }

    public static boolean saveUser(Map<String, User> oldUsersTableData, User userToBeSaved) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        List<User> users = new ArrayList<>();

        for(User user: oldUsersTableData.values()) {
            users.add(user);
        }

        try{
            if(!oldUsersTableData.containsKey(userToBeSaved.getEmail())) {
                users.add(userToBeSaved);
            }
            mapper.writeValue(new File("users.json"), users);
        }
        catch(IOException ioex) {
            ioex.printStackTrace();
            return false;
        }

        return true;
    }

    public static boolean saveUser(Map<String, User> oldUsersTableData) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        List<User> users = new ArrayList<>();

        for(User user: oldUsersTableData.values()) {
            users.add(user);
        }

        try {
            mapper.writeValue(new File("users.json"), users);
        }
        catch(IOException ex) {
            ex.printStackTrace();
            return false;
        }

        return true;
    }

    public static boolean saveRequest(Map<String, Request> oldRequestTableData, Request requestToBeAdded) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        List<Request> requests = new ArrayList<>();
        for(Request request: oldRequestTableData.values()) {
            requests.add(request);
        }

        try{
            requests.add(requestToBeAdded);
            mapper.writeValue(new File("requests.json"), requests);
        }
        catch(IOException ioex) {
            ioex.printStackTrace();
            return false;
        }

        return true;
    }

    public static boolean saveCourse(Map<String, Course> oldCoursesTableData, Course courseToBeSaved) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        List<Course> courses = new ArrayList<>();
        for(Course course: oldCoursesTableData.values()) {
            courses.add(course);
        }

        try {
            if(!oldCoursesTableData.containsKey(courseToBeSaved.getId())) {
                courses.add(courseToBeSaved);
            }
            mapper.writeValue(new File("courses.json"), courses);
        }
        catch(IOException ioex) {
            ioex.printStackTrace();
            return false;
        }

        return true;
    }

    public static boolean saveCourse(Map<String, Course> oldCoursesTableData) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        List<Course> courses = new ArrayList<>();
        for(Course course: oldCoursesTableData.values()) {
            courses.add(course);
        }

        try {
            mapper.writeValue(new File("courses.json"), courses);
        }
        catch(IOException ioex) {
            ioex.printStackTrace();
            return false;
        }

        return true;
    }

    public static boolean deleteRequest(Map<String, Request> oldRequestsTableData, Request requestToBeDeleted) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        // remove the target request by the requester email.
        oldRequestsTableData.remove(requestToBeDeleted.getUser().getEmail());

        List<Request> requests = new ArrayList<>();
        for(Request request: oldRequestsTableData.values()) {
            requests.add(request);
        }

        try{
            mapper.writeValue(new File("requests.json"), requests);
        }
        catch(IOException ioex) {
            ioex.printStackTrace();
            return false;
        }

        return true;
    }
}
