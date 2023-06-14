package Report;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class Report {
    public String getItemReport(Connection connection, String budgetId) {
        String result = "";
        String sql = "SELECT item.id AS item_id, item.name AS item_name, " +
                "COALESCE(SUM(income.amount), 0) AS total_income, " +
                "COALESCE(SUM(expense.amount), 0) AS total_expense, " +
                "COALESCE(SUM(income.amount), 0) - COALESCE(SUM(expense.amount), 0) AS profit " +
                "FROM item " +
                "LEFT JOIN income ON item.id = income.item_id " +
                "LEFT JOIN expense ON item.id = expense.item_id " +
                "WHERE item.budget_id = ? " +
                "GROUP BY item.id, item.name";

        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, budgetId);
            ResultSet res = statement.executeQuery();
            JSONArray jsonArray = new JSONArray();

            while (res.next()) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("item_id", res.getString("item_id"));
                jsonObject.put("item_name", res.getString("item_name"));
                jsonObject.put("total_income", res.getDouble("total_income"));
                jsonObject.put("total_expense", res.getDouble("total_expense"));
                jsonObject.put("profit", res.getDouble("profit"));
                jsonArray.put(jsonObject);
            }

            result = jsonArray.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }


    public String getBudgetReport(Connection connection) {
        JSONArray jsonArray = new JSONArray();
        String sql = "SELECT budget.id AS budget_id, budget.name AS budget_name, " +
                "COALESCE(SUM(income.amount), 0) AS total_income, " +
                "COALESCE(SUM(expense.amount), 0) AS total_expense, " +
                "COALESCE(SUM(income.amount), 0) - COALESCE(SUM(expense.amount), 0) AS profit " +
                "FROM budget " +
                "LEFT JOIN item ON budget.id = item.budget_id " +
                "LEFT JOIN income ON item.id = income.item_id " +
                "LEFT JOIN expense ON item.id = expense.item_id " +
                "GROUP BY budget.id, budget.name";

        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet res = statement.executeQuery();


            while (res.next()) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("budget_id", res.getString("budget_id"));
                jsonObject.put("budget_name", res.getString("budget_name"));
                jsonObject.put("total_income", res.getDouble("total_income"));
                jsonObject.put("total_expense", res.getDouble("total_expense"));
                jsonObject.put("profit", res.getDouble("profit"));
                jsonArray.put(jsonObject);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonArray.toString();
    }

    public JSONObject calculateBudgetAnalysis(Connection connection) {
        JSONObject jsonObject = new JSONObject();
        double income = 0;
        double expense = 0;
        int count = 0;
        try {
            Statement statement = connection.createStatement();

            // Query to calculate the total income for each budget
            String incomeQuery = "SELECT budget_id, SUM(amount) AS total_income FROM income GROUP BY budget_id";
            ResultSet incomeResult = statement.executeQuery(incomeQuery);

            // Store the total income for each budget in a map
            Map<String, Double> incomeMap = new HashMap<>();
            while (incomeResult.next()) {
                String budgetId = incomeResult.getString("budget_id");
                double totalIncome = incomeResult.getDouble("total_income");
                incomeMap.put(budgetId, totalIncome);
            }

            // Query to calculate the total expense for each budget
            String expenseQuery = "SELECT budget_id, SUM(amount) AS total_expense FROM expense GROUP BY budget_id";
            ResultSet expenseResult = statement.executeQuery(expenseQuery);

            // Store the total expense for each budget in a map
            Map<String, Double> expenseMap = new HashMap<>();
            while (expenseResult.next()) {
                String budgetId = expenseResult.getString("budget_id");
                double totalExpense = expenseResult.getDouble("total_expense");
                expenseMap.put(budgetId, totalExpense);
            }

            for (Map.Entry<String, Double> entry : incomeMap.entrySet()) {
                String budgetId = entry.getKey();
                double totalIncome = entry.getValue();
                double totalExpense = expenseMap.getOrDefault(budgetId, 0.0);

                double incomePercentage = (totalIncome / (totalIncome + totalExpense)) * 100;

                double expensePercentage = (totalExpense / (totalIncome + totalExpense)) * 100;

                income += incomePercentage;
                expense += expensePercentage;
                count += 1;
            }
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        jsonObject.put("income", income / count );
        jsonObject.put("expense", expense / count);
        jsonObject.put("count", count);
        return jsonObject;
    }

    public String analyis(Connection connection) {
        JSONObject obj1 = calculateBudgetAnalysis(connection);
        JSONObject obj2 = calculateWeeklyPercentage(connection);
        obj1.put("lastIncome", obj2.getDouble("lastIncome"));
        obj1.put("lastExpense", obj2.getDouble("lastExpense"));
        return obj1.toString();
    }

    public JSONObject calculateWeeklyPercentage(Connection connection) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("lastIncome",0);
        jsonObject.put("lastExpense",0);
        try {
            Statement statement = connection.createStatement();
            // Calculate the start and end dates of the current week
            LocalDate currentDate = LocalDate.now();
            LocalDate startOfWeek = currentDate.with(java.time.temporal.TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
            LocalDate endOfWeek = currentDate.with(java.time.temporal.TemporalAdjusters.nextOrSame(java.time.DayOfWeek.SUNDAY));

            // Query to calculate the total income and expense for each budget in the current week
            String query = "SELECT budget_id, SUM(amount) AS total_amount, 'income' AS type " +
                    "FROM income " +
                    "WHERE date BETWEEN '" + startOfWeek + "' AND '" + endOfWeek + "' " +
                    "GROUP BY budget_id " +
                    "UNION " +
                    "SELECT budget_id, SUM(amount) AS total_amount, 'expense' AS type " +
                    "FROM expense " +
                    "WHERE date BETWEEN '" + startOfWeek + "' AND '" + endOfWeek + "' " +
                    "GROUP BY budget_id";

            ResultSet resultSet = statement.executeQuery(query);

            // Store the total income and expense for each budget in a map
            double totalIncome = 0.0;
            double totalExpense = 0.0;
            int budgetCount = 0;

            while (resultSet.next()) {
                double totalAmount = resultSet.getDouble("total_amount");
                String type = resultSet.getString("type");

                if (type.equals("income")) {
                    totalIncome += totalAmount;
                } else if (type.equals("expense")) {
                    totalExpense += totalAmount;
                }

                budgetCount++;
            }

            // Calculate the percentage of income and expense for each budget
            double totalAmount = totalIncome + totalExpense;
            double incomePercentage = (totalIncome / totalAmount) * 100;
            double expensePercentage = (totalExpense / totalAmount) * 100;

            jsonObject.put("lastIncome",incomePercentage);
            jsonObject.put("lastExpense",expensePercentage);

            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return  jsonObject;
    }

}
