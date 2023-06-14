package Common;
import net.sourceforge.jdatepicker.JDatePicker;
import net.sourceforge.jdatepicker.impl.JDatePanelImpl;
import net.sourceforge.jdatepicker.impl.JDatePickerImpl;
import net.sourceforge.jdatepicker.impl.UtilDateModel;
import org.json.JSONObject;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import java.util.UUID;

public class Common {
    private JDatePicker datePicker;
    private JPanel panel;
    public  JComboBox<DataItem> comboBox;

    public static String CreateId() {
        return UUID.randomUUID().toString();
    }

    public static String jsonify(String path, String method , JSONObject data) {
        JSONObject jo = new JSONObject();
        jo.put("method", method);
        jo.put("path", path);
        jo.put("data", data.toString());
        return jo.toString();
    }

    public static void addPlaceholder(JTextField textField, String placeholder) {
        textField.setText(placeholder);
        textField.setForeground(Color.GRAY);

        textField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (textField.getText().equals(placeholder)) {
                    textField.setText("");
                    textField.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (textField.getText().isEmpty()) {
                    textField.setText(placeholder);
                    textField.setForeground(new Color(59, 89, 152));
                }
            }
        });
    }

    public static ImageIcon getRoundedIcon(ImageIcon originalIcon, int size, Color color) {
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

    public static ImageIcon resizeImage(ImageIcon originalIcon, int size) {
        Image image = originalIcon.getImage();
        BufferedImage bufferedImage = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2 = bufferedImage.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.drawImage(image, 0, 0, size, size, null);
        g2.dispose();

        return new ImageIcon(bufferedImage);
    }

    public JPanel createDatePickerForm() {
        panel = new JPanel(new FlowLayout());
        panel.setOpaque(false);
        datePicker = createDatePicker();
        panel.add((Component) datePicker);
        return panel;
    }

    private JDatePicker createDatePicker() {
        UtilDateModel dateModel = new UtilDateModel();
        JDatePanelImpl datePickerPanel = new JDatePanelImpl(dateModel);
        JDatePickerImpl datePickerImpl = new JDatePickerImpl(datePickerPanel, new DateLabelFormatter());

        Properties dateProperties = new Properties();
        datePickerImpl.setTextEditable(true);  // Enable manual date entry
        datePickerImpl.setShowYearButtons(true);  // Show year navigation buttons

        datePickerImpl.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedDate = datePickerImpl.getJFormattedTextField().getText();
                Calendar cal = Calendar.getInstance();
                cal.setTime(parseDate(selectedDate));
                dateModel.setValue(cal.getTime());
            }
        });

        return datePickerImpl;
    }

    private Date parseDate(String dateStr) {
        try {
            return new SimpleDateFormat("MM/dd/yyyy").parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Date getSelectedDate() {
        return (Date) datePicker.getModel().getValue();
    }

    private static class DateLabelFormatter extends JFormattedTextField.AbstractFormatter {
        private final String datePattern = "MM/dd/yyyy";
        private final java.text.SimpleDateFormat dateFormatter = new java.text.SimpleDateFormat(datePattern);

        @Override
        public Object stringToValue(String text) throws java.text.ParseException {
            return dateFormatter.parseObject(text);
        }

        @Override
        public String valueToString(Object value) throws java.text.ParseException {
            if (value != null) {
                Calendar cal = (Calendar) value;
                return dateFormatter.format(cal.getTime());
            }
            return "";
        }
    }

    public void addDataSelectionComponent(JPanel panel, DataItem[] dataItems) {
        panel.setLayout(new BorderLayout());

        comboBox = new JComboBox<>(dataItems);
        comboBox.setBackground(Color.WHITE);
        comboBox.setForeground( new Color(1, 102, 170));
        comboBox.setBorder(new EmptyBorder(0, 0, 0, 0));

        comboBox.setRenderer(new DataItemRenderer());
        panel.add(comboBox, BorderLayout.CENTER);
    }

    public JComboBox<DataItem> getComboBox() {
        return this.comboBox;
    }

    private static class DataItemRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            if (value instanceof DataItem) {
                DataItem dataItem = (DataItem) value;
                label.setText(dataItem.getName());
            }

            return label;
        }
    }

    public static class DataItem {
        private String id;
        private String name;

        public DataItem(String id, String name) {
            this.id = id;
            this.name = name;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
