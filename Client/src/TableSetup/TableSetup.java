package TableSetup;

import Accountant.Accountant;
import Budget.Budget;
import Common.Common;
import User.User;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;

public class TableSetup {

    private JTable table;
    private JPanel container;
    private TableSetup.CustomTableModel tableModel;
    private String type;
    public JSONArray jsonArray;
    public JSONArray clickIncomeExpense;
    String[] columnNames;
    Accountant accountant;

    public TableSetup(String[] columnNames, Accountant accountant) {
        this.columnNames = columnNames;
        this.accountant = accountant;
        tableModel = new TableSetup.CustomTableModel(columnNames);

        table = new JTable(tableModel);

        table.setRowHeight(50);

        table.getColumnModel().getColumn(0).setCellRenderer(new TableSetup.ImageCellRenderer());

        JTableHeader tableHeader = table.getTableHeader();
        tableHeader.setBorder(new LineBorder(new Color(1, 102, 170), 1));
        tableHeader.setBackground(Color.WHITE);
        tableHeader.setForeground(new Color(1, 102, 170));
        tableHeader.setPreferredSize(new Dimension(240, 30));
        tableHeader.setFont(new Font("News Gothic MT", Font.PLAIN, 20));

        TableColumn firstColumn = table.getColumnModel().getColumn(0);
        firstColumn.setPreferredWidth(65);

        table.setShowHorizontalLines(false);
        table.setShowVerticalLines(false);
        table.setForeground(new Color(1, 102, 170));
        table.setBackground(Color.WHITE);

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int column = table.columnAtPoint(e.getPoint());
                int row = table.rowAtPoint(e.getPoint());
                if (column == 0) {
                    handlePhotoClick(row);
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBackground(Color.WHITE);
        scrollPane.setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        JViewport viewport = scrollPane.getViewport();
        viewport.setBackground(Color.WHITE);
        container = new JPanel();
        container.setBackground(Color.WHITE);
        container.setLayout(new BorderLayout());
        container.add(scrollPane, BorderLayout.CENTER);
    }

    public JPanel getContainer() {
        return this.container;
    }

    public void setJsonArray(JSONArray jsonArray) {
        this.jsonArray = jsonArray;
    }

    private void handlePhotoClick(int row) {
        if (this.type.equals("expense")) {
            this.accountant.ActionRightPlane.add(this.accountant.ViewExpenseDetail(row, this.jsonArray), "viewExpenseDetails");
            SwingUtilities.invokeLater(() -> {
                this.accountant.ActionRightCard.show(this.accountant.ActionRightPlane, "viewExpenseDetails");
            });
        }  else if (this.type.equals("income")) {
            this.accountant.ActionRightPlane.add(this.accountant.ViewIncomeDetail(row, this.jsonArray), "ViewIncomeDetail");
            SwingUtilities.invokeLater(() -> {
                this.accountant.ActionRightCard.show(this.accountant.ActionRightPlane, "ViewIncomeDetail");
            });
        } else if (this.type.equals("budget")) {
            this.accountant.ActionRightPlane.add(this.accountant.ViewBudgetDetail(this.jsonArray.getJSONObject(row)), "ViewBudgetDetail");
            SwingUtilities.invokeLater(() -> {
                this.accountant.ActionRightCard.show(this.accountant.ActionRightPlane, "ViewBudgetDetail");
            });
        } else if (this.type.equals("item")) {
            this.accountant.ActionRightPlane.add(this.accountant.ViewItemDetail(row, this.jsonArray), "ViewItemDetail");
            SwingUtilities.invokeLater(() -> {
                this.accountant.ActionRightCard.show(this.accountant.ActionRightPlane, "ViewItemDetail");
            });
        } else if (this.type.equals("incomeDetail")) {
            JSONObject obj = this.clickIncomeExpense.getJSONObject(row);

            try {
                String data =  Common.jsonify("api/incomes/" + obj.getString("id"), "delete", this.accountant.user.toJson());
                this.accountant.writer.println(data);
                data = this.accountant.reader.readLine();
            } catch (Exception e) {
                e.printStackTrace();
            }

            SwingUtilities.invokeLater(() -> {
                SwingUtilities.invokeLater(() -> {
                    this.accountant.ActionLeftCard.show(this.accountant.ActionLeftPlane,  "ItemsInfo");
                    this.accountant.ActionRightCard.show(this.accountant.ActionRightPlane, "ViewTemp");
                });
            });
        } else if (this.type.equals("expenseDetail")) {
            JSONObject obj = this.clickIncomeExpense.getJSONObject(row);

            try {
                String data =  Common.jsonify("api/expenses/" + obj.getString("id"), "delete", this.accountant.user.toJson());
                this.accountant.writer.println(data);
                data = this.accountant.reader.readLine();
            } catch (Exception e) {
                e.printStackTrace();
            }

            SwingUtilities.invokeLater(() -> {
                SwingUtilities.invokeLater(() -> {
                    this.accountant.ActionLeftCard.show(this.accountant.ActionLeftPlane,  "ItemsInfo");
                    this.accountant.ActionRightCard.show(this.accountant.ActionRightPlane, "ViewTemp");
                });
            });
        }

    }

    public void addRowAccountant(JSONObject jsonObject, String type) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

         if(type.equals("budgets")) {
            Budget budget = new Budget();
            budget.fromJson(jsonObject.toString());
            tableModel.addRow(new Object[]{new ImageIcon(budget.getName()), dateFormat.format(budget.getStart()),budget.getType(), budget.getPrice()});
            }   else if(type.equals("incomes") || type.equals("expenses") ) {
            Budget budget = new Budget();
            budget.fromJson(jsonObject.toString());
            JSONArray incomeArray = new JSONArray();
            if (type.equals("incomes"))
                incomeArray = jsonObject.getJSONArray("income");
            else
                incomeArray = jsonObject.getJSONArray("expense");
            double total = 0;
            for (int i = 0; i < incomeArray.length(); i++) {
                JSONObject object = incomeArray.getJSONObject(i);
                total += object.getDouble("amount");
            }
            tableModel.addRow(new Object[]{new ImageIcon(budget.getName()), budget.getType(), total});
        } else if (type.equals("expenseDetail")) {
            tableModel.addRow(new Object[]{new ImageIcon(jsonObject.getString("name")), jsonObject.getString("item_name"),
                    jsonObject.getString("date"), jsonObject.getDouble("amount") });
        } else if (type.equals("incomeDetail")) {
            tableModel.addRow(new Object[]{new ImageIcon(jsonObject.getString("name")), jsonObject.getString("item_name"),
                    jsonObject.getString("date"), jsonObject.getDouble("amount") });
        } else if (type.equals("items")) {
            Budget budget = new Budget();
            budget.fromJson(jsonObject.toString());
            JSONArray itemArray = jsonObject.getJSONArray("item");
            tableModel.addRow(new Object[]{new ImageIcon(budget.getName()), budget.getType(), itemArray.length()});
        } else if (type.equals("itemDetail")) {
             tableModel.addRow(new Object[]{new ImageIcon(jsonObject.getString("name")), jsonObject.getString("budgetName")});
         }

    }

