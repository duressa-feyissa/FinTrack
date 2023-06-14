package Users;
import Common.Common;
import org.json.JSONArray;
import org.json.JSONObject;
import javax.crypto.SecretKey;
import java.sql.*;

public class User {
    String id;
    String username;
    String email;
    String password;
    boolean isAdmin;
    Connection connection;

    public User() {
        this.id = Common.CreateId();
    }

    public void setUser(String username, String email, String password, boolean isAdmin) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.isAdmin = isAdmin;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void JsonToObj(String data) {
        JSONObject obj = new JSONObject(data);
        this.id = obj.getString("id");
        this.username = obj.getString("usenname");
        this.email = obj.getString("email");
        this.password = obj.getString("password");
        this.isAdmin = obj.getBoolean("isAdmin");
    }

    public boolean CreateNewUser(Connection connection) {
        try {
            String sql = "SELECT * FROM user WHERE email = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, this.email);
            ResultSet result = statement.executeQuery();

            if (result.next())
                return false;

            SecretKey secretKey = Common.readKeyFromFile();
            this.password = Common.encryptMessage(this.password, secretKey);
            sql = "INSERT INTO user (id, username, email, password, isAdmin) VALUES (?, ?, ?, ?, ?)";
            statement = connection.prepareStatement(sql);

            statement.setString(1, this.id);
            statement.setString(2, this.username);
            statement.setString(3, this.email);
            statement.setString(4, this.password);
            statement.setBoolean(5, this.isAdmin);
            statement.executeUpdate();

            connection.commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateUser(Connection connection) {
        String sql = "SELECT * FROM user WHERE id = ?";
        try {
            PreparedStatement selectStatement = connection.prepareStatement(sql);
            selectStatement.setString(1, this.id);
            ResultSet result = selectStatement.executeQuery();

            if (result.next()) {
                sql = "UPDATE user SET username = ?, email = ?, isAdmin = ? WHERE id = ?";
                PreparedStatement updateStatement = connection.prepareStatement(sql);
                updateStatement.setString(1, this.username);
                updateStatement.setString(2, this.email);
                updateStatement.setBoolean(3, this.isAdmin);
                updateStatement.setString(4, this.id);

                updateStatement.executeUpdate();
                connection.commit();
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteUser(Connection connection) {
        if (this.isAdmin) {
            // Admin users cannot be deleted
            return false;
        }

        String sql = "DELETE FROM user WHERE id = ?";
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, this.id);
            if (statement.executeUpdate() == 1) {
                connection.commit();
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public User authenticateUser(Connection connection, String email, String password) {
        String sql = "SELECT * FROM user WHERE email = ?";

        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, email);

            ResultSet result = statement.executeQuery();
            if (result.next()) {
                User user = new User();
                user.setId(result.getString("id"));
                user.setUser(result.getString("username"),  result.getString("email"), result.getString("password"), result.getBoolean("isAdmin"));
                String decodedPassWord = Common.decryptMessage(user.password, Common.readKeyFromFile());
                if (decodedPassWord.equals(password))
                    return user;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getAllAccountants(Connection connection) {
        String result = "";
        String sql = "SELECT * FROM user WHERE isAdmin = false";
        JSONArray jsonArray = new JSONArray();

        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet res = statement.executeQuery();
            while (res.next()) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("id", res.getString("id"));
                jsonObject.put("username", res.getString("username"));
                jsonObject.put("email", res.getString("email"));
                jsonObject.put("isAdmin", res.getBoolean("isAdmin"));
                jsonArray.put(jsonObject);
            }
            result = jsonArray.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public String getAllAccountantsBudget(Connection connection, String id) {
        String result = "";
        String sql = "SELECT accountant_ids FROM budget WHERE id = ?";
        JSONArray jsonArray = new JSONArray(getAllAccountants(connection));

        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, id);
            ResultSet res = statement.executeQuery();
            res.next();
            JSONArray accountantIds = new JSONArray(res.getString("accountant_ids"));

            // Remove accountants whose IDs are present in jsonArray
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject accountant = jsonArray.getJSONObject(i);
                String accountantId = accountant.getString("id");
                if (accountantIds.toString().contains(accountantId)) {
                    jsonArray.remove(i);
                    i--;
                }
            }

            result = jsonArray.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    public JSONObject getAccountantById(Connection connection) {
        JSONObject jsonObject = new JSONObject();
        String sql = "SELECT * FROM user WHERE id = ? AND isAdmin = false";

        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, this.id);
            ResultSet res = statement.executeQuery();
            if (res.next()) {
                jsonObject.put("id", res.getString("id"));
                jsonObject.put("username", res.getString("username"));
                jsonObject.put("email", res.getString("email"));
                jsonObject.put("isAdmin", res.getBoolean("isAdmin"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public String getBudgetsByAccountant(Connection connection) {
        String result = "";
        String sql = "SELECT * FROM budget WHERE accountant_ids LIKE ?";
        JSONArray jsonArray = new JSONArray();

        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, "%\"" + this.id + "\"%");
            ResultSet res = statement.executeQuery();
            while (res.next()) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("id", res.getString("id"));
                jsonObject.put("name", res.getString("name"));
                jsonObject.put("description", res.getString("description"));
                jsonObject.put("start", res.getDate("start"));
                jsonObject.put("accountant_ids", new JSONArray(res.getString("accountant_ids")));
                jsonObject.put("end", res.getDate("end"));
                jsonObject.put("type", res.getString("type"));
                jsonObject.put("price", res.getDouble("price"));
                jsonArray.put(jsonObject);
            }
            result = jsonArray.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
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

    public String getAllAccountantsInBudget(Connection connection, String id) {
        String result = "";
        String sql = "SELECT accountant_ids FROM budget WHERE id = ?";
        JSONArray jsonArray = new JSONArray(getAllAccountants(connection));

        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, id);
            ResultSet res = statement.executeQuery();
            res.next();
            JSONArray accountantIds = new JSONArray(res.getString("accountant_ids"));

            JSONArray includedAccountants = new JSONArray();

            // Include accountants whose IDs are present in accountantIds
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject accountant = jsonArray.getJSONObject(i);
                String accountantId = accountant.getString("id");
                if (accountantIds.toString().contains(accountantId)) {
                    includedAccountants.put(accountant);
                }
            }

            result = includedAccountants.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    public static class BudgetAnalysis {
        public static void calculateBudgetAnalysis() {
            try {
                Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/Fintrack", "shupi", "Dureti");
                Statement statement = connection.createStatement();

                // Query to calculate the total income for each budget
                String incomeQuery = "SELECT budget_id, SUM(amount) AS total_income FROM income GROUP BY budget_id";
                ResultSet incomeResult = statement.executeQuery(incomeQuery);

                // Query to calculate the total expense for each budget
                String expenseQuery = "SELECT budget_id, SUM(amount) AS total_expense FROM expense GROUP BY budget_id";
                ResultSet expenseResult = statement.executeQuery(expenseQuery);

                while (incomeResult.next() && expenseResult.next()) {
                    String budgetId = incomeResult.getString("budget_id");
                    double totalIncome = incomeResult.getDouble("total_income");
                    double totalExpense = expenseResult.getDouble("total_expense");

                    // Calculate the percentage of new income
                    double incomePercentage = (totalIncome / (totalIncome + totalExpense)) * 100;

                    // Calculate the percentage of expense
                    double expensePercentage = (totalExpense / (totalIncome + totalExpense)) * 100;

                    System.out.println("Budget ID: " + budgetId);
                    System.out.println("New Income Percentage: " + incomePercentage + "%");
                    System.out.println("Expense Percentage: " + expensePercentage + "%");
                    System.out.println("-----------------------");
                }

                statement.close();
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
