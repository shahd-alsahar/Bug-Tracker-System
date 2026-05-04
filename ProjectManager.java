/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package bugtrackerprojectsh;
 
import java.io.*;
import java.util.*;
 
public class ProjectManager {
 
    private static final String BUG_FILE  = "bug.txt";
    private static final String USER_FILE = "users.txt";
 
   
 
   
    public static class BugRow {
        public final String id, title, assignedTo, status, priority;
        public BugRow(String id, String title, String assignedTo,
                      String status, String priority) {
            this.id         = id;
            this.title      = title;
            this.assignedTo = assignedTo;
            this.status     = status;
            this.priority   = priority;
        }
    }
 
   
    public static class BugStats {
        public int total, open, closed, inProgress;
    }
 
   
    public static class ReportData {
        public List<BugRow>                bugRows        = new ArrayList<>();
        public BugStats                    stats          = new BugStats();
        
        public LinkedHashMap<String, Integer> devPerf     = new LinkedHashMap<>();
        
        public LinkedHashMap<String, Integer> testerPerf  = new LinkedHashMap<>();
       
        public Map<String, int[]>            projectStats = new LinkedHashMap<>();
    }
 
   
    public ReportData getReportData() {
        ReportData report = new ReportData();
 
       
        Map<String, String> usersMap = loadUsersMap();
 
        
        try (BufferedReader br = new BufferedReader(new FileReader(BUG_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] d = line.split(",", -1);
                if (d.length < 10) continue;
 
                String id         = d[0].trim();
                String title      = d[1].trim();
                String priority   = d[3].trim();
                String project    = d[4].trim();
                String status     = d[7].trim();
                String testerName = d[8].trim();   // index 8 = tester
                String assignedTo = d[9].trim();   // index 9 = developer / assigned
 
                
                report.bugRows.add(new BugRow(id, title, assignedTo, status, priority));
 
                
                report.stats.total++;
                switch (status.toLowerCase()) {
                    case "open"        -> report.stats.open++;
                    case "closed"      -> {
                        report.stats.closed++;
                        
                        String devName = resolveUserName(assignedTo, usersMap);
                        report.devPerf.merge(devName, 1, Integer::sum);
                    }
                    case "in progress" -> report.stats.inProgress++;
                }
 
                
                String tName = resolveUserName(testerName, usersMap);
                if (!tName.isEmpty())
                    report.testerPerf.merge(tName, 1, Integer::sum);

                report.projectStats.putIfAbsent(project, new int[]{0, 0});
                int[] pc = report.projectStats.get(project);
                if (status.equalsIgnoreCase("open"))   pc[0]++;
                if (status.equalsIgnoreCase("closed")) pc[1]++;
            }
        } catch (FileNotFoundException e) {
            System.out.println("Bug file not found: " + BUG_FILE);
        } catch (IOException e) {
            System.out.println("Error reading bugs: " + e.getMessage());
        }
 
        
        report.devPerf    = sortDescending(report.devPerf);
        report.testerPerf = sortDescending(report.testerPerf);
 
        return report;
    }
 
   
    public void displayBugsReport() {
        ReportData r = getReportData();
 
        System.out.println("\n--- Project Bugs Report ---");
        for (Map.Entry<String, int[]> e : r.projectStats.entrySet()) {
            int[] c = e.getValue();
            System.out.println("Project: " + e.getKey()
                + " | Open Bugs: " + c[0]
                + " | Closed Bugs: " + c[1]);
        }
 
        System.out.println("\n--- Developer Performance ---");
        for (Map.Entry<String, Integer> e : r.devPerf.entrySet())
            System.out.println(e.getKey() + " closed " + e.getValue() + " bugs.");
 
        System.out.println("\n--- Tester Performance ---");
        for (Map.Entry<String, Integer> e : r.testerPerf.entrySet())
            System.out.println(e.getKey() + " found " + e.getValue() + " bugs.");
    }
 
    
    private Map<String, String> loadUsersMap() {
        Map<String, String> map = new HashMap<>();
        File f = new File(USER_FILE);
        if (!f.exists()) return map;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] u = line.split(",", -1);
                if (u.length >= 5) {
                    map.put(u[0].trim(), u[1].trim()); 
                    map.put(u[1].trim(), u[1].trim()); 
                } else if (u.length >= 2) {
                    map.put(u[0].trim(), u[0].trim());
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading users: " + e.getMessage());
        }
        return map;
    }
 
   
    private String resolveUserName(String raw, Map<String, String> usersMap) {
        if (raw == null || raw.isEmpty()) return "";
        return usersMap.getOrDefault(raw, raw);
    }
 
    
    private LinkedHashMap<String, Integer> sortDescending(Map<String, Integer> src) {
        LinkedHashMap<String, Integer> sorted = new LinkedHashMap<>();
        src.entrySet().stream()
            .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
            .forEach(e -> sorted.put(e.getKey(), e.getValue()));
        return sorted;
    }
}
