package DB;
import java.sql.*;
public class DB {
    private Connection connection = null;
    public DB(String database, String user, String password) {
        String url = "jdbc:mysql://localhost:3306/" + database;
        try {
            connection = DriverManager.getConnection(url, user, password);
            connection.setAutoCommit(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        return this.connection;
    }
}
