/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package bugtrackerprojectsh;
 
import java.io.*;
import java.util.*;
 
public class Admin {
 
    private static final String BUG_FILE  = "bug.txt";
    private static final String USER_FILE = "users.txt";
 
    
    public List<String[]> getAllBugs() {
        List<String[]> bugs = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(BUG_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] d = line.split(",", -1);
                if (d.length >= 10) bugs.add(d);
            }
        } catch (FileNotFoundException e) {
            System.out.println("Bug file not found: " + BUG_FILE);
        } catch (IOException e) {
            System.out.println("Error reading bug.txt: " + e.getMessage());
        }
        return bugs;
    }
 
    
    public void viewAllBugs() {
        List<String[]> bugs = getAllBugs();
        System.out.println("\n--- All Bugs ---");
        bugs.forEach(d -> System.out.println(String.join(",", d)));
    }
 
    
    public List<String[]> getAllUsers() {
        List<String[]> users = new ArrayList<>();
        File f = new File(USER_FILE);
        if (!f.exists()) return users;
 
        try (BufferedReader reader = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split(",", -1);
                if (parts.length >= 5 && !parts[0].trim().isEmpty()) {
                    users.add(new String[]{
                        parts[0].trim(), // id
                        parts[1].trim(), // name
                        parts[2].trim(), // email
                        parts[3].trim(), // password
                        parts[4].trim()  // role
                    });
                } else if (parts.length >= 3) {
                    
                    users.add(new String[]{
                        String.valueOf(users.size() + 1),
                        parts[0].trim(),
                        parts[0].trim() + "@system.com",
                        parts[1].trim(),
                        parts[2].trim()
                    });
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading users.txt: " + e.getMessage());
        }
        return users;
    }
 
    
    public boolean addUser(String id, String name, String email,
                           String password, String role) {
        try (BufferedWriter writer = new BufferedWriter(
                new FileWriter(USER_FILE, true))) {
            writer.write(id + "," + name + "," + email + ","
                       + password + "," + role);
            writer.newLine();
            return true;
        } catch (IOException e) {
            System.out.println("Error writing to users.txt: " + e.getMessage());
            return false;
        }
    }
 
    
    public boolean deleteUser(String username) {
        return rewriteUsers(username, null);
    }
 
   
    public boolean updateUser(String username, String[] updated) {
        return rewriteUsers(username, updated);
    }
 
   
    public boolean saveAllUsers(List<String[]> users) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(USER_FILE))) {
            for (String[] u : users)
                pw.println(u[0] + "," + u[1] + "," + u[2] + ","
                         + u[3] + "," + u[4]);
            return true;
        } catch (IOException e) {
            System.out.println("Error saving users: " + e.getMessage());
            return false;
        }
    }
 
   
    public void addUser() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter ID: ");       String id    = sc.nextLine();
        System.out.print("Enter Name: ");     String name  = sc.nextLine();
        System.out.print("Enter Email: ");    String email = sc.nextLine();
        System.out.print("Enter Password: "); String pass  = sc.nextLine();
        System.out.print("Enter Role (tester / developer / pm / admin): ");
        String role = sc.nextLine();
        boolean ok = addUser(id, name, email, pass, role);
        System.out.println(ok ? "User added successfully!" : "Error adding user.");
    }
 
    
    public void deleteUserConsole(String userId) {
        
        File inputFile = new File(USER_FILE);
        File tempFile  = new File("users_temp.txt");
        boolean found  = false;
 
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
 
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] data = line.split(",", -1);
                if (data.length < 5) continue;
                if (data[0].trim().equals(userId)) { found = true; continue; }
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error deleting user: " + e.getMessage());
            return;
        }
 
        inputFile.delete();
        tempFile.renameTo(inputFile);
        System.out.println(found ? "User deleted successfully!" : "User not found.");
    }
 
   
    public void updateUserConsole(String userId) {
        Scanner sc     = new Scanner(System.in);
        File inputFile = new File(USER_FILE);
        File tempFile  = new File("users_temp.txt");
        boolean found  = false;
 
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
 
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] data = line.split(",", -1);
                if (data.length < 5) continue;
                if (data[0].trim().equals(userId)) {
                    found = true;
                    System.out.print("Enter new name: ");     data[1] = sc.nextLine();
                    System.out.print("Enter new email: ");    data[2] = sc.nextLine();
                    System.out.print("Enter new password: "); data[3] = sc.nextLine();
                    System.out.print("Enter new role: ");     data[4] = sc.nextLine();
                    line = String.join(",", data);
                }
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error updating user: " + e.getMessage());
            return;
        }
 
        inputFile.delete();
        tempFile.renameTo(inputFile);
        System.out.println(found ? "User updated successfully!" : "User not found.");
    }
 
    
    private boolean rewriteUsers(String username, String[] updated) {
        File input  = new File(USER_FILE);
        File temp   = new File("users_temp.txt");
        boolean found = false;
 
        try (BufferedReader reader = new BufferedReader(new FileReader(input));
             BufferedWriter writer = new BufferedWriter(new FileWriter(temp))) {
 
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] data = line.split(",", -1);
                if (data.length < 5) continue;
 
                if (data[1].trim().equalsIgnoreCase(username)) {
                    found = true;
                    if (updated != null) {          
                        writer.write(String.join(",", updated));
                        writer.newLine();
                    }
                    
                } else {
                    writer.write(line);
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            System.out.println("Error rewriting users: " + e.getMessage());
            return false;
        }
 
        input.delete();
        temp.renameTo(input);
        return found;
    }
}
