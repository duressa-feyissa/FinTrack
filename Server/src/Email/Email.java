package Email;
import Common.Common;
import javax.mail.*;
import javax.mail.internet.*;
import java.sql.*;
import java.util.Random;
import java.util.Properties;

public class Email {

    public static boolean emailHandler(Connection connection, String recipient) {

        String sender = "";

        String password = "";

        Properties properties = System.getProperties();

        properties.setProperty("mail.smtp.host", "smtp.gmail.com");
        properties.setProperty("mail.smtp.port", "587");
        properties.setProperty("mail.smtp.starttls.enable", "true");
        properties.setProperty("mail.smtp.auth", "true");

        Session session = Session.getInstance(properties, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(sender, password);
            }
        });

        int verificationCode = generateVerificationCode();

        return sendVerificationCode(connection, session, sender, recipient, verificationCode);
    }

    public static int generateVerificationCode() {
        Random random = new Random();
        return 100000 + random.nextInt(900000);
    }

    public static boolean sendVerificationCode(Connection connection, Session session, String sender, String recipient, int verificationCode) {
        try {
            MimeMessage message = new MimeMessage(session);

            message.setFrom(new InternetAddress(sender));

            message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));

            message.setSubject("Verification Code");

            message.setText("Your verification code is: " + verificationCode);

            Transport.send(message);
            System.out.println("Email successfully sent");

            storeVerificationCodeInDatabase(connection, recipient, verificationCode);
            return true;
        } catch (MessagingException mex) {
            mex.printStackTrace();
            return false;
        }
    }

    public static void storeVerificationCodeInDatabase(Connection connection, String recipient, int verificationCode) {

        try {
            String id = Common.CreateId();
            String sql = "INSERT INTO temporary_codes (id, email, verification_code) VALUES (?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, id);
            statement.setString(2, recipient);
            statement.setInt(3, verificationCode);

            statement.executeUpdate();

            statement.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean verifyCode(Connection connection, String email, int verificationCode) {
        boolean match = false;
        try {
            String sql = "SELECT COUNT(*) FROM temporary_codes WHERE email = ? AND verification_code = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, email);
            statement.setInt(2, verificationCode);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                match = (count > 0);
            }
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return match;
    }
}
