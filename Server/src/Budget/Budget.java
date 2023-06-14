package Budget;
import Item.Item;
import org.json.JSONArray;
import org.json.JSONObject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Budget {

    private String id;
    private String name;
    private String description;
    private Date start;
    private Date end;
    private JSONArray accountantIds;
    private String type;
    private double price;

    public Budget() {

    }


    public void setBudget(String id, String name, String description, String start, String end) {
        try {
            String inputPattern = "EEE MMM dd HH:mm:ss zzz yyyy";
            SimpleDateFormat inputDateFormat = new SimpleDateFormat(inputPattern, Locale.ENGLISH);

            this.id = id;
            this.name = name;
            this.description = description;
            this.start = inputDateFormat.parse(start);
            this.end = inputDateFormat.parse(end);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean createBudget(Connection connection, JSONArray accountantIds, String type, double price) {
        try {
            String sql = "INSERT INTO budget (id, name, description, start, end, accountant_ids, type, price) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, this.id);
            statement.setString(2, this.name);
            statement.setString(3, this.description);
            statement.setDate(4, new java.sql.Date(this.start.getTime()));
            statement.setDate(5, new java.sql.Date(this.end.getTime()));
            statement.setString(6, accountantIds.toString());
            statement.setString(7, type);
            statement.setDouble(8, price);
            statement.executeUpdate();
            connection.commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateBudget(Connection connection) {
        String sql = "SELECT * FROM budget WHERE id = ?";
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, this.id);
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                sql = "UPDATE budget SET name = ?, description = ?, start = ?, end = ?, accountant_ids = ?, type = ?, price = ? " +
                        "WHERE id = ?";
                statement = connection.prepareStatement(sql);
                statement.setString(1, this.name);
                statement.setString(2, this.description);
                statement.setDate(3, new java.sql.Date(this.start.getTime()));
                statement.setDate(4, new java.sql.Date(this.end.getTime()));
                statement.setString(5, this.accountantIds.toString());
                statement.setString(6, this.type);
                statement.setDouble(7, this.price);
                statement.setString(8, this.id);

                statement.executeUpdate();
                connection.commit();
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public String getAllBudget(Connection connection) {
        String result = "";
        String sql = "SELECT * FROM budget";
        JSONArray jsonArray = new JSONArray();

        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet res = statement.executeQuery();
            while (res.next()) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("id", res.getString("id"));
                jsonObject.put("name", res.getString("name"));
                jsonObject.put("description", res.getString("description"));
                jsonObject.put("start", res.getDate("start"));
                jsonObject.put("end", res.getDate("end"));
                jsonObject.put("accountant_ids", new JSONArray(res.getString("accountant_ids")));
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

    public String getBudget(Connection connection) {
        String result = "";
        String sql = "SELECT * FROM budget WHERE id = ?";
        JSONObject jsonObject = new JSONObject();

        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, this.id);
            ResultSet res = statement.executeQuery();
            if (res.next()) {
                jsonObject.put("id", res.getString("id"));
                jsonObject.put("name", res.getString("name"));
                jsonObject.put("description", res.getString("description"));
                jsonObject.put("start", res.getDate("start"));
                jsonObject.put("end", res.getDate("end"));
                jsonObject.put("accountant_ids", new JSONArray(res.getString("accountant_ids")));
                jsonObject.put("type", res.getString("type"));
                jsonObject.put("price", res.getDouble("price"));
            }
            result = jsonObject.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public boolean deleteBudget(Connection connection) {
        String sql = "DELETE FROM budget WHERE id = ?";
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
        JSONObject budget = new JSONObject();
        budget.put("id", this.id);
        budget.put("name", this.name);
        budget.put("description", this.description);
        budget.put("start", this.start);
        budget.put("end", this.end);
        budget.put("accountant_ids", this.accountantIds);
        budget.put("type", this.type);
        budget.put("price", this.price);
        return budget;
    }

    public boolean addAccountant(Connection connection, String id, String accountantId) {
        try {
            String selectSql = "SELECT accountant_ids FROM budget WHERE id = ?";
            PreparedStatement selectStatement = connection.prepareStatement(selectSql);
            selectStatement.setString(1, id);
            ResultSet resultSet = selectStatement.executeQuery();
            if (resultSet.next()) {
                String existingAccountantIds = resultSet.getString("accountant_ids");
                JSONArray jsonArray = new JSONArray(existingAccountantIds);

                jsonArray.put(accountantId);

                String updateSql = "UPDATE budget SET accountant_ids = ? WHERE id = ?";
                PreparedStatement updateStatement = connection.prepareStatement(updateSql);
                updateStatement.setString(1, jsonArray.toString());
                updateStatement.setString(2, id);
                updateStatement.executeUpdate();
                connection.commit();

                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean removeAccountant(Connection connection, String id, String accountantId) {
        try {
            String selectSql = "SELECT accountant_ids FROM budget WHERE id = ?";
            PreparedStatement selectStatement = connection.prepareStatement(selectSql);
            selectStatement.setString(1, id);
            ResultSet resultSet = selectStatement.executeQuery();
            if (resultSet.next()) {
                String existingAccountantIds = resultSet.getString("accountant_ids");
                JSONArray jsonArray = new JSONArray(existingAccountantIds);
                boolean found = false;
                for (int i = 0; i < jsonArray.length(); i++) {
                    if (jsonArray.getString(i).equals(accountantId)) {
                        jsonArray.remove(i);
                        found = true;
                    }
                }
                if (found) {
                    String updateSql = "UPDATE budget SET accountant_ids = ? WHERE id = ?";
                    PreparedStatement updateStatement = connection.prepareStatement(updateSql);
                    updateStatement.setString(1, jsonArray.toString());
                    updateStatement.setString(2, id);
                    updateStatement.executeUpdate();
                    connection.commit();
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public JSONArray getAllBudgetsWithIncome(Connection connection, String type) {
        JSONArray resultArray = new JSONArray();
        String budgetSql = "SELECT * FROM budget";

        try {
            PreparedStatement budgetStatement = connection.prepareStatement(budgetSql);
            ResultSet budgetResult = budgetStatement.executeQuery();

            while (budgetResult.next()) {
                JSONObject budgetObject = new JSONObject();
                String budgetId = budgetResult.getString("id");
                budgetObject.put("id", budgetId);
                budgetObject.put("name", budgetResult.getString("name"));
                budgetObject.put("description", budgetResult.getString("description"));
                budgetObject.put("start", budgetResult.getDate("start"));
                budgetObject.put("end", budgetResult.getDate("end"));
                budgetObject.put("type", budgetResult.getString("type"));
                budgetObject.put("price", budgetResult.getDouble("price"));

                JSONArray dataArray = getDataForBudget(connection, budgetId, type);
                if (type.equals("income"))
                    budgetObject.put("income", dataArray);
                else
                    budgetObject.put("expense", dataArray);
                resultArray.put(budgetObject);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultArray;
    }

    public JSONArray getAllBudgetsWithAccountant(Connection connection, String type, String id) {
        JSONArray resultArray = new JSONArray();
        String budgetSql = "SELECT * FROM budget WHERE accountant_ids LIKE ?";

        try {
            PreparedStatement budgetStatement = connection.prepareStatement(budgetSql);
            budgetStatement.setString(1,"%\"" + id + "\"%");
            ResultSet budgetResult = budgetStatement.executeQuery();

            while (budgetResult.next()) {
                JSONObject budgetObject = new JSONObject();
                String budgetId = budgetResult.getString("id");
                budgetObject.put("id", budgetId);
                budgetObject.put("name", budgetResult.getString("name"));
                budgetObject.put("description", budgetResult.getString("description"));
                budgetObject.put("start", budgetResult.getDate("start"));
                budgetObject.put("end", budgetResult.getDate("end"));
                budgetObject.put("type", budgetResult.getString("type"));
                budgetObject.put("price", budgetResult.getDouble("price"));

                JSONArray dataArray;
                if (type.equals("item")) {
                    Item item = new Item();
                    dataArray = new JSONArray(item.getItemsByBudgetId(connection, budgetId));
                    budgetObject.put("item", dataArray);
                } else {
                    dataArray = getDataForBudget(connection, budgetId, type);
                    if (type.equals("income"))
                        budgetObject.put("income", dataArray);
                    else
                        budgetObject.put("expense", dataArray);
                }
                resultArray.put(budgetObject);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultArray;
    }

    private JSONArray getDataForBudget(Connection connection, String budgetId, String type) {
        JSONArray incomeArray = new JSONArray();
        String sql;
        if (type.equals("income"))
             sql = "SELECT * FROM income WHERE budget_id = ?";
        else
            sql = "SELECT * FROM expense WHERE budget_id = ?";

        try {
            PreparedStatement incomeStatement = connection.prepareStatement(sql);
            incomeStatement.setString(1, budgetId);
            ResultSet incomeResult = incomeStatement.executeQuery();

            while (incomeResult.next()) {
                String itemID = incomeResult.getString("item_id");
                Item item = new Item();
                JSONObject obj = item.getItemById(connection, itemID);
                JSONObject incomeObject = new JSONObject();
                incomeObject.put("id", incomeResult.getString("id"));
                incomeObject.put("item_id", itemID);
                incomeObject.put("item_name", obj.getString("name"));
                incomeObject.put("name", incomeResult.getString("name"));
                incomeObject.put("date", incomeResult.getDate("date"));
                incomeObject.put("amount", incomeResult.getDouble("amount"));
                incomeObject.put("accountant_id", incomeResult.getString("accountant_id"));

                incomeArray.put(incomeObject);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return incomeArray;
    }
}
