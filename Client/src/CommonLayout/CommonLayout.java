package CommonLayout;

import Common.Common;
import User.User;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.ImageObserver;

public class CommonLayout {

    public JPanel container = new JPanel();
    protected  JPanel leftPanel;
    protected JPanel rightPanel;
    protected CardLayout actionsCardLayout;
    protected JPanel actions;
    protected  JPanel bottomPart;
    protected JLabel currentRoute;
    User user;
    public String path;
    protected JPanel progress;
    public CommonLayout(User user, String path){
        this.path =path;
        this.user = user;
        container.setPreferredSize(new Dimension(800, 600));
        leftPanel = new JPanel() {
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                Image img = Toolkit.getDefaultToolkit().getImage(path + "src/image/main.jpg");
                ImageObserver observer = new ImageIcon(img).getImageObserver();
                g.drawImage(img, 0, 0, observer);
            }
        };

        Dimension maxLeftSize = new Dimension(220, leftPanel.getPreferredSize().height);
        leftPanel.setMaximumSize(maxLeftSize);
        rightPanel = new JPanel();
        leftPanel.setOpaque(false);
        LeftSetUp();
        RightSetUp();

        rightPanel.setBackground(Color.WHITE);

        container.setLayout(new BorderLayout());

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setResizeWeight(0.05);
        splitPane.setDividerSize(5);
        container.add(splitPane, BorderLayout.CENTER);
    }

    public JPanel getContainer(){
        return this.container;
    }

    void LeftSetUp() {
        JPanel childComponent = new JPanel(new BorderLayout());
        childComponent.setOpaque(false);

        //Upper Part
        ImageIcon logo = new ImageIcon(path + "src/image/logo (2).png");
        JLabel labelLogoHolder = new JLabel(logo);

        JLabel textName = new JLabel("FINTRACk");
        textName.setFont(new Font("Roboto", Font.BOLD, 30));
        textName.setForeground(new Color(238, 244, 247));

        JPanel upperPart = new JPanel(new BorderLayout());
        upperPart.setOpaque(false);

        JPanel logoholder = new JPanel(new FlowLayout(FlowLayout.CENTER));
        logoholder.setOpaque(false);
        logoholder.add(labelLogoHolder);

        JPanel nameholder = new JPanel(new FlowLayout(FlowLayout.CENTER));
        nameholder.setOpaque(false);
        nameholder.add(textName);

        upperPart.add(logoholder, BorderLayout.NORTH);
        upperPart.add(nameholder, BorderLayout.CENTER);
        childComponent.add(upperPart, BorderLayout.NORTH);

        bottomPart = new JPanel(new BorderLayout());
        bottomPart.setOpaque(false);
        childComponent.add(bottomPart, BorderLayout.CENTER);

        leftPanel.setLayout(new BorderLayout());
        leftPanel.add(childComponent);
    }

    void RightSetUp() {
        rightPanel.setLayout(new BorderLayout());
        rightPanel.setBackground(new Color(245, 245, 245));

        JPanel upperPanel = getUpper();
        JPanel bottomPanel = getBottom();

        rightPanel.add(upperPanel, BorderLayout.NORTH);
        rightPanel.add(bottomPanel, BorderLayout.CENTER);
    }


    JPanel getUpper() {
        JPanel upperPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        upperPanel.setBackground(Color.white);
        upperPanel.setPreferredSize(new Dimension(100, 90));
        upperPanel.setOpaque(false);

        JPanel user = new JPanel(new BorderLayout());
        user.setOpaque(false);
        JLabel name = new JLabel(this.user.getUsername());
        JLabel photo = new JLabel();
        name.setHorizontalAlignment(SwingConstants.CENTER);
        ImageIcon imageIcon = new ImageIcon(path + "src/image/logo.png");
        photo.setIcon(Common.getRoundedIcon(imageIcon, 60, new Color(1, 102, 170)));

        user.add(photo, BorderLayout.NORTH);
        user.add(name, BorderLayout.SOUTH);
        upperPanel.add(user);
        return upperPanel;
    }

    JPanel getBottom() {
        JPanel bottom = new JPanel(new BorderLayout(20, 30));
        bottom.setBackground(new Color(245, 245, 245));

        actions = new JPanel(new GridLayout(1,2, 40, 10));

        JPanel route = new JPanel(new FlowLayout(FlowLayout.LEFT));
        progress = new JPanel();

        currentRoute = new JLabel("Home");
        currentRoute.setFont(new Font("Roboto", Font.PLAIN, 24));
        currentRoute.setForeground(new Color(1, 102, 170));
        route.add(currentRoute);
        route.setOpaque(false);
        progress.setOpaque(false);
        route.setPreferredSize(new Dimension(500, 30));
        actions.setPreferredSize(new Dimension(500, 650));

        bottom.add(route, BorderLayout.NORTH);
        bottom.add(progress, BorderLayout.CENTER);
        bottom.add(actions, BorderLayout.SOUTH);

        int padding = 30;
        bottom.setBorder(new EmptyBorder(padding, padding, padding, padding));

        JPanel bottomWrapper = new JPanel(new BorderLayout());
        bottomWrapper.setPreferredSize(new Dimension(500, 710));
        bottomWrapper.add(bottom, BorderLayout.NORTH);
        return bottomWrapper;
    }


}