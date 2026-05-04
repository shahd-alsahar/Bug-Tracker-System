/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package bugtrackerprojectsh;

import java.io.BufferedReader;
import java.io.FileReader;

public class login {

    public static String[] login(String email, String password) {

        try (BufferedReader reader = new BufferedReader(new FileReader("user.txt"))) {

            String line;

            while ((line = reader.readLine()) != null) {

                String[] data = line.split(",");

                if (data.length < 5) continue;

                String userEmail = data[2].trim().toLowerCase();
                String userPassword = data[3].trim();

                if (userEmail.equals(email.trim().toLowerCase()) &&
                    userPassword.equals(password.trim())) {

                    
                    return new String[]{data[1].trim(), data[4].trim()};
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}