    public  void BudgetView(BufferedReader reader, PrintWriter writer, User user) {
        this.type = "budget";
        String data = Common.jsonify("api/users/" + user.getId() + "/budgets", "get", user.toJson());
        writer.println(data);
        try {
            data = reader.readLine();
            String[] array = data.split(" ", 2);
            if (array[0].equals("200")) {
                JSONArray jsonArray = new JSONArray(array[1]);
                this.jsonArray = jsonArray;
                for (int i = 0; i < jsonArray.length(); i++)
                    this.addRowAccountant(jsonArray.getJSONObject(i), "budgets");
            }
            else
                System.out.println("Failed");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public  void ViewIncome(BufferedReader reader, PrintWriter writer, User user) {
        String data = Common.jsonify("api/budgets/incomes/" + user.getId(), "get", user.toJson());
        this.type = "income";
        writer.println(data);
        try {
            data = reader.readLine();
            String[] array = data.split(" ", 2);
            if (array[0].equals("200")) {
                JSONArray jsonArray = new JSONArray(array[1]);
                this.jsonArray = jsonArray;
                for (int i = 0; i < jsonArray.length(); i++)
                    this.addRowAccountant(jsonArray.getJSONObject(i), "incomes");
            }
            else
                System.out.println("Failed");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public  void ViewItems(BufferedReader reader, PrintWriter writer, User user) {
        String data = Common.jsonify("api/budgets/items/" + user.getId(), "get", user.toJson());
        this.type = "item";
        writer.println(data);
        try {
            data = reader.readLine();
            System.out.println(data);
            String[] array = data.split(" ", 2);
            if (array[0].equals("200")) {
                JSONArray jsonArray = new JSONArray(array[1]);
                this.jsonArray = jsonArray;
                for (int i = 0; i < jsonArray.length(); i++)
                    this.addRowAccountant(jsonArray.getJSONObject(i), "items");
            }
            else
                System.out.println("Failed");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public  void ViewExpense(BufferedReader reader, PrintWriter writer, User user) {
        String data = Common.jsonify("api/budgets/expenses/" + user.getId(), "get", user.toJson());
        this.type = "expense";
        writer.println(data);
        try {
            data = reader.readLine();
            String[] array = data.split(" ", 2);
            if (array[0].equals("200")) {
                JSONArray jsonArray = new JSONArray(array[1]);
                this.jsonArray = jsonArray;
                for (int i = 0; i < jsonArray.length(); i++)
                    this.addRowAccountant(jsonArray.getJSONObject(i), "expenses");
            }
            else
                System.out.println("Failed");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public  void ViewExpenseDetail(int index) {
        this.type = "expenseDetail";
        JSONObject data = this.jsonArray.getJSONObject(index);
        JSONArray expenseArray = data.getJSONArray("expense");
        this.clickIncomeExpense = expenseArray;
        for (int i = 0; i < expenseArray.length(); i++) {
            JSONObject object = expenseArray.getJSONObject(i);
            this.addRowAccountant(object, "expenseDetail");
        }
    }

    public  void ViewItemDetail(int index) {
        this.type = "itemDetail";
        JSONObject data = this.jsonArray.getJSONObject(index);
        JSONArray itemArray = data.getJSONArray("item");
        String name = data.getString("name");
        for (int i = 0; i < itemArray.length(); i++) {
            JSONObject object = itemArray.getJSONObject(i);
            object.put("budgetName", name);
            this.addRowAccountant(object, "itemDetail");
        }
    }

    public  void ViewIncomeDetail(int index) {
        this.type = "incomeDetail";
        JSONObject data = this.jsonArray.getJSONObject(index);
        JSONArray expenseArray = data.getJSONArray("income");
        this.clickIncomeExpense = expenseArray;
        for (int i = 0; i < expenseArray.length(); i++) {
            JSONObject object = expenseArray.getJSONObject(i);
            this.addRowAccountant(object, "incomeDetail");
        }
    }


    class CustomTableModel extends DefaultTableModel {

        public CustomTableModel(String[] columnNames) {
            super(columnNames, 0);
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            if (columnIndex == 0) {
                return ImageIcon.class;
            }
            return super.getColumnClass(columnIndex);
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    }

    class ImageCellRenderer extends DefaultTableCellRenderer {
        public ImageCellRenderer() {
            setHorizontalAlignment(SwingConstants.CENTER);
        }
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            ImageIcon icon = (ImageIcon) value;
            Image image = icon.getImage().getScaledInstance(45, 45, Image.SCALE_SMOOTH);
            label.setIcon(new ImageIcon(image));

            return label;
        }
    }
}
