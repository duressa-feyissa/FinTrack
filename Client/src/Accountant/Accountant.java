package Accountant;

import Common.Common;
import CommonLayout.CommonLayout;
import RoundedBorder.RoundedBorder;
import TableSetup.TableSetup;
import User.User;
import net.sourceforge.jdatepicker.impl.JDatePickerImpl;
import org.json.JSONArray;
import org.json.JSONObject;
import Login.Login;
import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class Accountant extends CommonLayout {
    public Common common;
    public User user;
    public BufferedReader reader;
    public PrintWriter writer;
    public Login login;
    public JPanel ActionLeftPlane;
    public JPanel ActionRightPlane;
    public CardLayout ActionLeftCard;
    public CardLayout ActionRightCard;

    public Accountant(User user, BufferedReader reader, PrintWriter writer, String path) {
        super(user, path);
        this.user = user;
        this.reader = reader;
        this.writer = writer;

        ActionLeftCard = new CardLayout();
        ActionRightCard = new CardLayout();
        ActionLeftPlane = new JPanel(ActionLeftCard);
        ActionRightPlane = new JPanel(ActionRightCard);
        ActionRightPlane.setOpaque(false);
        ActionLeftPlane.setOpaque(false);
        ActionRightPlane.setBackground(Color.WHITE);
        ActionRightPlane.setBackground(Color.WHITE);

        this.progress.setPreferredSize(new Dimension(530, 100));
        ManageItems();
        setUpBottomPart();


        this.actions.add(ActionLeftPlane);
        this.actions.add(ActionRightPlane);
    }

    public void setLogin(Login login) {
        this.login = login;
    }


    private void setUpBottomPart() {

        JPanel panel1 = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel1.setOpaque(false);
        panel1.setPreferredSize(new Dimension(leftPanel.getWidth(), 100));
        bottomPart.add(panel1, BorderLayout.NORTH);

        JPanel panel2 = new JPanel();
        JPanel center = new JPanel(new GridLayout(7, 1, 1, 9));

        JPanel home = setUpIcon("Home", path + "src/image/profile.png");
        JPanel budgets = setUpIcon("Budgets", path + "src/image/budget.png");
        JPanel items = setUpIcon("Items", path + "src/image/item.png");
        JPanel income = setUpIcon("Incomes", path + "src/image/income.png");
        JPanel expense = setUpIcon("Expenses",  path + "src/image/expense.png");
        JPanel report = setUpIcon("Reports", path + "src/image/report.png");
        JPanel logout = setUpIcon("Logout", path + "src/image/logout.png");

        center.add(home);
        center.add(budgets);
        center.add(items);
        center.add(income);
        center.add(expense);
        center.add(logout);
        center.setOpaque(false);
        center.setPreferredSize(new Dimension(250, 450));

        home.addMouseListener(createMouseListener("Home"));
        budgets.addMouseListener(createMouseListener("Budgets"));
        items.addMouseListener(createMouseListener("Items"));
        income.addMouseListener(createMouseListener("Incomes"));
        expense.addMouseListener(createMouseListener("Expenses"));
        report.addMouseListener(createMouseListener("Reports"));
        logout.addMouseListener(createMouseListener("Logout"));

        panel2.add(center);
        panel2.setOpaque(false);
        bottomPart.add(panel2, BorderLayout.CENTER);

        JPanel panel3 = new JPanel();
        panel3.setOpaque(false);
        panel3.setPreferredSize(new Dimension(leftPanel.getWidth(), 50));
        bottomPart.add(panel3, BorderLayout.SOUTH);
    }

    JPanel setUpIcon(String text, String path) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(100, 50));

        ImageIcon icon = Common.resizeImage(new ImageIcon(path), 50);
        JLabel imageHolder = new JLabel();
        imageHolder.setIcon(icon);

        JLabel label = new JLabel(text);
        label.setFont(new Font("Roboto", Font.PLAIN, 22));
        label.setForeground(Color.WHITE);
        panel.add(label, BorderLayout.CENTER);
        panel.add(imageHolder, BorderLayout.EAST);
        return panel;
    }

    private MouseAdapter createMouseListener(String iconName) {
        return new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleIconClick(iconName);
            }
        };
    }

    private void handleIconClick(String iconName) {
        if (iconName.equals("Home")) {
            this.currentRoute.setText("Home");
            this.ManageHome();
            SwingUtilities.invokeLater(() -> {
                ActionLeftCard.show(this.ActionLeftPlane, "ViewProFile");
                ActionRightCard.show(this.ActionRightPlane, "ViewTemp");
            });
        } else if (iconName.equals("Items")) {
            this.currentRoute.setText("Items");
            this.ManageItems();
            SwingUtilities.invokeLater(() -> {
                ActionLeftCard.show(this.ActionLeftPlane, "ItemsInfo");
                ActionRightCard.show(this.ActionRightPlane, "ViewTemp");
            });
        } else if (iconName.equals("Budgets")) {
            this.currentRoute.setText("Budgets");
            this.ManageBudget();
            SwingUtilities.invokeLater(() -> {
                this.ActionLeftCard.show(this.ActionLeftPlane, "viewBudget");
                this.ActionRightCard.show(this.ActionRightPlane, "ViewTemp");
            });
        } else if (iconName.equals("Incomes")) {
            this.currentRoute.setText("Incomes");
            this.ManageIncome();
            SwingUtilities.invokeLater(() -> {
                ActionLeftCard.show(this.ActionLeftPlane, "viewIncome");
                ActionRightCard.show(this.ActionRightPlane, "ViewTemp");
            });
        } else if (iconName.equals("Expenses")) {
            this.currentRoute.setText("Expenses");
            this.ManageExpense();
            SwingUtilities.invokeLater(() -> {
                ActionLeftCard.show(this.ActionLeftPlane, "viewExpense");
                ActionRightCard.show(this.ActionRightPlane, "ViewTemp");
            });
        }  else if (iconName.equals("Logout")) {
            this.currentRoute.setText("Logouts");
            this.login.SignIn();
            SwingUtilities.invokeLater(() -> {
                this.login.cardLayoutMain.show(this.login.cardPanelMain, "container");
            });
        }
    }

    void ManageHome() {
        ActionLeftPlane.add(this.ViewProFile(), "ViewProFile");
        ActionRightPlane.add(this.ViewTemp(), "ViewTemp");
    }

    void ManageItems() {
        ActionLeftPlane.add(this.ItemsInfo(),  "ItemsInfo");
        ActionRightPlane.add(this.ViewTemp(), "ViewTemp");
    }

    void ManageBudget() {
        ActionLeftPlane.add(this.ViewBuget(), "viewBudget");
        ActionRightPlane.add(this.ViewTemp(), "ViewTemp");
    }

    void ManageIncome() {
        ActionLeftPlane.add(this.ViewInCome(), "viewIncome");
        ActionRightPlane.add(this.ViewTemp(), "ViewTemp");
    }

    void ManageExpense() {
        ActionLeftPlane.add(this.ViewExpense(), "viewExpense");
        ActionRightPlane.add(this.ViewTemp(), "ViewTemp");
    }


    JPanel ItemsInfo() {
        JPanel container = new JPanel(new BorderLayout(40, 25));
        JPanel header = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel message = new JLabel("View Item");
        message.setForeground(Color.WHITE);
        message.setFont(new Font("News Gothic MT", Font.BOLD, 25));
        message.setOpaque(false);
        container.setOpaque(false);
        header.setBackground(new Color(1, 102, 170));

        JPanel panel;
        String[] column = {"Name", "Type", "Items"};
        TableSetup table = new TableSetup(column, this);
        table.ViewItems(this.reader, this.writer, this.user);
        panel = table.getContainer();

        header.add(message);
        container.add(header, BorderLayout.NORTH);
        container.add(panel, BorderLayout.CENTER);
        return container;
    }

    JPanel ViewProFile() {
        JPanel container = new JPanel(new BorderLayout(40, 25));
        JPanel header = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel message = new JLabel("Profile");
        message.setForeground(Color.WHITE);
        message.setFont(new Font("News Gothic MT", Font.BOLD, 25));
        header.setBackground(new Color(1, 102, 170));
        container.setBackground(Color.WHITE);

        JPanel body = new JPanel(new BorderLayout());
        JPanel photo = new JPanel(new FlowLayout(FlowLayout.CENTER));
        photo.setOpaque(false);

        ImageIcon imageIcon = new ImageIcon(path +"src/image/logo.png");
        JLabel imageLabel = new JLabel();
        imageLabel.setIcon(Common.getRoundedIcon(imageIcon, 90, new Color(42, 193, 255)));
        photo.add(imageLabel);

        JPanel info = new JPanel(new GridLayout(3, 2, 50, 25));
        String[] array = {"Name", user.getUsername(), "Email", user.getEmail(), "Is Admin", user.isAdmin() ? "Yes" : "No"};
        for (String txt : array) {
            JLabel label = new JLabel(Character.toUpperCase(txt.charAt(0)) + txt.substring(1));
            label.setLayout(new FlowLayout(FlowLayout.CENTER));
            label.setForeground(new Color(1, 102, 170));
            label.setFont(new Font("News Gothic MT", Font.BOLD, 22));
            label.setOpaque(false);
            info.add(label);
        }

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setOpaque(false);
        panel.add(info);
        info.setOpaque(false);
        body.setOpaque(false);
        body.add(photo, BorderLayout.NORTH);
        body.add(panel, BorderLayout.CENTER);
        body.setPreferredSize(new Dimension(400, 400));
        header.add(message);
        header.setBorder(new LineBorder(Color.blue, 1));
        container.add(header, BorderLayout.NORTH);
        container.add(body, BorderLayout.CENTER);
        return container;
    }

    JPanel ViewTemp() {
        JPanel hold = new JPanel(new BorderLayout(40, 25));
        JPanel header = new JPanel(new FlowLayout(FlowLayout.CENTER));
        header.setBackground(new Color(1, 102, 170));
        hold.setBackground(Color.WHITE);

        JPanel body = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JPanel photo = new JPanel(new FlowLayout(FlowLayout.CENTER));
        photo.setOpaque(false);
        body.setBackground(Color.WHITE);

        ImageIcon logo = new ImageIcon("src/image/logo (2).png");
        JLabel imageLabel = new JLabel(logo);
        header.add(imageLabel);

        JLabel label = new JLabel("FinTrack");
        label.setForeground(Color.WHITE);
        label.setFont(new Font("News Gothic MT", Font.BOLD, 90));
        body.add(label);
        body.setBackground(new Color(1, 102, 170));
        hold.add(header, BorderLayout.NORTH);
        hold.setBackground(new Color(1, 102, 170));
        hold.add(body, BorderLayout.CENTER);
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(new Color(1, 102, 170));
        container.add(hold, BorderLayout.CENTER);
        return container;
    }

    JPanel ViewBuget() {
        JPanel container = new JPanel(new BorderLayout(40, 25));
        JPanel header = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel message = new JLabel("View Budget");
        message.setForeground(Color.WHITE);
        message.setFont(new Font("News Gothic MT", Font.BOLD, 25));
        message.setOpaque(false);
        container.setOpaque(false);
        header.setBackground(new Color(1, 102, 170));

        JPanel panel;
        String[] column = {"Name", "Start", "Type", "Price"};
        TableSetup table = new TableSetup(column, this);
        table.BudgetView(this.reader, this.writer, this.user);
        panel = table.getContainer();

        header.add(message);
        container.add(header, BorderLayout.NORTH);
        container.add(panel, BorderLayout.CENTER);
        return container;
    }

    JPanel ViewInCome() {
        JPanel container = new JPanel(new BorderLayout(40, 25));
        JPanel header = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel message = new JLabel("View Income");
        message.setForeground(Color.WHITE);
        message.setFont(new Font("News Gothic MT", Font.BOLD, 25));
        message.setOpaque(false);
        container.setOpaque(false);
        header.setBackground(new Color(1, 102, 170));

        JPanel panel;
        String[] column = {"Name", "Type", "Income"};
        TableSetup table = new TableSetup(column, this);
        table.ViewIncome(this.reader, this.writer, this.user);
        panel = table.getContainer();

        header.add(message);
        container.add(header, BorderLayout.NORTH);
        container.add(panel, BorderLayout.CENTER);
        return container;
    }

    JPanel ViewExpense() {
        JPanel container = new JPanel(new BorderLayout(40, 25));
        JPanel header = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel message = new JLabel("View Expense");
        message.setForeground(Color.WHITE);
        message.setFont(new Font("News Gothic MT", Font.BOLD, 25));
        message.setOpaque(false);
        container.setOpaque(false);
        header.setBackground(new Color(1, 102, 170));

        JPanel panel = new JPanel();
        String[] column = {"Name", "Type", "Expense"};
        TableSetup table = new TableSetup(column, this);
        table.ViewExpense(this.reader, this.writer, this.user);
        panel = table.getContainer();

        header.add(message);
        container.add(header, BorderLayout.NORTH);
        container.add(panel, BorderLayout.CENTER);
        return container;
    }

    public JPanel ViewItemDetail(int id, JSONArray jsonArray) {
        JPanel container = new JPanel(new BorderLayout(40, 25));
        JPanel header = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel message = new JLabel("View Item Detail");
        message.setForeground(Color.WHITE);
        message.setFont(new Font("News Gothic MT", Font.BOLD, 25));
        message.setOpaque(false);
        container.setOpaque(false);
        header.setBackground(new Color(1, 102, 170));

        JPanel panel;
        String[] column = {"Name", "Budget"};
        TableSetup table = new TableSetup(column, this);
        table.setJsonArray(jsonArray);
        table.ViewItemDetail(id);
        panel = table.getContainer();

        header.add(message);
        container.add(header, BorderLayout.NORTH);
        container.add(panel, BorderLayout.CENTER);
        return container;
    }

    public JPanel ViewExpenseDetail(int id, JSONArray jsonArray) {
        JPanel container = new JPanel(new BorderLayout(40, 25));
        container.setBackground(Color.WHITE);
        JPanel header = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel message = new JLabel("View Expense Detail");
        message.setForeground(Color.WHITE);
        message.setFont(new Font("News Gothic MT", Font.BOLD, 25));
        message.setOpaque(false);
        container.setOpaque(false);
        header.setBackground(new Color(1, 102, 170));

        JPanel panel;
        String[] column = {"Name", "Item", "Date", "Amount"};
        TableSetup table = new TableSetup(column, this);
        table.setJsonArray(jsonArray);
        table.ViewExpenseDetail(id);
        panel = table.getContainer();

        JSONObject obj = jsonArray.getJSONObject(id);

        JPanel footer = new JPanel();
        footer.setBackground(Color.WHITE);
        JButton create = new JButton("Add Expense");
        create.setBounds(0, 240, 140, 30);
        create.setBorder(new RoundedBorder(10));
        create.setBackground(Color.WHITE);
        create.setForeground(new Color(1, 102, 170));
        create.setPreferredSize(new Dimension(100, 30));
        create.addMouseListener(AddIncomeExpenseListener(obj.getString("id"), "expense" ));

        header.add(message);
        footer.add(create);
        container.add(header, BorderLayout.NORTH);
        container.add(panel, BorderLayout.CENTER);
        container.add(footer, BorderLayout.SOUTH);
        return container;
    }

    public JPanel ViewIncomeDetail(int id, JSONArray jsonArray) {
        JPanel container = new JPanel(new BorderLayout(40, 25));
        container.setBackground(Color.WHITE);
        JPanel header = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel message = new JLabel("View Income Detail");
        message.setForeground(Color.WHITE);
        message.setFont(new Font("News Gothic MT", Font.BOLD, 25));
        message.setOpaque(false);
        container.setOpaque(false);
        header.setBackground(new Color(1, 102, 170));

        JPanel panel;
        String[] column = {"Name", "Item", "Date", "Amount"};
        TableSetup table = new TableSetup(column, this);
        table.setJsonArray(jsonArray);
        table.ViewIncomeDetail(id);
        panel = table.getContainer();

        JSONObject obj = jsonArray.getJSONObject(id);

        JPanel footer = new JPanel();
        footer.setBackground(Color.WHITE);
        JButton create = new JButton("Add Income");
        create.setBounds(0, 240, 140, 30);
        create.setBorder(new RoundedBorder(10));
        create.setBackground(Color.WHITE);
        create.setForeground(new Color(1, 102, 170));
        create.setPreferredSize(new Dimension(100, 30));
        create.addMouseListener(AddIncomeExpenseListener(obj.getString("id"), "income" ));

        header.add(message);
        footer.add(create);
        container.add(header, BorderLayout.NORTH);
        container.add(panel, BorderLayout.CENTER);
        container.add(footer, BorderLayout.SOUTH);
        return container;
    }

    public JPanel ViewBudgetDetail(JSONObject jsonObject) {
        String data = Common.jsonify("api/users", "get", user.toJson());
        this.writer.println(data);
        ArrayList<String> users = new ArrayList<>();
        try {
            data = reader.readLine();
            JSONArray usersArray = new JSONArray(data.split(" ", 2)[1]);
            for (Object id : jsonObject.getJSONArray("accountant_ids")) {
                for (int i = 0; i < usersArray.length(); i++) {
                    if (id.equals(usersArray.getJSONObject(i).getString("id"))) {
                        users.add(usersArray.getJSONObject(i).getString("username"));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        JPanel container = new JPanel(new BorderLayout(40, 25));
        JPanel header = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JPanel body = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel message = new JLabel("View Budget Detail");
        message.setForeground(Color.WHITE);
        message.setFont(new Font("News Gothic MT", Font.BOLD, 25));
        message.setOpaque(false);
        header.setBackground(new Color(1, 102, 170));

        JPanel table = new JPanel(new GridLayout(7 + (users.size() * 2), 2, 4, 4));
        String[] array = {"name", "description", "start", "end", "type", "price"};

        for (String str : array) {
            JLabel label1 = new JLabel(Character.toUpperCase(str.charAt(0)) + str.substring(1));
            label1.setForeground(new Color(1, 102, 170));
            String text;
            if (str.equals("price"))
                text = String.valueOf(jsonObject.getDouble(str));
            else
                text = jsonObject.getString(str);
            JLabel label2 = new JLabel(text);
            label2.setOpaque(false);
            label2.setForeground(new Color(1, 102, 170));
            table.add(label1);
            table.add(label2);
        }

        for (int i = 0; i < users.size(); i++) {
            String text = "";
            if (i == 0) {
                text = "Accountants";
            }
            JLabel label1 = new JLabel(text);
            label1.setForeground(new Color(1, 102, 170));
            label1.setOpaque(false);
            table.add(label1);
            JLabel label2 = new JLabel(users.get(i));
            label2.setOpaque(false);
            label2.setForeground(new Color(1, 102, 170));
            table.add(label2);
        }

        JButton edit = new JButton("Manage");
        edit.setBackground(Color.WHITE);
        edit.setForeground(new Color(1, 102, 170));

        JPanel buttonContainer1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonContainer1.setOpaque(false);
        buttonContainer1.add(edit);
        edit.addMouseListener(ManageItemListener(jsonObject.getString("id")));

        table.add(buttonContainer1);
        table.setPreferredSize(new Dimension(450, 350 + (60 * users.size())));
        header.add(message);
        table.setOpaque(false);
        body.setOpaque(false);
        table.setBackground(Color.WHITE);
        body.setBackground(Color.WHITE);
        body.add(table);
        container.setBackground(Color.WHITE);
        container.add(header, BorderLayout.NORTH);
        container.add(body, BorderLayout.CENTER);
        return container;
    }

    JPanel AddIncomeExpense(String budget_id, String type) {
        JPanel container = new JPanel(new BorderLayout(40, 25));
        JPanel header = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel message = new JLabel("Add " + Character.toUpperCase(type.charAt(0)) + type.substring(1));
        message.setForeground(Color.WHITE);
        message.setFont(new Font("News Gothic MT", Font.BOLD, 25));
        message.setOpaque(false);
        header.setBackground(new Color(1, 102, 170));
        container.setBackground(Color.WHITE);

        header.add(message);
        container.add(header, BorderLayout.NORTH);
        JPanel panel = new JPanel(null);
        panel.setOpaque(false);
        JPanel result = this.CreateIncomeAdExpenseHandler(budget_id, type);
        result.setOpaque(false);
        result.setBounds(175, 0, 500, 600);
        panel.add(result);
        panel.setBackground(Color.WHITE);
        container.add(panel, BorderLayout.CENTER);
        return container;
    }

    JPanel CreateIncomeAdExpenseHandler(String budget_id, String type)  {
        JPanel form = new JPanel(null);
        form.setOpaque(false);
        JTextField name = new JTextField();
        name.setBorder(new RoundedBorder(10));
        name.setBounds(0, 0, 400, 40);
        Common.addPlaceholder(name, "Name");
        form.setOpaque(false);

        this.common = new Common();
        JPanel start = new JPanel(new FlowLayout(FlowLayout.LEFT));
        start.setOpaque(false);
        JLabel startText = new JLabel("Date");
        startText.setOpaque(false);
        startText.setBackground(new Color(1, 102, 170));
        startText.setPreferredSize(new Dimension(80, 40));
        JPanel startDate = this.common.createDatePickerForm();
        startDate.setOpaque(false);
        start.add(startText);
        start.add(startDate);
        start.setBackground(Color.WHITE);
        start.setBounds(0, 60, 400, 40);

        JDatePickerImpl startDatePicker = (JDatePickerImpl) startDate.getComponent(0);

        JPanel panel = new JPanel();

        Common.DataItem[] dataItems;

        String data = Common.jsonify("api/items/budget/" + budget_id, "get", user.toJson());
        try {
            writer.println(data);
            data = reader.readLine();
            String[] array = data.split(" ", 2);
            System.out.println(Arrays.toString(array));
            if (array[0].equals("200")) {
                JSONArray jsonArray = new JSONArray(array[1]);
                dataItems = new Common.DataItem[jsonArray.length()];

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String id = jsonObject.getString("id");
                    String name1 = jsonObject.getString("name");
                    dataItems[i] = new Common.DataItem(id, name1);
                }
                this.common.addDataSelectionComponent(panel, dataItems);
            } else
                System.out.println("Failed");
        } catch (Exception e) {
            e.printStackTrace();
        }

        panel.setBounds(0, 120, 400, 40);
        panel.setBorder(new RoundedBorder(10));


        JTextField price = new JTextField();
        price.setBorder(new RoundedBorder(10));
        Common.addPlaceholder(price, "Price");
        price.setBounds(0, 180, 400, 40);

        JButton create = new JButton("Create");
        create.setBounds(0, 240, 100, 30);
        create.setBorder(new RoundedBorder(10));
        create.setBackground(Color.WHITE);
        create.setForeground(new Color(1, 102, 170));
        create.addActionListener(createButtonActionListener(name, panel, startDatePicker, price, type, budget_id));

        form.add(name);
        form.add(start);
        form.add(panel);
        form.add(price);
        form.add(create);
        return form;
    }


    JPanel ManageItem(String budget_id) {
        JPanel container = new JPanel(new BorderLayout(40, 25));
        JPanel header = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel message = new JLabel("Manage Item");
        message.setForeground(Color.WHITE);
        message.setFont(new Font("News Gothic MT", Font.BOLD, 25));
        message.setOpaque(false);
        header.setBackground(new Color(1, 102, 170));
        container.setBackground(Color.WHITE);

        header.add(message);
        container.add(header, BorderLayout.NORTH);
        JPanel panel = new JPanel(null);
        panel.setOpaque(false);
        JPanel result = this.CreateItemHandler(budget_id);
        result.setOpaque(false);
        result.setBounds(175, 0, 500, 600);
        panel.add(result);
        panel.setBackground(Color.WHITE);
        container.add(panel, BorderLayout.CENTER);
        return container;
    }

    JPanel CreateItemHandler(String budget_id)  {
        JPanel form = new JPanel(null);
        form.setOpaque(false);
        JTextField name = new JTextField();
        name.setBorder(new RoundedBorder(10));
        name.setBounds(0, 0, 400, 40);
        Common.addPlaceholder(name, "Name");
        form.setOpaque(false);

        this.common = new Common();

        JPanel panel = new JPanel();

        Common.DataItem[] dataItems;

        String data = Common.jsonify("api/users/" + user.getId() + "/budgets", "get",  user.toJson());
        try {
            writer.println(data);
            data = reader.readLine();
            String[] array = data.split(" ", 2);
            System.out.println(Arrays.toString(array));
            if (array[0].equals("200")) {
                JSONArray jsonArray = new JSONArray(array[1]);
                dataItems = new Common.DataItem[jsonArray.length()];

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String id = jsonObject.getString("id");
                    String name1 = jsonObject.getString("name");
                    dataItems[i] = new Common.DataItem(id, name1);
                }
                this.common.addDataSelectionComponent(panel, dataItems);
            } else
                System.out.println("Failed");
        } catch (Exception e) {
            e.printStackTrace();
        }

        panel.setBounds(0, 60, 400, 40);

        Common.DataItem[] dataItemsToRemoved;

        JButton create = new JButton("Create");
        create.setBounds(0, 120, 100, 30);
        create.setBorder(new RoundedBorder(10));
        create.setBackground(Color.WHITE);
        create.setForeground(new Color(1, 102, 170));
        create.addMouseListener(createItemActionListener(name, panel));

        JPanel removed = new JPanel();
        String url = Common.jsonify("api/items/budget/" + budget_id, "get", user.toJson());
        try {
            writer.println(url);
            url = reader.readLine();
            String[] array = url.split(" ", 2);
            System.out.println(Arrays.toString(array));
            if (array[0].equals("200")) {
                JSONArray jsonArray = new JSONArray(array[1]);
                dataItemsToRemoved = new Common.DataItem[jsonArray.length()];

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String id = jsonObject.getString("id");
                    String name1 = jsonObject.getString("name");
                    dataItemsToRemoved[i] = new Common.DataItem(id, name1);
                }
                this.common.addDataSelectionComponent(removed, dataItemsToRemoved);
            } else
                System.out.println("Failed");
        } catch (Exception e) {
            e.printStackTrace();
        }

        JButton delete = new JButton("Delete");
        delete.setBounds(0, 440, 100, 30);
        delete.addMouseListener(ItemRemoveListener(removed));
        delete.setBorder(new RoundedBorder(10));
        delete.setBackground(Color.WHITE);
        delete.setForeground(new Color(1, 102, 170));
        delete.setBounds(330, 170, 70, 30);
        delete.setOpaque(false);
        removed.setBounds(0, 170, 300, 40);

        form.add(name);
        form.add(panel);
        form.add(removed);
        form.add(delete);
        form.add(create);
        return form;
    }

    private ActionListener createButtonActionListener(JTextField Tname,  JPanel panel, JDatePickerImpl startDatePicker,  JTextField price, String type, String id) {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String name = Tname.getText();
                JComboBox<Common.DataItem> comboBox = (JComboBox<Common.DataItem>) panel.getComponent(0);
                Common.DataItem selectedDataItem = (Common.DataItem) comboBox.getSelectedItem();
                String item_id = selectedDataItem.getId();
                double prices = Double.parseDouble(price.getText());
                Date date = (Date) startDatePicker.getModel().getValue();
                JSONObject obj = new JSONObject();
                obj.put("name", name);
                obj.put("budget_id", id);
                obj.put("item_id", item_id);
                obj.put("date", date);
                obj.put("amount", prices);
                obj.put("accountant_id", user.getId());

                String data1 = "";
                if (type.equals("income"))
                    data1 = Common.jsonify("api/incomes", "post", obj);
                else
                    data1 = Common.jsonify("api/expenses", "post", obj);
                try {
                    writer.println(data1);
                    reader.readLine();
                    SwingUtilities.invokeLater(() -> {
                        ActionLeftCard.show(ActionLeftPlane, "viewIncome");
                        ActionRightCard.show(ActionRightPlane, "ViewTemp");
                    });
                } catch (Exception x) {
                    x.printStackTrace();
                }
            }
        };
    }

    private MouseAdapter createItemActionListener(JTextField Tname, JPanel panel) {
        return new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    String name = Tname.getText();
                    JComboBox<Common.DataItem> comboBox = (JComboBox<Common.DataItem>) panel.getComponent(0);
                    Common.DataItem selectedDataItem = (Common.DataItem) comboBox.getSelectedItem();
                    String budget_id = selectedDataItem.getId();

                    JSONObject obj = new JSONObject();
                    obj.put("name", name);
                    obj.put("budget_id", budget_id);
                    obj.put("image", "");

                    String data = Common.jsonify("api/items", "post", obj);
                    writer.println(data);
                    data = reader.readLine();
                    System.out.println(data);

                    SwingUtilities.invokeLater(() -> {
                        ActionLeftCard.show(ActionLeftPlane,  "ItemsInfo");
                        ActionRightCard.show(ActionRightPlane, "ViewTemp");
                    });
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
    }

    private MouseAdapter ItemRemoveListener(JPanel panel) {
        return new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    JComboBox<Common.DataItem> comboBox = (JComboBox<Common.DataItem>) panel.getComponent(0);
                    Common.DataItem selectedDataItem = (Common.DataItem) comboBox.getSelectedItem();
                    String itemId = selectedDataItem.getId();

                    JSONObject obj = new JSONObject();

                    String data = Common.jsonify("api/items/" + itemId, "delete", obj);
                    writer.println(data);
                    reader.readLine();


                    SwingUtilities.invokeLater(() -> {
                        ActionLeftCard.show(ActionLeftPlane,  "ItemsInfo");
                        ActionRightCard.show(ActionRightPlane, "ViewTemp");
                    });
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
    }

    private MouseAdapter AddIncomeExpenseListener(String budget_id, String type) {
        return new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    ActionLeftPlane.add(AddIncomeExpense(budget_id, type), "AddIncomeExpense");
                    SwingUtilities.invokeLater(() -> {
                        ActionLeftCard.show(ActionLeftPlane, "AddIncomeExpense");
                    });
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
    };

    private MouseAdapter ManageItemListener(String budget_id) {
        return new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    ActionLeftPlane.add(ManageItem(budget_id), "ManageItem");
                    SwingUtilities.invokeLater(() -> {
                        ActionLeftCard.show(ActionLeftPlane, "ManageItem");
                    });
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
    };
}
