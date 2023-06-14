package Expense;
import Common.Common;
import org.json.JSONArray;
import org.json.JSONObject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Expense {

    String id;
    String item_id;
    String name;
    Date date;
    double amount;
    String budget_id;
    String accountant_id;

    public Expense() {
        this.id = Common.CreateId();
    }

    public void setExpense(String item_id, String name, String date, double amount, String budget_id, String accountant_id) {
        try {
            String inputPattern = "EEE MMM dd HH:mm:ss zzz yyyy";
            SimpleDateFormat inputDateFormat = new SimpleDateFormat(inputPattern, Locale.ENGLISH);

            this.item_id = item_id;
            this.name = name;
            this.date =  !date.isEmpty() ? inputDateFormat.parse(date) : new Date();
            this.amount = amount;
            this.budget_id = budget_id;
            this.accountant_id = accountant_id;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean createExpense(Connection connection) {
        try {
            String sql = "INSERT INTO expense (id, item_id, name, date, amount, budget_id, accountant_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, this.id);
            statement.setString(2, this.item_id);
            statement.setString(3, this.name);
            statement.setDate(4, new java.sql.Date(this.date.getTime()));
            statement.setDouble(5, this.amount);
            statement.setString(6, this.budget_id);
            statement.setString(7, this.accountant_id);
            statement.executeUpdate();
            connection.commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateExpense(Connection connection) {
        String sql = "SELECT * FROM expense WHERE id = ?";
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, this.id);

            ResultSet result = statement.executeQuery();
            if (result.next()) {
                sql = "UPDATE expense SET item_id = ?, name = ?, date = ?, amount = ?, budget_id = ?, accountant_id = ? WHERE id = ?";
                statement = connection.prepareStatement(sql);
                statement.setString(1, this.item_id);
                statement.setString(2, this.name);
                statement.setDate(3, new java.sql.Date(this.date.getTime()));
                statement.setDouble(4, this.amount);
                statement.setString(5, this.budget_id);
                statement.setString(6, this.accountant_id);
                statement.setString(7, this.id);
                statement.executeUpdate();
                connection.commit();
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    public String getAllExpenses(Connection connection) {
        String result = "";
        String sql = "SELECT * FROM expense";
        JSONArray jsonArray = new JSONArray();

        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet res = statement.executeQuery();
            while (res.next()) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("id", res.getString("id"));
                jsonObject.put("item_id", res.getString("item_id"));
                jsonObject.put("name", res.getString("name"));
                jsonObject.put("date", res.getDate("date"));
                jsonObject.put("amount", res.getDouble("amount"));
                jsonObject.put("budget_id", res.getString("budget_id"));
                jsonObject.put("accountant_id", res.getString("accountant_id"));
                jsonArray.put(jsonObject);
            }
            result = jsonArray.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public String getExpense(Connection connection) {
        String result = "";
        String sql = "SELECT * FROM expense WHERE id = ?";
        JSONObject jsonObject = new JSONObject();

        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, this.id);
            ResultSet res = statement.executeQuery();
            if (res.next()) {
                jsonObject.put("id", res.getString("id"));
                jsonObject.put("item_id", res.getString("item_id"));
                jsonObject.put("name", res.getString("name"));
                jsonObject.put("date", res.getDate("date"));
                jsonObject.put("amount", res.getDouble("amount"));
                jsonObject.put("budget_id", res.getString("budget_id"));
                jsonObject.put("accountant_id", res.getString("accountant_id"));
            }
            result = jsonObject.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public boolean deleteExpense(Connection connection) {
        String sql = "DELETE FROM expense WHERE id = ?";
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

    public JSONObject toJson() {
        JSONObject expense = new JSONObject();
        expense.put("id", this.id);
        expense.put("item_id", this.item_id);
        expense.put("name", this.name);
        expense.put("date", this.date);
        expense.put("amount", this.amount);
        expense.put("budget_id", this.budget_id);
        expense.put("accountant_id", this.accountant_id);
        return expense;
    }
}
