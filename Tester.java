
package bugtrackerprojectsh;
 
import java.io.*;
import java.util.Scanner;
 
public class Tester {
 
   
 
    private static final String BASE_PATH = System.getProperty("user.dir") + File.separator;
 
    private int    testerId;
    private String testerName;
 
    public Tester(int id, String name) {
        this.testerId   = id;
        this.testerName = name;
    }
 
    
 
    public int countMyOpenBugs() {
        return countBugsByStatus("open");
    }
 
    public int countMyClosedBugs() {
        return countBugsByStatus("closed");
    }
 
    public int countInboxEmails() {
        int count = 0;
        File file = new File(BASE_PATH + "emails.txt");
        try (Scanner sc = new Scanner(file)) {
            while (sc.hasNextLine()) {
                String line = sc.nextLine().trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split(",", -1);
                if (parts.length >= 2 && parts[1].trim().equalsIgnoreCase(testerName))
                    count++;
            }
        } catch (FileNotFoundException e) {
            
        }
        return count;
    }
 
   
    private int countBugsByStatus(String status) {
        int count = 0;
        File file = new File(BASE_PATH + "bug.txt");
        try (Scanner sc = new Scanner(file)) {
            while (sc.hasNextLine()) {
                String line = sc.nextLine().trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split(",", -1);
                if (parts.length < 9) continue;
                // [7] = Status,  [8] = Tester name
                if (parts[7].trim().equalsIgnoreCase(status)
                        && parts[8].trim().equalsIgnoreCase(testerName))
                    count++;
            }
        } catch (FileNotFoundException e) {
            
        }
        return count;
    }
 
    
    public void displayOpenBugs() {
        File bugFile = new File(BASE_PATH + "bug.txt");
        if (!bugFile.exists()) { System.out.println("No bugs file found."); return; }
        try (BufferedReader reader = new BufferedReader(new FileReader(bugFile))) {
            String line;
            System.out.println("\n=== Open Bugs for: " + testerName + " ===");
            boolean found = false;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] data = line.split(",", -1);
                if (data.length < 9) continue;
                if (data[7].trim().equalsIgnoreCase("open")
                        && data[8].trim().equalsIgnoreCase(testerName)) {
                    System.out.printf("%-8s | %-35s | %-8s | %-10s | %s%n",
                            data[0], data[1], data[3], data[7], data[5]);
                    found = true;
                }
            }
            if (!found) System.out.println("No open bugs found for " + testerName);
        } catch (IOException e) {
            System.out.println("Error reading bug.txt: " + e.getMessage());
        }
    }
 
    
    public void addBug() {
        Scanner input = new Scanner(System.in);
        File bugFile  = new File(BASE_PATH + "bug.txt");
        int newId = generateNextId(bugFile);
 
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(bugFile, true))) {
            System.out.print("Bug Title: ");        String title  = input.nextLine().trim();
            System.out.print("Bug Type: ");         String type   = input.nextLine().trim();
            System.out.print("Priority: ");         String prio   = input.nextLine().trim();
            System.out.print("Level: ");            String level  = input.nextLine().trim();
            System.out.print("Project: ");          String proj   = input.nextLine().trim();
            System.out.print("Developer name: ");   String dev    = input.nextLine().trim();
            System.out.print("Screenshot path: ");  String shot   = input.nextLine().trim();
 
            String date   = java.time.LocalDate.now().toString();
            String status = "Open";
 
            
            writer.write(String.join(",", String.valueOf(newId), title, type, prio,
                    level, proj, date, status, testerName, dev, shot));
            writer.newLine();
            System.out.println("Bug #" + newId + " added!");
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
 
    
    private int generateNextId(File bugFile) {
        int maxId = 0;
        if (!bugFile.exists()) return 1;
        try (BufferedReader reader = new BufferedReader(new FileReader(bugFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", -1);
                try { maxId = Math.max(maxId, Integer.parseInt(parts[0].trim())); }
                catch (NumberFormatException ignored) {}
            }
        } catch (IOException ignored) {}
        return maxId + 1;
    }
 
    
    
    public int    getTesterId()   { return testerId;   }
    public String getTesterName() { return testerName; }
}