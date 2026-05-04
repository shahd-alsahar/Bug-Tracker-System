package bugtrackerprojectsh;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

public class UserService {

    public static ArrayList<String> getDevelopers() {

        ArrayList<String> devs = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader("user.txt"))) {

            String line;

            while ((line = br.readLine()) != null) {

                String[] data = line.split(",");

                if (data.length >= 5) {

                    String role = data[4].trim();

                    if (role.equalsIgnoreCase("developer")) {
                        devs.add(data[1]); // name
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return devs;
    }
}
