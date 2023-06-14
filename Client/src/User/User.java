package User;

import org.json.JSONObject;
public class User {
    String id;
    String username;
    String email;
    String password;
    boolean isAdmin;
    public User(){
    }

    public void setUser(String username, String email, String password, boolean isAdmin) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.isAdmin = isAdmin;
    }

    public String getUsername() {
        return username;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public void setId(String id) {
        this.id = id;
    }

    public JSONObject toJson() {
        JSONObject user = new JSONObject();
        user.put("id", this.id);
        user.put("username", this.username);
        user.put("email", this.email);
        user.put("password", this.password);
        user.put("isAdmin", this.isAdmin);
        return user;
    }
    public void JsonToObj(String data) {
        JSONObject obj = new JSONObject(data);
        this.id = obj.getString("id");
        this.username = obj.getString("username");
        this.email = obj.getString("email");
        this.isAdmin = obj.getBoolean("isAdmin");
    }
}