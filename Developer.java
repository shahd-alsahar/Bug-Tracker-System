package bugtrackerprojectsh;
 
import java.io.*;
import java.util.*;
import bugtrackerprojectsh.EmailService;
 
public class Developer {
 
    
    private static final String BASE_PATH = System.getProperty("user.dir") + File.separator;
 
    private int    developerId;
    private String developerName;
 
    public Developer(int id, String name) {
        this.developerId   = id;
        this.developerName = name;
    }
 
    
 
    public int countAssignedBugs() {
        return countBugsByStatus(null); 
    }
 
    public int countBugsByStatus(String status) {
        int count = 0;
        File file = new File(BASE_PATH + "bug.txt");
        try (Scanner sc = new Scanner(file)) {
            while (sc.hasNextLine()) {
                String line = sc.nextLine().trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split(",", -1);
                if (parts.length < 10) continue;
                
                boolean devMatch = parts[9].trim().equalsIgnoreCase(developerName);
                boolean statusMatch = (status == null)
                    || parts[7].trim().equalsIgnoreCase(status);
                if (devMatch && statusMatch) count++;
            }
        } catch (FileNotFoundException e) {
            
        }
        return count;
    }
 
    
    public void displayAssignedBugs() {
        File file = new File(BASE_PATH + "bug.txt");
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            System.out.println("\n=== Assigned Bugs for: " + developerName + " ===");
            System.out.println("-".repeat(90));
            System.out.printf("%-8s | %-30s | %-12s | %-8s | %-15s | %-10s%n",
                "ID", "Title", "Type", "Priority", "Project", "Status");
            System.out.println("-".repeat(90));
            boolean found = false;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] data = line.split(",", -1);
                if (data.length < 10) continue;
                
                if (data[9].trim().equalsIgnoreCase(developerName)) {
                    System.out.printf("%-8s | %-30s | %-12s | %-8s | %-15s | %-10s%n",
                        data[0].trim(), data[1].trim(), data[2].trim(),
                        data[3].trim(), data[5].trim(), data[7].trim());
                    found = true;
                }
            }
            if (!found) System.out.println("No bugs assigned to " + developerName);
        } catch (IOException e) {
            System.out.println("Error reading bug.txt: " + e.getMessage());
        }
    }
 
    
    public void markBugAsClosed(String bugIdToClose) {
        File inputFile = new File(BASE_PATH + "bug.txt");
        File tempFile  = new File(BASE_PATH + "bug_temp.txt");
 
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
 
            String line;
            boolean found = false;
 
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
 
                String[] data = line.split(",", -1);
                if (data.length < 10) {
                    writer.write(line);
                    writer.newLine();
                    continue;
                }
 
                String bugId      = data[0].trim();   // [0] = ID
                String testerName = data[8].trim();   // [8] = Tester
                String assignedDev = data[9].trim();  // [9] = Developer
 
                if (bugId.equals(bugIdToClose)
                        && assignedDev.equalsIgnoreCase(developerName)) {
                    data[7] = "Closed"; 
                    found = true;
 
                    
                    EmailService.sendEmail(
                        developerName,
                        testerName,
                        "Bug Closed",
                        "Bug #" + bugIdToClose + " (" + data[1].trim()
                            + ") has been fixed by " + developerName
                    );
                }
 
                writer.write(String.join(",", data));
                writer.newLine();
            }
 
            reader.close();
            writer.close();
 
            if (!inputFile.delete()) {
                System.out.println("Error deleting original file.");
                return;
            }
            if (!tempFile.renameTo(inputFile)) {
                System.out.println("Error renaming temp file.");
                return;
            }
 
            if (found) {
                System.out.println("Bug #" + bugIdToClose + " closed + Email sent ✅");
            } else {
                System.out.println("Bug not found or not assigned to you ❌");
            }
 
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
            tempFile.delete();
        }
    }
 
    
    public void updateBugStatus(String bugId, String newStatus) {
        File inputFile = new File(BASE_PATH + "bug.txt");
        File tempFile  = new File(BASE_PATH + "bug_temp.txt");
 
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
 
            String line;
            boolean found = false;
 
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
 
                String[] data = line.split(",", -1);
                if (data.length >= 10
                        && data[0].trim().equals(bugId)
                        && data[9].trim().equalsIgnoreCase(developerName)) {
 
                    data[7] = newStatus; // [7] = Status
                    found = true;
 
                    if (newStatus.equalsIgnoreCase("Closed")) {
                        String testerName = data[8].trim(); // [8] = Tester
                        EmailService.sendEmail(
                            developerName,
                            testerName,
                            "Bug Closed",
                            "Bug #" + bugId + " (" + data[1].trim()
                                + ") has been fixed by " + developerName
                        );
                    }
                    line = String.join(",", data);
                }
 
                writer.write(line);
                writer.newLine();
            }
 
            reader.close();
            writer.close();
 
            if (!inputFile.delete() || !tempFile.renameTo(inputFile)) {
                System.out.println("Failed to save changes.");
                return;
            }
            if (found) System.out.println("Bug #" + bugId + " → " + newStatus + " ✅");
            else       System.out.println("Bug not found ❌");
 
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
            tempFile.delete();
        }
    }
 
    
    public int    getDeveloperId()   { return developerId;   }
    public String getDeveloperName() { return developerName; }
}



    