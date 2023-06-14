package Admin;

import Budget.Budget;
import Common.Common;
import CommonLayout.CommonLayout;
import Login.Login;
import RoundedBorder.RoundedBorder;
import TableMaker.TableMaker;
import User.User;
import net.sourceforge.jdatepicker.impl.JDatePickerImpl;
import org.json.JSONArray;
import org.json.JSONObject;
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
import java.util.concurrent.atomic.AtomicInteger;


public class Admin extends CommonLayout {
    public Common common;
    public User user;
    public BufferedReader reader;
    public PrintWriter writer;
    public JPanel ActionLeftPlane;
    public JPanel ActionRightPlane;
    public CardLayout ActionLeftCard;
    public CardLayout ActionRightCard;
    public Login login;

    public Admin(User user, BufferedReader reader, PrintWriter writer, String path) {
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

        this.progress.setLayout(new BorderLayout());
        this.progress.add(setUpAnalyisis(), BorderLayout.CENTER);

        ManageAccountant();
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
        JPanel accountants= setUpIcon("Accountants", path + "src/image/accountant.png");
        JPanel income = setUpIcon("Incomes", path + "src/image/income.png");
        JPanel expense = setUpIcon("Expenses", path + "src/image/expense.png");
        JPanel report = setUpIcon("Reports", path + "src/image/report.png");
        JPanel logout = setUpIcon("Logout", path + "src/image/logout.png");

        center.add(home);
        center.add(accountants);
        center.add(budgets);
        center.add(income);
        center.add(expense);
        center.add(report);
        center.add(logout);
        center.setOpaque(false);
        center.setPreferredSize(new Dimension(250, 450));

        home.addMouseListener(createMouseListener("Home"));
        accountants.addMouseListener(createMouseListener("Accountants"));
        budgets.addMouseListener(createMouseListener("Budgets"));
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

    JPanel setUpAnalyisis() {
        int[] analyisis = {0, 0, 0, 0};

        String data = Common.jsonify("api/reports/analysis", "get", this.user.toJson());
        try {
            writer.println(data);
            data = reader.readLine();
            String[] array = data.split(" ", 2);
            if (array[0].equals("200")) {
                JSONObject jsonObject = new JSONObject(array[1]);
                analyisis[0] = (int)jsonObject.getDouble("income");
                analyisis[1] = (int)jsonObject.getDouble("expense");
                analyisis[2] = (int)jsonObject.getDouble("lastIncome");
                analyisis[3] = (int)jsonObject.getDouble("lastExpense");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        JPanel container = new JPanel(new GridLayout(1, 4, 20, 5));
        String[] array = {"Total Income", "Total Expense", "Last Week Income", "Last Week Expense"};
        for (int i = 0; i < 4; i++) {
            final int index = i;  // Create a final variable to capture the value of i
            JPanel panel = new JPanel(new BorderLayout(0, 8));
            JPanel footer = new JPanel();
            //footer.setBorder(new LineBorder(Color.blue, 1));
            JLabel label = new JLabel(array[i]);
            label.setFont(new Font("News Gothic MT", Font.PLAIN, 20));
            label.setForeground(new Color(59, 89, 152));
            //footer.setBackground(new Color(59, 89, 152));
            label.setOpaque(false);

            JProgressBar progressBar = new JProgressBar();
            progressBar.setForeground(new Color(59, 89, 152));
            progressBar.setMinimum(0);
            progressBar.setMaximum(100);
            progressBar.setStringPainted(true);
            progressBar.setBorder(new LineBorder(Color.blue, 1));

            AtomicInteger progress = new AtomicInteger();
            Timer timer = new Timer(2000, e -> {
                progress.addAndGet(5);
                progressBar.setValue(analyisis[index]);

                if (progress.get() >= 100) {
                    ((Timer) e.getSource()).stop();
                }
            });
            timer.start();
            footer.add(label);
            progressBar.setPreferredSize(new Dimension(300, 80));
            panel.add(progressBar, BorderLayout.CENTER);
            panel.add(footer, BorderLayout.SOUTH);
            container.add(panel);
        }
        return container;
    }

    private MouseAdapter createMouseListener(String iconName) {
        return new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleIconClick(iconName);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                setCursorToHand(e.getComponent());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setCursorToDefault(e.getComponent());
            }
        };
    }

    private void setCursorToHand(Component component) {
        Cursor cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
        component.setCursor(cursor);
    }

    private void setCursorToDefault(Component component) {
        Cursor cursor = Cursor.getDefaultCursor();
        component.setCursor(cursor);
    }

    private void handleIconClick(String iconName) {
        if (iconName.equals("Home")) {
            this.currentRoute.setText("Home");
            this.ManageHome();
            SwingUtilities.invokeLater(() -> {
                ActionLeftCard.show(this.ActionLeftPlane, "ViewProFile");
                ActionRightCard.show(this.ActionRightPlane, "ViewTemp");
            });
        } else if (iconName.equals("Accountants")) {
            this.currentRoute.setText("Accountants");
            this.ManageAccountant();
            SwingUtilities.invokeLater(() -> {
                ActionLeftCard.show(this.ActionLeftPlane, "accountantInfo");
                ActionRightCard.show(this.ActionRightPlane, "ViewTemp");
            });
        } else if (iconName.equals("Budgets")) {
            this.currentRoute.setText("Budgets");
            this.ManageBudget();
            SwingUtilities.invokeLater(() -> {
                this.ActionLeftCard.show(this.ActionLeftPlane, "viewBudget");
                this.ActionRightCard.show(this.ActionRightPlane, "addBudget");
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
        } else if (iconName.equals("Reports")) {
            this.currentRoute.setText("Reports");
            this.ManageReport();
            SwingUtilities.invokeLater(() -> {
                ActionLeftCard.show(this.ActionLeftPlane, "viewReport");
                ActionRightCard.show(this.ActionRightPlane, "ViewTemp");
            });
        } else if (iconName.equals("Logout")) {
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

    void ManageAccountant() {
        ActionLeftPlane.add(this.AccountantInfo(), "accountantInfo");
        ActionRightPlane.add(this.ViewTemp(),"ViewTemp");
    }

    void ManageBudget() {
        ActionLeftPlane.add(this.ViewBuget(), "viewBudget");
        ActionRightPlane.add(this.AddBudget(), "addBudget");
    }

    void ManageIncome() {
        ActionLeftPlane.add(this.ViewInCome(), "viewIncome");
        ActionRightPlane.add(this.ViewTemp(), "ViewTemp");
    }

    void ManageExpense() {
        ActionLeftPlane.add(this.ViewExpense(), "viewExpense");
        ActionRightPlane.add(this.ViewTemp(), "ViewTemp");
    }

    void ManageReport() {
        ActionLeftPlane.add(this.ViewBudgetReport(),  "viewReport");
        ActionRightPlane.add(this.ViewTemp(), "ViewTemp");
    }

    JPanel AccountantInfo() {
        JPanel container = new JPanel(new BorderLayout(40, 25));
        JPanel header = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel message = new JLabel("Accountant Information");
        message.setForeground(Color.WHITE);
        message.setFont(new Font("News Gothic MT", Font.BOLD, 25));
        message.setOpaque(false);
        container.setOpaque(false);
        header.setBackground(new Color(1, 102, 170));

        String[] column = {"", "Name", "Email", "Is Admin"};
        TableMaker table = new TableMaker(column, this);
        table.AccountantView(this.reader, this.writer, this.user);
        JPanel panel = table.getContainer();
        panel.setOpaque(false);

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

        ImageIcon imageIcon = new ImageIcon("/home/kena/Downloads/logo.png");
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

        ImageIcon logo = new ImageIcon("/home/kena/Downloads/logo (2).png");
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

        JPanel panel = new JPanel();
        String[] column = {"Name", "Start", "Type", "Price"};
        TableMaker table = new TableMaker(column, this);
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

        JPanel panel = new JPanel();
        String[] column = {"Name", "Type", "Income"};
        TableMaker table = new TableMaker(column, this);
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
        TableMaker table = new TableMaker(column, this);
        table.ViewExpense(this.reader, this.writer, this.user);
        panel = table.getContainer();

        header.add(message);
        container.add(header, BorderLayout.NORTH);
        container.add(panel, BorderLayout.CENTER);
        return container;
    }

    JPanel ViewBudgetReport() {
        JPanel container = new JPanel(new BorderLayout(40, 25));
        JPanel header = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel message = new JLabel("View Budget Report");
        message.setForeground(Color.WHITE);
        message.setFont(new Font("News Gothic MT", Font.BOLD, 25));
        message.setOpaque(false);
        container.setOpaque(false);
        header.setBackground(new Color(1, 102, 170));

        JPanel panel;
        String[] column = {"Name", "Total Income", "Total Expense", "Profit"};
        TableMaker table = new TableMaker(column, this);
        table.ViewBudgetReport(this.reader, this.writer, this.user);
        panel = table.getContainer();

        header.add(message);
        container.add(header, BorderLayout.NORTH);
        container.add(panel, BorderLayout.CENTER);
        return container;
    }

    public JPanel ViewBudgetReportDetail(String id) {
        JPanel container = new JPanel(new BorderLayout(40, 25));
        JPanel header = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel message = new JLabel("View Budget Report Detail");
        message.setForeground(Color.WHITE);
        message.setFont(new Font("News Gothic MT", Font.BOLD, 25));
        message.setOpaque(false);
        container.setOpaque(false);
        header.setBackground(new Color(1, 102, 170));

        JPanel panel;
        String[] column = {"Name", "Total Income", "Total Expense", "Profit"};
        TableMaker table = new TableMaker(column, this);
        table.ViewBudgetReportDetail(reader, writer, user, id);
        panel = table.getContainer();

        header.add(message);
        container.add(header, BorderLayout.NORTH);
        container.add(panel, BorderLayout.CENTER);
        return container;
    }

    public JPanel ViewExpenseDetail(int id, JSONArray jsonArray) {
        JPanel container = new JPanel(new BorderLayout(40, 25));
        JPanel header = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel message = new JLabel("View Expense Detail");
        message.setForeground(Color.WHITE);
        message.setFont(new Font("News Gothic MT", Font.BOLD, 25));
        message.setOpaque(false);
        container.setOpaque(false);
        header.setBackground(new Color(1, 102, 170));

        JPanel panel;
        String[] column = {"Name", "Item", "Date", "Amount"};
        TableMaker table = new TableMaker(column, this);
        table.setJsonArray(jsonArray);
        table.ViewExpenseDetail(id);
        panel = table.getContainer();

        header.add(message);
        container.add(header, BorderLayout.NORTH);
        container.add(panel, BorderLayout.CENTER);
        return container;
    }

    public JPanel AddAccountant() {
        JPanel container = new JPanel(new BorderLayout(40, 25));
        JPanel header = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel message = new JLabel("Add Accountant");
        message.setForeground(Color.WHITE);
        message.setFont(new Font("News Gothic MT", Font.BOLD, 25));
        message.setOpaque(false);
        container.setOpaque(false);
        header.setBackground(new Color(1, 102, 170));

        header.add(message);
        container.add(header, BorderLayout.NORTH);
        container.add(AddAccountantHandler(), BorderLayout.CENTER);
        return container;
    }

    JPanel AddAccountantHandler() {
        JPanel form = new JPanel(null);
        form.setOpaque(false);
        JTextField name = new JTextField();
        name.setBorder(new RoundedBorder(10));
        name.setBounds(200, 0, 400, 40);
        Common.addPlaceholder(name, "Email");
        form.setBackground(Color.WHITE);

        JButton edit = new JButton("Add");
        edit.setBackground(Color.WHITE);
        edit.setForeground(new Color(1, 102, 170));
        edit.setBounds(200, 80, 400, 40);
        edit.setOpaque(false);
        edit.addMouseListener(AddAccountantTemp(name));
        form.add(name);
        form.add(edit);
        return form;
    }

    private MouseAdapter AddAccountantTemp(JTextField name){
        return new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    String email = name.getText();
                    JSONObject obj = new JSONObject();
                    obj.put("email", email);
                    String data = Common.jsonify("api/users/temp", "post", obj);
                    writer.println(data);
                    reader.readLine();
                    SwingUtilities.invokeLater(() -> {
                        ViewBuget();
                        ActionLeftCard.show(ActionLeftPlane, "viewBudget");
                        ActionRightCard.show(ActionRightPlane, "addBudget");
                    });
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
    };

    public JPanel ViewIncomeDetail(int id, JSONArray jsonArray) {
        JPanel container = new JPanel(new BorderLayout(40, 25));
        JPanel header = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel message = new JLabel("View Income Detail");
        message.setForeground(Color.WHITE);
        message.setFont(new Font("News Gothic MT", Font.BOLD, 25));
        message.setOpaque(false);
        container.setOpaque(false);
        header.setBackground(new Color(1, 102, 170));

        JPanel panel = new JPanel();
        String[] column = {"Name", "Item", "Date", "Amount"};
        TableMaker table = new TableMaker(column, this);
        table.setJsonArray(jsonArray);
        table.ViewIncomeDetail(id);
        panel = table.getContainer();

        header.add(message);
        container.add(header, BorderLayout.NORTH);
        container.add(panel, BorderLayout.CENTER);
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

        JButton delete = new JButton("Delete");
        JButton edit = new JButton("Edit");
        delete.setBackground(Color.WHITE);
        edit.setBackground(Color.WHITE);
        delete.setForeground(new Color(1, 102, 170));
        edit.setForeground(new Color(1, 102, 170));

        JPanel buttonContainer1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonContainer1.setOpaque(false);
        buttonContainer1.add(edit);

        JPanel buttonContainer2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonContainer2.setOpaque(false);
        buttonContainer2.add(delete);

        table.add(buttonContainer1);
        table.add(buttonContainer2);

        edit.addMouseListener(EditBudgetListener(jsonObject));
        delete.addMouseListener(deleteBudgetListener(jsonObject.getString("id")));

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

    JPanel AddBudget() {
        JPanel container = new JPanel(new BorderLayout(40, 25));
        JPanel header = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel message = new JLabel("Add Budget");
        message.setForeground(Color.WHITE);
        message.setFont(new Font("News Gothic MT", Font.BOLD, 25));
        message.setOpaque(false);
        header.setBackground(new Color(1, 102, 170));
        container.setBackground(Color.WHITE);

        header.add(message);
        container.add(header, BorderLayout.NORTH);
        JPanel panel = new JPanel(null);
        panel.setOpaque(false);
        JPanel result = this.AddBudgetHandler();
        result.setOpaque(false);
        result.setBounds(175, 0, 500, 500);
        panel.add(result);
        panel.setBackground(Color.WHITE);
        container.add(panel, BorderLayout.CENTER);
        return container;
    }


    JPanel AddBudgetHandler() {
        JPanel form = new JPanel(null);
        form.setOpaque(false);
        JTextField name = new JTextField();
        name.setBorder(new RoundedBorder(10));
        name.setBounds(0, 0, 400, 40);
        Common.addPlaceholder(name, "Name");
        form.setOpaque(false);

        form.setBackground(Color.WHITE);
        JTextField description = new JTextField();
        description.setBorder(new RoundedBorder(10));
        description.setBounds(0, 60, 400, 120);
        Common.addPlaceholder(description, "Description");

        this.common = new Common();
        JPanel start = new JPanel(new FlowLayout(FlowLayout.LEFT));
        start.setOpaque(false);
        JLabel startText = new JLabel("Start");
        startText.setOpaque(false);
        startText.setBackground(new Color(1, 102, 170));
        startText.setPreferredSize(new Dimension(80, 40));
        JPanel startDate = this.common.createDatePickerForm();
        startDate.setOpaque(false);
        start.add(startText);
        start.add(startDate);
        start.setBackground(Color.WHITE);
        start.setBounds(0, 200, 400, 40);

        JPanel end = new JPanel(new FlowLayout(FlowLayout.LEFT));
        end.setOpaque(false);
        JLabel endText = new JLabel("End ");
        endText.setPreferredSize(new Dimension(80, 40));
        endText.setBackground(new Color(1, 102, 170));
        endText.setOpaque(false);
        JPanel endDate = this.common.createDatePickerForm();
        endDate.setOpaque(false);
        end.add(endText);
        end.add(endDate);
        end.setBounds(0, 250, 400, 40);

        JDatePickerImpl startDatePicker = (JDatePickerImpl) startDate.getComponent(0);
        JDatePickerImpl endDatePicker = (JDatePickerImpl) endDate.getComponent(0);

        JPanel panel = new JPanel();

        Common.DataItem[] dataItems;

        String data = Common.jsonify("api/users", "get", user.toJson());
        writer.println(data);

        try {
            data = reader.readLine();
            String[] array = data.split(" ", 2);
            System.out.println(Arrays.toString(array));
            if (array[0].equals("200")) {
                JSONArray jsonArray = new JSONArray(array[1]);
                dataItems = new Common.DataItem[jsonArray.length()];

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String id = jsonObject.getString("id");
                    String username = jsonObject.getString("username");
                    dataItems[i] = new Common.DataItem(id, username);
                }
                this.common.addDataSelectionComponent(panel, dataItems);
            } else
                System.out.println("Failed");
        } catch (Exception e) {
            e.printStackTrace();
        }

        panel.setBounds(0, 300, 400, 40);

        JRadioButton fixed = new JRadioButton("Fixed");
        fixed.setOpaque(false);
        JRadioButton dynamic = new JRadioButton("Dynamic");
        dynamic.setOpaque(false);
        fixed.setBounds(0, 350, 195, 30);
        dynamic.setBounds(200, 350, 200, 30);

        JTextField price = new JTextField();
        price.setBorder(new RoundedBorder(10));
        Common.addPlaceholder(price, "If select fixed set price!");
        price.setBounds(0, 385, 400, 40);

        JButton create = new JButton("Create");
        create.setBounds(0, 440, 100, 30);
        create.setBorder(new RoundedBorder(10));
        create.setBackground(Color.WHITE);
        create.setForeground(new Color(1, 102, 170));
        create.addActionListener(createButtonActionListener(name, description, fixed, panel, startDatePicker, endDatePicker, this.reader, this.writer, price));

        form.add(name);
        form.add(description);
        form.add(start);
        form.add(end);
        form.add(panel);
        form.add(fixed);
        form.add(dynamic);
        form.add(price);
        form.add(create);
        return form;
    }

    JPanel EditBudget(JSONObject jsonObject) {
        JPanel container = new JPanel(new BorderLayout(40, 25));
        JPanel header = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel message = new JLabel("Edit Budget");
        message.setForeground(Color.WHITE);
        message.setFont(new Font("News Gothic MT", Font.BOLD, 25));
        message.setOpaque(false);
        header.setBackground(new Color(1, 102, 170));
        container.setBackground(Color.WHITE);

        header.add(message);
        container.add(header, BorderLayout.NORTH);
        JPanel panel = new JPanel(null);
        panel.setOpaque(false);
        JPanel result = this.EditBudgetHandler(jsonObject);
        result.setOpaque(false);
        result.setBounds(175, 0, 500, 600);
        panel.add(result);
        panel.setBackground(Color.WHITE);
        container.add(panel, BorderLayout.CENTER);
        return container;
    }

    JPanel EditBudgetHandler(JSONObject jsonObject) {
        JPanel form = new JPanel(null);
        form.setOpaque(false);
        JTextField name = new JTextField();
        name.setBorder(new RoundedBorder(10));
        name.setBounds(0, 0, 400, 40);
        Common.addPlaceholder(name, jsonObject.getString("name"));

        form.setBackground(Color.WHITE);
        JTextField description = new JTextField();
        description.setBorder(new RoundedBorder(10));
        description.setBounds(0, 60, 400, 120);
        Common.addPlaceholder(description, jsonObject.getString("description"));

        this.common = new Common();
        JPanel start = new JPanel(new FlowLayout(FlowLayout.LEFT));
        start.setOpaque(false);
        JLabel startText = new JLabel("Start");
        startText.setOpaque(false);
        startText.setBackground(new Color(1, 102, 170));
        startText.setPreferredSize(new Dimension(80, 40));
        JPanel startDate = this.common.createDatePickerForm();
        startDate.setOpaque(false);
        start.add(startText);
        start.add(startDate);
        start.setBackground(Color.WHITE);
        start.setBounds(0, 200, 400, 40);

        JPanel end = new JPanel(new FlowLayout(FlowLayout.LEFT));
        end.setOpaque(false);
        JLabel endText = new JLabel("End ");
        endText.setPreferredSize(new Dimension(80, 40));
        endText.setBackground(new Color(1, 102, 170));
        endText.setOpaque(false);
        JPanel endDate = this.common.createDatePickerForm();
        endDate.setOpaque(false);
        end.add(endText);
        end.add(endDate);
        end.setBounds(0, 250, 400, 40);

        JDatePickerImpl startDatePicker = (JDatePickerImpl) startDate.getComponent(0);
        JDatePickerImpl endDatePicker = (JDatePickerImpl) endDate.getComponent(0);

        JPanel panel = new JPanel();

        Common.DataItem[] dataItems;

        String data = Common.jsonify("api/users/budgets/" + jsonObject.get("id"), "get", user.toJson());
        writer.println(data);

        try {
            data = reader.readLine();
            String[] array = data.split(" ", 2);
            System.out.println(Arrays.toString(array));
            if (array[0].equals("200")) {
                JSONArray jsonArray = new JSONArray(array[1]);
                dataItems = new Common.DataItem[jsonArray.length()];

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject Object = jsonArray.getJSONObject(i);
                    String id = Object.getString("id");
                    String username = Object.getString("username");
                    dataItems[i] = new Common.DataItem(id, username);
                }
                this.common.addDataSelectionComponent(panel, dataItems);
            } else
                System.out.println("Failed");
        } catch (Exception e) {
            e.printStackTrace();
        }

        panel.setBounds(0, 300, 400, 40);
        JRadioButton fixed = new JRadioButton("Fixed");
        fixed.setOpaque(false);
        JRadioButton dynamic = new JRadioButton("Dynamic");
        dynamic.setOpaque(false);
        fixed.setBounds(0, 350, 195, 30);
        dynamic.setBounds(200, 350, 200, 30);

        JTextField price = new JTextField();
        price.setBorder(new RoundedBorder(10));
        Common.addPlaceholder(price, String.valueOf(jsonObject.getDouble("price")));
        price.setBounds(0, 385, 400, 40);

        JButton create = new JButton("Update");
        create.setBounds(0, 440, 100, 30);
        create.setBorder(new RoundedBorder(10));
        create.setBackground(Color.WHITE);
        create.setForeground(new Color(1, 102, 170));
        create.addActionListener(updateButtonActionListener(name, description, fixed, panel, startDatePicker, endDatePicker, this.reader, this.writer, price, jsonObject.getString("id")));


        data = Common.jsonify("api/users/budgets/" + jsonObject.get("id") + "/" + "not", "get", user.toJson());
        System.out.println(data);
        JPanel removed = new JPanel();
        removed.setOpaque(false);
        writer.println(data);
        Common.DataItem[] dataItemsRemove;
        try {
            data = reader.readLine();
            String[] array = data.split(" ", 2);
            System.out.println(Arrays.toString(array));
            if (array[0].equals("200")) {
                JSONArray jsonArray = new JSONArray(array[1]);
                dataItemsRemove = new Common.DataItem[jsonArray.length()];

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject Object = jsonArray.getJSONObject(i);
                    String id = Object.getString("id");
                    String username = Object.getString("username");
                    dataItemsRemove[i] = new Common.DataItem(id, username);
                }
                this.common.addDataSelectionComponent(removed, dataItemsRemove);
            } else
                System.out.println("Failed");
        } catch (Exception e) {
            e.printStackTrace();
        }

        JButton delete = new JButton("Delete");
        delete.addMouseListener(RemoveAcountantListener(jsonObject,removed));
        delete.setBounds(0, 440, 100, 30);
        delete.setBorder(new RoundedBorder(10));
        delete.setBackground(Color.WHITE);
        delete.setForeground(new Color(1, 102, 170));
        delete.setBounds(330, 490, 70, 30);
        delete.setOpaque(false);
        removed.setBounds(0, 490, 300, 40);

        form.add(name);
        form.add(description);
        form.add(start);
        form.add(end);
        form.add(panel);
        form.add(fixed);
        form.add(dynamic);
        form.add(price);
        form.add(create);
        form.add(delete);
        form.add(removed);
        return form;
    }

    private ActionListener createButtonActionListener(JTextField name, JTextField description, JRadioButton fixed, JPanel panel, JDatePickerImpl startDatePicker, JDatePickerImpl endDatePicker, BufferedReader reader, PrintWriter writer, JTextField price) {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String nameValue = name.getText();
                String descriptionValue = description.getText();
                JComboBox<Common.DataItem> comboBox = (JComboBox<Common.DataItem>) panel.getComponent(0);
                Common.DataItem selectedDataItem = (Common.DataItem) comboBox.getSelectedItem();
                boolean isFixed = fixed.isSelected();

                String val = isFixed ? "Fixed" : "Dynamic";
                double prices = 0.0;
                try {
                    prices = isFixed ? Double.parseDouble(price.getText()) : 0.0;
                } catch (Exception x) {
                    x.printStackTrace();
                }

                Date startDate = (Date) startDatePicker.getModel().getValue();
                Date endDate = (Date) endDatePicker.getModel().getValue();

                Budget budget = new Budget();
                budget.setBudget(nameValue, descriptionValue, startDate, endDate, selectedDataItem.getId(), val, prices);
                String data = Common.jsonify("api/budgets", "post", budget.toJson());
                try {
                    writer.println(data);
                    reader.readLine();
                    SwingUtilities.invokeLater(() -> {
                        ActionLeftCard.show(ActionLeftPlane, "ViewProFile");
                        ActionRightCard.show(ActionRightPlane, "ViewTemp");
                    });
                } catch (Exception x) {
                    x.printStackTrace();
                }
            }
        };
    }

    private ActionListener updateButtonActionListener(JTextField name, JTextField description, JRadioButton fixed, JPanel panel, JDatePickerImpl startDatePicker, JDatePickerImpl endDatePicker, BufferedReader reader, PrintWriter writer, JTextField price, String id) {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String nameValue = name.getText();
                String descriptionValue = description.getText();
                JComboBox<Common.DataItem> comboBox = (JComboBox<Common.DataItem>) panel.getComponent(0);
                Common.DataItem selectedDataItem = (Common.DataItem) comboBox.getSelectedItem();
                boolean isFixed = fixed.isSelected();

                String val = isFixed ? "Fixed" : "Dynamic";
                double prices = 0.0;
                try {
                    prices = isFixed ? Double.parseDouble(price.getText()) : 0.0;
                } catch (Exception x) {
                    x.printStackTrace();
                }

                Date startDate = (Date) startDatePicker.getModel().getValue();
                Date endDate = (Date) endDatePicker.getModel().getValue();

                Budget budget = new Budget();
                budget.setBudget(nameValue, descriptionValue, startDate, endDate, selectedDataItem.getId(), val, prices);
                String data1 = Common.jsonify("api/budgets" + id, "put", budget.toJson());
                JSONObject obj = new JSONObject();
                obj.put("id", id);
                obj.put("accountantId", selectedDataItem.getId());

                String data2 = Common.jsonify("api/budgets/accountants/add", "post", obj);
                try {
                    writer.println(data1);
                    reader.readLine();
                    writer.println(data2);
                    reader.readLine();
                    SwingUtilities.invokeLater(() -> {
                        ViewBuget();
                        ActionLeftCard.show(ActionLeftPlane, "viewBudget");
                        ActionRightCard.show(ActionRightPlane, "addBudget");
                    });
                } catch (Exception x) {
                    x.printStackTrace();
                }
            }
        };
    }

    private MouseAdapter RemoveAcountantListener(JSONObject jsonObject, JPanel panel) {
        return new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    JComboBox<Common.DataItem> comboBox = (JComboBox<Common.DataItem>) panel.getComponent(0);
                    Common.DataItem selectedDataItem = (Common.DataItem) comboBox.getSelectedItem();
                    JSONObject obj = new JSONObject();
                    obj.put("id", jsonObject.getString("id"));
                    obj.put("accountantId", selectedDataItem.getId());
                    String data = Common.jsonify("api/budgets/accountants/remove", "post", obj);
                    writer.println(data);
                    reader.readLine();
                    SwingUtilities.invokeLater(() -> {
                        ViewBuget();
                        ActionLeftCard.show(ActionLeftPlane, "viewBudget");
                        ActionRightCard.show(ActionRightPlane, "addBudget");
                    });
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
    };
    private MouseAdapter EditBudgetListener(JSONObject jsonObject) {
        return new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    ActionLeftPlane.add(EditBudget(jsonObject), "EditBudget");
                    SwingUtilities.invokeLater(() -> {
                        ActionLeftCard.show(ActionLeftPlane, "EditBudget");
                    });
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
    };

    private MouseAdapter deleteBudgetListener(String id) {
        return new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    String data = Common.jsonify("api/budgets/" + id, "delete", user.toJson());
                    writer.println(data);
                    reader.readLine();
                    SwingUtilities.invokeLater(() -> {
                        ViewBuget();
                        ActionLeftCard.show(ActionLeftPlane, "viewBudget");
                        ActionRightCard.show(ActionRightPlane, "addBudget");
                    });
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
    };

}


