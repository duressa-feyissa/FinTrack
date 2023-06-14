package image;

import Common.Common;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;

public class DatePickerForm extends JPanel {
    private Common common;

    public DatePickerForm() {
        common = new Common();

        setLayout(new BorderLayout());

        JPanel datePickerPanel = common.createDatePickerForm();
        add(datePickerPanel, BorderLayout.CENTER);

        JButton selectButton = new JButton("Select");
        selectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Date selectedDate = common.getSelectedDate();
                System.out.println("Selected Date: " + selectedDate);
            }
        });
        add(selectButton, BorderLayout.SOUTH);
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame frame = new JFrame("Main Application");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

                DatePickerForm datePickerForm = new DatePickerForm();
                frame.add(datePickerForm);

                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
    }
}
