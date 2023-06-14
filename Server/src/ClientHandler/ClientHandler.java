package ClientHandler;
import Budget.Budget;
import DB.DB;
import Email.Email;
import Report.Report;
import Users.User;
import Item.*;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import Expense.*;
import Income.*;
import java.util.Arrays;

public class ClientHandler implements Runnable {
    public DB db = new DB("Fintrack", "shupi", "Dureti");
    public User user;
    private final Socket clientSocket;
    private BufferedReader reader;
    private PrintWriter writer;

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
        this.user = new User();
    }

    @Override
    public void run() {
        try {
            // Setup input and output streams
            reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            writer = new PrintWriter(clientSocket.getOutputStream(), true);

            String request = "";

            while ((request = reader.readLine()) != null) {
                processRequest(request);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null)
                    reader.close();
                if (writer != null)
                    writer.close();
                clientSocket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void processRequest(String request) {
        JSONObject json = new JSONObject(request);

        String[] paths = json.getString("path").split("/");
        String method = json.getString("method");
        JSONObject dataJson = new JSONObject(json.getString("data"));

        System.out.println(Arrays.toString(paths));
        System.out.println("Method: " + method);

        switch (paths[1]) {
            case "auth":
                handleAuth(paths, method, dataJson);
                break;
            case "users":
                handleUser(paths, method, dataJson);
                break;
            case "budgets":
                handleBudget(paths, method, dataJson);
                break;
            case "incomes":
                handleIncome(paths, method, dataJson);
                break;
            case "expenses":
                handleExpense(paths, method, dataJson);
                break;
            case "items":
                handleItem(paths, method, dataJson);
                break;
            case "reports":
                handleReport(paths, method, dataJson);
                break;
            default:
                this.sendResponse(404, "Not found!");
        }
    }

    private void handleAuth(String[] paths, String method, JSONObject dataJson) {
        if (paths.length == 2 && method.equals("post")) {
            this.user = new User();
            this.user = user.authenticateUser(db.getConnection(), dataJson.getString("email"), dataJson.getString("password"));
            if (this.user != null) {
                this.sendResponse(200, this.user.toJson().toString());
            } else {
                this.sendResponse(401, "Authentication faield!");
            }

        } else {
            this.sendResponse(404, "Not found!");
        }
    }

    protected void sendResponse(int statusCode, String message) {
        String response = statusCode + " " + message;
        writer.println(response);
    }

    boolean authHandler(String method, JSONObject dataJson) {
        if (method.equals("post")) {
            this.user = new User();
            this.user = this.user.authenticateUser(this.db.getConnection(), dataJson.getString("email"), dataJson.getString("password"));
            if (this.user != null) {
                this.sendResponse(200, this.user.toJson().toString());
                return true;
            }
            else
                this.sendResponse(401, "Authentication failed. Access denied.");
        }
        return false;
    }

    void handleUser(String[] paths, String method, JSONObject dataJson) {
        if (method.equals("post") && paths.length == 2) {
            this.user.setUser(dataJson.getString("username"), dataJson.getString("email"), dataJson.getString("password"), dataJson.getBoolean("isAdmin"));
            if (this.user.CreateNewUser(this.db.getConnection()))
                this.sendResponse(200, user.toJson().toString());
            else
                this.sendResponse(400, "User Already Registered!");
        } else if (method.equals("post") && paths[2].equals("temp") && paths.length == 3) {
            if (Email.emailHandler(this.db.getConnection(), dataJson.getString("email")))
                this.sendResponse(200, "{}");
            else
                this.sendResponse(400, "Not found");
        } else if (method.equals("post") && paths[2].equals("check") && paths.length == 3) {
            if (Email.verifyCode(this.db.getConnection(), dataJson.getString("email"), dataJson.getInt("code")))
                this.sendResponse(200, "{}");
            else
                this.sendResponse(400, "Not found");
        } else if (method.equals("get") && paths.length == 2) {
            String result = this.user.getAllAccountants(this.db.getConnection());
            if (!result.isEmpty())
                this.sendResponse(200, result);
            else
                this.sendResponse(401, "Accountant list is empty!");
        } else if (method.equals("get") && paths.length == 3) {
            this.user.setId(paths[2]);
            JSONObject jsonObject = this.user.getAccountantById(this.db.getConnection());
            if (jsonObject != null)
                this.sendResponse(200, jsonObject.toString());
            else
                this.sendResponse(401, "The accountant does not found!");
        } else if (method.equals("put") && paths.length == 3) {
            this.user.setId(paths[2]);
            this.user.setUser(dataJson.getString("username"), dataJson.getString("email"), dataJson.getString("password"), dataJson.getBoolean("isAdmin"));
            if (this.user.updateUser(this.db.getConnection()))
                this.sendResponse(200, "Accountant was successfully updated!");
            else
                this.sendResponse(401, "The user does not found!");
         } else if (method.equals("delete") && paths.length == 3) {
            this.user.setId(paths[2]);
            if (this.user.deleteUser(this.db.getConnection()))
                this.sendResponse(200, "Accountant was successfully delete!");
            else
                this.sendResponse(401, "The expense does not found!");
        } else if (method.equals("get") && paths.length == 4 && paths[3].equals("budgets")) {
            this.user.setId(paths[2]);
            String result = this.user.getBudgetsByAccountant(this.db.getConnection());
            if (!result.isEmpty())
                this.sendResponse(200, result);
            else
                this.sendResponse(401, "The no budget related to this accountant!");
        } else if (method.equals("get") && paths.length == 4 && paths[2].equals("budgets")) {
            this.user.setId(paths[2]);
            String result = this.user.getAllAccountantsBudget(this.db.getConnection(), paths[3]);
            if (!result.isEmpty())
                this.sendResponse(200, result);
            else
                this.sendResponse(401, "The no budget related to this accountant!");
        } else if (method.equals("get") && paths.length == 5 && paths[2].equals("budgets")) {
            this.user.setId(paths[2]);
            String result = this.user.getAllAccountantsInBudget(this.db.getConnection(), paths[3]);
            if (!result.isEmpty())
                this.sendResponse(200, result);
            else
                this.sendResponse(401, "The no budget related to this accountant!");
        } else {
            this.sendResponse(401, "Not found!");
        }
    }

    void handleBudget(String[] paths, String method, JSONObject jsonObject) {
        Budget budget = new Budget();

        if (method.equals("post") && paths.length == 2) {
            budget.setBudget(jsonObject.getString("id"), jsonObject.getString("name"),
                    jsonObject.getString("description"), jsonObject.getString("start"),
                    jsonObject.getString("end"));
            JSONArray accountantIds = jsonObject.getJSONArray("accountant_ids");

            String type = jsonObject.getString("type");
            double price = jsonObject.getDouble("price");
            if (budget.createBudget(this.db.getConnection(), accountantIds, type, price))
                this.sendResponse(200, budget.toJson().toString());
            else
                this.sendResponse(401, "Budget not created!");
        } else if (method.equals("get") && paths.length == 2) {
            String result = budget.getAllBudget(this.db.getConnection());
            if (!result.isEmpty())
                this.sendResponse(200, result);
            else
                this.sendResponse(401, "Budget list is empty!");
        } else if (method.equals("get") && paths.length == 3 && paths[2].equals("incomes")) {
            JSONArray jsonArray = budget.getAllBudgetsWithIncome(this.db.getConnection(), "income");
            if (jsonArray != null)
                this.sendResponse(200, jsonArray.toString());
            else
                this.sendResponse(401, "The income was not found!");
        } else if (method.equals("get") && paths.length == 3 && paths[2].equals("expenses")) {
            JSONArray jsonArray = budget.getAllBudgetsWithIncome(this.db.getConnection(), "expense");
            if (jsonArray != null)
                this.sendResponse(200, jsonArray.toString());
            else
                this.sendResponse(401, "The income was not found!");
        }  else if (method.equals("get") && paths.length == 4 && paths[2].equals("expenses")) {
            JSONArray jsonArray = budget.getAllBudgetsWithAccountant(this.db.getConnection(), "expense", paths[3]);
            if (jsonArray != null)
                this.sendResponse(200, jsonArray.toString());
            else
                this.sendResponse(401, "The expense was not found!");
        }  else if (method.equals("get") && paths.length == 4 && paths[2].equals("items")) {
            JSONArray jsonArray = budget.getAllBudgetsWithAccountant(this.db.getConnection(), "item", paths[3]);
            if (jsonArray != null)
                this.sendResponse(200, jsonArray.toString());
            else
                this.sendResponse(401, "The expense was not found!");
        } else if (method.equals("get") && paths.length == 4 && paths[2].equals("incomes")) {
            JSONArray jsonArray = budget.getAllBudgetsWithAccountant(this.db.getConnection(), "income", paths[3]);
            if (jsonArray != null)
                this.sendResponse(200, jsonArray.toString());
            else
                this.sendResponse(401, "The expense was not found!");
        } else if (method.equals("get") && paths.length == 3) {
            budget.setId(paths[2]);
            String result = budget.getBudget(this.db.getConnection());
            if (!result.isEmpty())
                this.sendResponse(200, result);
            else
                this.sendResponse(401, "The budget was not found!");
        } else if (method.equals("put") && paths.length == 3) {
            budget.setBudget(jsonObject.getString("id"), jsonObject.getString("name"),
                    jsonObject.getString("description"), jsonObject.getString("start"),
                    jsonObject.getString("end"));
            JSONArray accountantIds = jsonObject.getJSONArray("accountant_ids");
            if (budget.updateBudget(this.db.getConnection()))
                this.sendResponse(200, budget.toJson().toString());
            else
                this.sendResponse(401, "The budget was not found!");
        } else if (method.equals("delete") && paths.length == 3) {
            budget.setId(paths[2]);
            if (budget.deleteBudget(this.db.getConnection()))
                this.sendResponse(200, "The budget was deleted successfully!");
            else
                this.sendResponse(401, "The budget was not found!");
        }  else if (method.equals("post") && paths.length == 4 && paths[2].equals("accountants") && paths[3].equals("add")) {
            if (budget.addAccountant(this.db.getConnection(), jsonObject.getString("id"),jsonObject.getString("accountantId")))
                this.sendResponse(200, "The new accountant added successfully!");
            else
                this.sendResponse(401, "The new accountant does not added successfully!");
        } else if (method.equals("post") && paths.length == 4 && paths[2].equals("accountants") && paths[3].equals("remove")) {
            if (budget.removeAccountant(this.db.getConnection(), jsonObject.getString("id"),jsonObject.getString("accountantId")))
                this.sendResponse(200, "The accountant removed successfully!");
            else
                this.sendResponse(401, "The accountant does not removed successfully!");
        }  else {
            this.sendResponse(401, "Not found!");
        }
    }

    void handleIncome(String[] paths, String method, JSONObject jsonObject) {
        Income income = new Income();

        if (method.equals("post") && paths.length == 2) {
            income.setIncome(jsonObject.getString("item_id"),
                    jsonObject.getString("name"), jsonObject.getString("date"), jsonObject.getDouble("amount"),
                    jsonObject.getString("budget_id"), jsonObject.getString("accountant_id"));
            if (income.createIncome(this.db.getConnection()))
                this.sendResponse(200, income.toJson().toString());
            else
                this.sendResponse(401, "Income not created!");
        } else if (method.equals("get") && paths.length == 2) {
            String result = income.getAllIncome(this.db.getConnection());
            if (!result.isEmpty())
                this.sendResponse(200, result);
            else
                this.sendResponse(401, "Income list is empty!");
        } else if (method.equals("get") && paths.length == 3) {
            income.setId(paths[2]);
            String result = income.getIncome(this.db.getConnection());
            if (!result.isEmpty())
                this.sendResponse(200, result);
            else
                this.sendResponse(401, "The income was not found!");
        } else if (method.equals("put") && paths.length == 3) {
            income.setId(paths[2]);
            income.setIncome(jsonObject.getString("item_id"),
                    jsonObject.getString("name"), jsonObject.getString("date"), jsonObject.getDouble("amount"),
                    jsonObject.getString("budget_id"), jsonObject.getString("accountant_id"));
            if (income.updateIncome(this.db.getConnection()))
                this.sendResponse(200, income.toJson().toString());
            else
                this.sendResponse(401, "The income was not found!");
        } else if (method.equals("delete") && paths.length == 3) {
            income.setId(paths[2]);
            if (income.deleteIncome(this.db.getConnection()))
                this.sendResponse(200, income.toJson().toString());
            else
                this.sendResponse(401, "The income was not found!");
        } else {
            this.sendResponse(401, "Not found!");
        }
    }

    void handleExpense(String[] paths, String method, JSONObject jsonObject) {
        Expense expense = new Expense();

        if (method.equals("post") && paths.length == 2) {
            expense.setExpense(jsonObject.getString("item_id"),
                    jsonObject.getString("name"), jsonObject.getString("date"), jsonObject.getDouble("amount"),
                    jsonObject.getString("budget_id"), jsonObject.getString("accountant_id"));
            if (expense.createExpense(this.db.getConnection()))
                this.sendResponse(200, expense.toJson().toString());
            else
                this.sendResponse(401, "Expense not created!");
        } else if (method.equals("get") && paths.length == 2) {
            String result = expense.getAllExpenses(this.db.getConnection());
            if (!result.isEmpty())
                this.sendResponse(200, result);
            else
                this.sendResponse(401, "Expense list is empty!");
        } else if (method.equals("get") && paths.length == 3) {
            expense.setId(paths[2]);
            String result = expense.getExpense(this.db.getConnection());
            if (!result.isEmpty())
                this.sendResponse(200, result);
            else
                this.sendResponse(401, "The expense does not found!");
        } else if (method.equals("put") && paths.length == 3) {
            expense.setId(paths[2]);
            expense.setExpense(jsonObject.getString("item_id"),
                    jsonObject.getString("name"), jsonObject.getString("date"), jsonObject.getDouble("amount"),
                    jsonObject.getString("budget_id"), jsonObject.getString("accountant_id"));
            if (expense.updateExpense(this.db.getConnection()))
                this.sendResponse(200, "Expense was successfully updated!");
            else
                this.sendResponse(401, "The expense does not found!");
        } else if (method.equals("delete") && paths.length == 3) {
            expense.setId(paths[2]);
            if (expense.deleteExpense(this.db.getConnection()))
                this.sendResponse(200, "Expense was successfully delete!");
            else
                this.sendResponse(401, "The expense does not found!");
        } else {
            this.sendResponse(401, "Not found!");
        }
    }

    void handleItem(String[] paths, String method, JSONObject dataJson) {
        if (method.equals("post") && paths.length == 2) {
            String name = dataJson.getString("name");
            String budgetId = dataJson.getString("budget_id");
            Item item = new Item(name, budgetId);
            if (item.createItem(this.db.getConnection()))
                this.sendResponse(200, item.toJson().toString());
            else
                this.sendResponse(400, "Failed to create item!");
        } else if (method.equals("get") && paths.length == 4 && paths[2].equals("budget")) {
            Item item = new Item();
            String result = item.getItemsByBudgetId(this.db.getConnection(), paths[3]);
            if (!result.isEmpty())
                this.sendResponse(200, result);
            else
                this.sendResponse(401, "No items found for the budget!");
        } else if (method.equals("get") && paths.length == 3) {
            Item item = new Item();
            String result = item.getItemById(this.db.getConnection(), paths[2]).toString();
            if (!result.isEmpty())
                this.sendResponse(200, result);
            else
                this.sendResponse(401, "Item not found!");
        } else if (method.equals("put") && paths.length == 3) {
            String itemId = paths[2];
            String name = dataJson.getString("name");
            String budgetId = dataJson.getString("budget_id");
            String image = dataJson.getString("image");

            Item item = new Item(name, budgetId, image);
            item.setId(itemId);

            if (item.updateItem(this.db.getConnection(), itemId))
                this.sendResponse(200, "Item successfully updated!");
            else
                this.sendResponse(401, "Item not found!");
        } else if (method.equals("delete") && paths.length == 3) {
            Item item = new Item();
            String itemId = paths[2];
            if (item.deleteItem(this.db.getConnection(), itemId))
                this.sendResponse(200, "Item successfully deleted!");
            else
                this.sendResponse(401, "Item not found!");
        } else {
            this.sendResponse(401, "Not found!");
        }
    }

    void handleReport(String[] paths, String method, JSONObject dataJson) {
        if (method.equals("get") && paths.length == 4 && paths[2].equals("budgets")) {
            Report report = new Report();
            String data = String.valueOf(report.getItemReport(this.db.getConnection(), paths[3]));
            this.sendResponse(200, data);
        } else if (method.equals("get") && paths.length == 3 && paths[2].equals("budgets")) {
            Report report = new Report();
            String data = String.valueOf(report.getBudgetReport(this.db.getConnection()));
            this.sendResponse(200, data);
        } else if (method.equals("get") && paths.length == 3 && paths[2].equals("analysis")) {
            Report report = new Report();
            String data = String.valueOf(report.analyis(this.db.getConnection()));
            this.sendResponse(200, data);
        }
    }
}