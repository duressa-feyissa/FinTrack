package Item;

import Common.Common;
import org.json.JSONArray;
import org.json.JSONObject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Item {
    private String id;
    private String name;
    private String budgetId;
    private String image = "";

    public Item() {
        this.id = Common.CreateId();
    }

    public Item(String name, String budgetId) {
        this.id = Common.CreateId();
        this.name = name;
        this.budgetId = budgetId;
    }

    public Item(String name, String budgetId, String image) {
        this.id = Common.CreateId();
        this.name = name;
        this.budgetId = budgetId;
        this.image = image;
    }


    public void setId(String id) {
        this.id = id;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public boolean createItem(Connection connection) {
        try {
            String sql = "INSERT INTO item (id, name, budget_id, image) VALUES (?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, this.id);
            statement.setString(2, this.name);
            statement.setString(3, this.budgetId);
            statement.setString(4, this.image);

            int rowsAffected = statement.executeUpdate();
            connection.commit();
            return rowsAffected == 1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public JSONObject getItemById(Connection connection, String itemId) {
        JSONObject jsonObject = new JSONObject();
        String sql = "SELECT * FROM item WHERE id = ?";
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, itemId);
            ResultSet res = statement.executeQuery();
            if (res.next()) {
                jsonObject.put("id", res.getString("id"));
                jsonObject.put("name", res.getString("name"));
                jsonObject.put("budget_id", res.getString("budget_id"));
                jsonObject.put("image", res.getString("image"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public String getItemsByBudgetId(Connection connection, String budgetId) {
        String result = "";
        String sql = "SELECT * FROM item WHERE budget_id = ?";
        JSONArray jsonArray = new JSONArray();

        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, budgetId);
            ResultSet res = statement.executeQuery();
            while (res.next()) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("id", res.getString("id"));
                jsonObject.put("name", res.getString("name"));
                jsonObject.put("budget_id", res.getString("budget_id"));
                jsonObject.put("image", res.getString("image"));
                jsonArray.put(jsonObject);
            }
            result = jsonArray.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public boolean updateItem(Connection connection, String itemId) {
        String sql = "UPDATE item SET name = ?, budget_id = ?, image = ? WHERE id = ?";
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, this.name);
            statement.setString(2, this.budgetId);
            statement.setString(3, this.image);
            statement.setString(4, itemId);

            int rowsAffected = statement.executeUpdate();
            return rowsAffected == 1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteItem(Connection connection, String itemId) {
        String sql = "DELETE FROM item WHERE id = ?";
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, itemId);

            int rowsAffected = statement.executeUpdate();
            return rowsAffected == 1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public JSONObject toJson() {
        JSONObject item = new JSONObject();
        item.put("id", this.id);
        item.put("name", this.name);
        item.put("budget_id", this.budgetId);
        item.put("image", this.image);
        return item;
    }

}
