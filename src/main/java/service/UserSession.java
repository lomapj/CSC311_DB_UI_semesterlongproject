package service;

public class UserSession {

    private static UserSession instance;
    private String username;
    private String password;

    private UserSession(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public static synchronized UserSession getInstance(String username, String password) {
        if (instance == null) {
            instance = new UserSession(username, password);
        }
        return instance;
    }

    public static synchronized UserSession getInstance() {
        if (instance == null) {
            throw new IllegalStateException("User is not logged in.");
        }
        return instance;
    }

    public static synchronized void clearSession() {
        instance = null;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
