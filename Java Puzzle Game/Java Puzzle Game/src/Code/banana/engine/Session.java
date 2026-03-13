package Code.banana.engine;

public class Session {
    private static String loggedInUser;
    private static boolean isGuest = false;
    private static String sessionId;
    private static boolean isFirstLogin = true;

    public static String getLoggedInUser() {
        return loggedInUser;
    }

    public static void setLoggedInUser(String username) {
        loggedInUser = username;
        isFirstLogin = true;
    }

    public static boolean isGuest() {
        return isGuest;
    }

    public static void setGuestMode(boolean guest) {
        isGuest = guest;
    }

    public static String getSessionId() {
        return sessionId;
    }

    public static void setSessionId(String id) {
        sessionId = id;
    }

    public static boolean isUserLoggedIn() {
        return loggedInUser != null && !loggedInUser.isEmpty();
    }

    public static void logout() {
        loggedInUser = null;
        isGuest = false;
        sessionId = null;
        isFirstLogin = true;
    }

    public static boolean isFirstLogin() {
        return isFirstLogin;
    }

    public static void setFirstLoginFalse() {
        isFirstLogin = false;
    }
}