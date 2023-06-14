package Login;

import Accountant.Accountant;
import Admin.Admin;
import Common.Common;
import RoundedBorder.RoundedBorder;
import User.User;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Login extends JFrame {

    JPanel container;
    JPanel leftDiv;
    JPanel rightDiv;
    JPanel signUpPanel;
    JPanel signInPanel;
    public CardLayout cardLayout;
    public JPanel cardPanel;
    public JPanel mainEnter;
    public CardLayout cardLayoutMain;
    PrintWriter writer;
    BufferedReader reader;
    public JPanel cardPanelMain;
    public String path = "/home/kena/Documents/AP/Client/";
    Socket socket;

    Login(String path, String ip, int port) {
        path = path;
        String serverAddress = ip;// "localhost";
        int serverPort = port;

        try {
            socket = new Socket(serverAddress, serverPort);
            writer = new PrintWriter(socket.getOutputStream(), true);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        setTitle("Login Form");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        HomeInterface();
        this.add(cardPanelMain);
        this.setSize(1000, 1000);
        this.setPreferredSize(new Dimension(1000, 1000));
        setVisible(true);
    }

    void HomeInterface() {
        container = new JPanel();
        mainEnter = new JPanel(null) {
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                Image img = Toolkit.getDefaultToolkit().getImage( path + "src/image/main.jpg");
                ImageObserver observer = new ImageIcon(img).getImageObserver();
                g.drawImage(img, 0, 0, observer);
            }
        };

        MainEnterStepUP();

        container.setLayout(new BorderLayout());

        leftDiv = new JPanel() {
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                Image img = Toolkit.getDefaultToolkit().getImage(path + "src/image/main.jpg");
                ImageObserver observer = new ImageIcon(img).getImageObserver();
                g.drawImage(img, 0, 0, observer);
            }
        };
        AddContentLeft();

        rightDiv = new JPanel() {
            @Override
            public Dimension getPreferredSize() {
                return container.getSize(); // Set preferred size to match the container size
            }
        };

        container.setLayout(new GridLayout());
        rightDiv.setBackground(Color.white);
        rightDiv.setLayout(new BorderLayout()); // Use a layout manager for the rightDiv panel

        JPanel Margin = new JPanel();
        Margin.setPreferredSize(new Dimension(400, 125));
        Margin.setOpaque(false);
        cardLayoutMain = new CardLayout();
        cardPanelMain = new JPanel(cardLayoutMain);
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        SignIn();
        SignUp();

        cardPanel.add(signInPanel, "signIn");
        cardPanel.add(signUpPanel, "signUp");
        rightDiv.add(Margin, BorderLayout.NORTH);
        rightDiv.add(cardPanel, BorderLayout.CENTER);
        container.add(leftDiv);
        container.add(rightDiv);
        cardPanelMain.add(mainEnter, "mainEnter");
        cardPanelMain.add(container, "container");
    }

    public void SignIn() {
        signInPanel = new JPanel();
        signInPanel.setBackground(Color.WHITE);

        JPanel form = new JPanel(null);
        form.setBackground(Color.WHITE);
        form.setBorder(new RoundedBorder(30));
        form.setPreferredSize(new Dimension(450, 575));

        ImageIcon imageIcon = new ImageIcon(path + "src/image/logo.png");
        JLabel imageLabel = new JLabel();
        imageLabel.setIcon(getRoundedIcon(imageIcon, 90, new Color(42, 193, 255)));
        imageLabel.setBounds(180, 10, 100, 100);

        JPanel formContainer = new JPanel(null);
        formContainer.setBackground(Color.WHITE);
        formContainer.setBounds(25, 180, 400, 380);

        JLabel hello = new JLabel("Hello Again!");
        Font font = new Font("News Gothic MT", Font.BOLD, 25);
        hello.setForeground(new Color(59, 89, 152));
        hello.setBounds(140, 120, 180, 40);
        hello.setFont(font);

        JTextField email = new JTextField();
        email.setBorder(new RoundedBorder(10));
        email.setBounds(50, 30, 300, 40);

        JPasswordField password = new JPasswordField();
        password.setBorder(new RoundedBorder(10));
        password.setBounds(50, 90, 300, 40);

        JButton login = new JButton();
        login.setBounds(50, 150, 300, 40);
        login.setBackground(new Color(59, 89, 152));
        login.setText("Sign In");
        login.setBorderPainted(false);
        font = new Font("News Gothic MT", Font.BOLD, 25);
        login.setFont(font);
        login.setBorder(new RoundedBorder(10));
        login.setForeground(Color.WHITE);

        Common.addPlaceholder(email, "Email");
        Common.addPlaceholder(password, "Password");

        login.addActionListener(e -> handleSignIn(email, password));

        JLabel message = new JLabel("Don't have account yet?! ");
        message.setBounds(50, 350, 300, 30);
        font = new Font("News Gothic MT", Font.BOLD, 15);
        message.setFont(font);
        message.setForeground(new Color(59, 89, 152));

        JButton toggleButtonSignUp = new JButton("Sign Up");
        font = new Font("News Gothic MT", Font.BOLD, 15);
        toggleButtonSignUp.setFont(font);
        toggleButtonSignUp.setForeground(new Color(59, 89, 152));
        toggleButtonSignUp.addActionListener(e -> cardLayout.next(cardPanel));
        toggleButtonSignUp.setBounds(265, 350, 120, 30);
        toggleButtonSignUp.setBorderPainted(false);
        toggleButtonSignUp.setBackground(Color.white);

        formContainer.add(email);
        formContainer.add(password);
        formContainer.add(login);
        formContainer.add(message);
        formContainer.add(toggleButtonSignUp);
        form.add(imageLabel);
        form.add(hello);
        form.add(formContainer);

        signInPanel.add(form);
    }

    void SignUp() {
        signUpPanel = new JPanel();
        signUpPanel.setBackground(Color.WHITE);

        JPanel form = new JPanel(null);
        form.setBackground(Color.WHITE);
        form.setBorder(new RoundedBorder(30));
        form.setPreferredSize(new Dimension(450, 575));

        ImageIcon imageIcon = new ImageIcon(path + "src/image/logo.png");
        JLabel imageLabel = new JLabel();
        imageLabel.setIcon(getRoundedIcon(imageIcon, 90, new Color(42, 193, 255)));
        imageLabel.setBounds(180, 10, 100, 100);

        JPanel formContainer = new JPanel(null);
        formContainer.setBackground(Color.WHITE);
        formContainer.setBounds(25, 180, 400, 380);

        JLabel hello = new JLabel("Hello Again!");
        Font font = new Font("News Gothic MT", Font.BOLD, 25);
        hello.setForeground(new Color(59, 89, 152));
        hello.setBounds(140, 120, 180, 40);
        hello.setFont(font);

        JTextField username = new JTextField();
        username.setBorder(new RoundedBorder(10));
        username.setBounds(50, 30, 300, 40);

        JTextField email = new JTextField();
        email.setBorder(new RoundedBorder(10));
        email.setBounds(50, 90, 300, 40);

        JTextField secretCode = new JTextField();
        secretCode.setBorder(new RoundedBorder(10));
        secretCode.setBounds(50, 150, 300, 40);

        JPasswordField password = new JPasswordField();
        password.setBorder(new RoundedBorder(10));
        password.setBounds(50, 210, 300, 40);

        JButton login = new JButton();
        login.setBounds(50, 270, 300, 40);
        login.setBackground(new Color(59, 89, 152));
        login.setText("Sign Up");
        font = new Font("News Gothic MT", Font.BOLD, 25);
        login.setFont(font);
        login.setBorderPainted(false);
        login.setBorder(new RoundedBorder(10));
        login.setForeground(Color.WHITE);

        JLabel message = new JLabel("Already Have an Account! ");
        message.setBounds(50, 350, 300, 30);
        font = new Font("News Gothic MT", Font.BOLD, 15);
        message.setFont(font);
        message.setForeground(new Color(59, 89, 152));

        Common.addPlaceholder(username, "Username");
        Common.addPlaceholder(email, "Email");
        Common.addPlaceholder(secretCode, "Secret Code");
        Common.addPlaceholder(password, "Password");

        login.addActionListener(e -> handleSignUp(username, email, secretCode, password));

        JButton toggleButtonSignUp = new JButton("Sign In");
        font = new Font("News Gothic MT", Font.BOLD, 15);
        toggleButtonSignUp.setFont(font);
        toggleButtonSignUp.setForeground(new Color(59, 89, 152));
        toggleButtonSignUp.addActionListener(e -> cardLayout.next(cardPanel));
        toggleButtonSignUp.setBounds(260, 350, 120, 30);
        toggleButtonSignUp.setBorderPainted(false);
        toggleButtonSignUp.setBackground(Color.white);

        formContainer.add(username);
        formContainer.add(email);
        formContainer.add(secretCode);
        formContainer.add(password);
        formContainer.add(login);
        formContainer.add(toggleButtonSignUp);
        formContainer.add(message);

        form.add(imageLabel);
        form.add(hello);
        form.add(formContainer);

        signUpPanel.add(form);
    }

    void MainEnterStepUP() {
        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        centerPanel.setOpaque(false);
        JPanel childComponent = new JPanel(null);
        childComponent.setOpaque(false);

        childComponent.setPreferredSize(new Dimension(400, 400));

        JPanel logoholder = new JPanel(null);
        logoholder.setOpaque(false);

        JPanel nameholder = new JPanel(null);
        nameholder.setOpaque(false);

        JPanel mottoholder = new JPanel(null);
        mottoholder.setOpaque(false);

        JPanel buttomHolder = new JPanel(null);
        buttomHolder.setOpaque(false);

        logoholder.setBounds(0, 0, 400, 200);
        nameholder.setBounds(0, 200, 400, 80);
        mottoholder.setBounds(0, 250, 400, 100);
        buttomHolder.setBounds(0, 350, 400, 50);

        ImageIcon logo = new ImageIcon(path + "src/image/logo (2).png");
        JLabel labelLogoHolder = new JLabel();
        labelLogoHolder.setIcon(logo);
        labelLogoHolder.setBounds(100, 0, 200, 200);
        logoholder.add(labelLogoHolder);

        JLabel textName = new JLabel("FINTRACk");
        textName.setFont(new Font("Roboto", Font.BOLD, 70));
        textName.setForeground(new Color(238, 244, 247));
        textName.setBounds(2, 0, 400, 100);
        nameholder.add(textName);

        JLabel textMotto = new JLabel("Real Accounting Services For You!");
        textMotto.setFont(new Font("Roboto", Font.BOLD, 17));
        textMotto.setForeground(new Color(238, 244, 247));
        textMotto.setBounds(30, 0, 400, 100);
        mottoholder.add(textMotto);

        JButton getStart = new JButton("GET START");
        getStart.setFont(new Font("Open Sans", Font.BOLD, 30));
        getStart.setBounds(75, 0, 250, 50);
        getStart.setBorder(BorderFactory.createEmptyBorder());
        getStart.setBackground(Color.white);
        getStart.setForeground(new Color(1, 102, 170));
        buttomHolder.add(getStart);

        getStart.addActionListener(e -> cardLayoutMain.show(cardPanelMain, "container"));

        childComponent.add(logoholder);
        childComponent.add(nameholder);
        childComponent.add(mottoholder);
        childComponent.add(buttomHolder);
        centerPanel.add(childComponent);

        mainEnter.setLayout(new BoxLayout(mainEnter, BoxLayout.Y_AXIS));

        mainEnter.add(Box.createVerticalGlue());

        mainEnter.add(centerPanel);

        mainEnter.add(Box.createVerticalGlue());
    }

    void AddContentLeft() {
        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        centerPanel.setOpaque(false); // Make the panel transparent
        JPanel childComponent = new JPanel(null);
        childComponent.setOpaque(false);

        childComponent.setPreferredSize(new Dimension(400, 400));

        JPanel logoholder = new JPanel(null);
        logoholder.setOpaque(false);

        JPanel nameholder = new JPanel(null);
        nameholder.setOpaque(false);

        JPanel mottoholder = new JPanel(null);
        mottoholder.setOpaque(false);

        JPanel buttomHolder = new JPanel(null);
        buttomHolder.setOpaque(false);

        logoholder.setBounds(0, 0, 400, 200);
        nameholder.setBounds(0, 200, 400, 80);
        mottoholder.setBounds(0, 250, 400, 100);
        buttomHolder.setBounds(0, 350, 400, 50);

        ImageIcon logo = new ImageIcon(path + "src/image/logo (2).png");
        JLabel labelLogoHolder = new JLabel();
        labelLogoHolder.setIcon(logo);
        labelLogoHolder.setBounds(100, 0, 200, 200);
        logoholder.add(labelLogoHolder);

        JLabel textName = new JLabel("FINTRACk");
        textName.setFont(new Font("Roboto", Font.BOLD, 70));
        textName.setForeground(new Color(238, 244, 247));
        textName.setBounds(2, 0, 400, 100);
        nameholder.add(textName);

        JLabel textMotto = new JLabel("Real Accounting Services For You!");
        textMotto.setFont(new Font("Roboto", Font.BOLD, 17));
        textMotto.setForeground(new Color(238, 244, 247));
        textMotto.setBounds(30, 0, 400, 100);
        mottoholder.add(textMotto);

        childComponent.add(logoholder);
        childComponent.add(nameholder);
        childComponent.add(mottoholder);
        centerPanel.add(childComponent);

        leftDiv.setLayout(new BoxLayout(leftDiv, BoxLayout.Y_AXIS));
        leftDiv.add(Box.createVerticalGlue());
        leftDiv.add(centerPanel);
        leftDiv.add(Box.createVerticalGlue());
    }

    private ImageIcon getRoundedIcon(ImageIcon originalIcon, int size, Color color) {
        Image image = originalIcon.getImage();
        BufferedImage bufferedImage = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2 = bufferedImage.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(color);

        Stroke stroke = new BasicStroke(1);
        g2.setStroke(stroke);
        g2.drawOval(5, 5, size - 10, size - 10);

        g2.setClip(new Ellipse2D.Float(5, 5, size - 10, size - 10));
        g2.drawImage(image, 0, 0, size, size, null);
        g2.dispose();

        return new ImageIcon(bufferedImage);
    }

    void handleSignUp(JTextField username, JTextField email, JTextField secretCode, JPasswordField password) {
        String enteredUsername = username.getText();
        String enteredEmail = email.getText();
        String enteredSecretCode = secretCode.getText();
        String enteredPassword = new String(password.getPassword());

        try {
            User user = new User();
            user.setUser(enteredUsername, enteredEmail, enteredPassword, false);

            String data = Common.jsonify("api/users", "post", user.toJson());
            writer.println(data);
            String response = reader.readLine();
            System.out.println(response);
            String[] array = response.split(" ", 2);

            if (array[0].equals("200")) {
                SwingUtilities.invokeLater(() -> {
                    cardLayout.show(cardPanel, "signIn");
                    System.out.println("Redirected to signIn");
                });
            } else {
                // Handle unsuccessful signup
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Username: " + enteredUsername);
        System.out.println("Email: " + enteredEmail);
        System.out.println("Secret Code: " + enteredSecretCode);
        System.out.println("Password: " + enteredPassword);
    }

    void handleSignIn(JTextField email, JPasswordField password) {
        String enteredEmail = email.getText();
        String enteredPassword = new String(password.getPassword());

        User user = new User();
        user.setUser("", enteredEmail, enteredPassword, false);
        String data = Common.jsonify("api/auth", "post", user.toJson());
        try {
            System.out.println(data);
            writer.println(data);
            String response = reader.readLine();
            System.out.println(response);
            String[] array = response.split(" ", 2);
            System.out.println(array[0]);
            if (array[0].equals("200")) {
                JSONObject obj = new JSONObject(array[1]);
                user.JsonToObj(array[1]);
                if (obj.getBoolean("isAdmin")) {
                    Admin admin = new Admin(user, reader, writer, path);
                    admin.setLogin(this);
                    cardPanelMain.add(admin.getContainer(), "admin");
                    SwingUtilities.invokeLater(() -> {
                        cardLayoutMain.show(cardPanelMain, "admin");
                    });
                } else {
                    Accountant accountant = new Accountant(user, reader, writer, path);
                    cardPanelMain.add(accountant.getContainer(), "accountant");
                    accountant.setLogin(this);
                    SwingUtilities.invokeLater(() -> {
                        cardLayoutMain.show(cardPanelMain, "accountant");
                    });
                }
            } else {
                JOptionPane.showMessageDialog(null,
                        "Wrong password entered!",
                        "Authentication",
                        JOptionPane.ERROR_MESSAGE);

                SignIn();
                SwingUtilities.invokeLater(() -> {
                    cardLayout.show(cardPanel, "signIn");
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Email: " + enteredEmail);
        System.out.println("Password: " + enteredPassword);
    }

    void close() {
        try {
            writer.close();
            reader.close();
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        String ip = "localhost";
        int port = 8080;
        String path = "";
        if (args.length > 0) {
            path += args[0] + "/Client/";
            if(args.length > 1) {
                ip = args[1];
                if (args.length == 2) {
                    port = Integer.parseInt(args[2]);
                }
            }
        }
        Login login = new Login(path, ip, port);
    }
}
