package Budget;

import Common.Common;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Budget {
    private String id;
    private String name;
    private String description;
    private Date start;
    private Date end;
    private JSONArray accountantIds = new JSONArray();
    private String type;
    private double price;

    public Budget() {
        this.id = Common.CreateId();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Date getStart() {
        return start;
    }

    public Date getEnd() {
        return end;
    }

    public JSONArray getAccountantIds() {
        return accountantIds;
    }

    public String getType() {
        return type;
    }

    public double getPrice() {
        return price;
    }

    public void setBudget(String name, String description, Date start, Date end, String accountant, String type, Double price) {
        try {
            this.name = name;
            this.description = description;
            this.start = start;
            this.end = end;
            this.accountantIds.put(accountant);
            this.type = type;
            this.price = price;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setId(String id) {
        this.id = id;
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

    public void fromJson(String data) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            JSONObject obj = new JSONObject(data);
            this.id = obj.getString("id");
            this.name = obj.getString("name");
            this.description = obj.getString("description");
            this.start = dateFormat.parse(obj.getString("start"));
            this.end = dateFormat.parse(obj.getString("end"));
            try {
                this.accountantIds = obj.getJSONArray("accountant_ids");
            } catch (Exception e) {}
            this.type = obj.getString("type");
            this.price = obj.getDouble("price");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
