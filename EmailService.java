package bugtrackerprojectsh;

import java.io.FileWriter;
import java.io.PrintWriter;

public class EmailService {

   public static void sendEmail(String from, String to, String subject, String message) {
    try (PrintWriter out = new PrintWriter(new FileWriter("emails.txt", true))) {

        String fromEmail = from.contains("@") ? from : from + "@gmail.com";
        String toEmail   = to.contains("@") ? to : to + "@gmail.com";

        out.println("From: " + fromEmail);
        out.println("To: " + toEmail);
        out.println("Subject: " + subject);
        out.println("Message: " + message);
        out.println("--------------------------------------------------");

    } catch (Exception e) {
        e.printStackTrace();
    }
}

}

    

