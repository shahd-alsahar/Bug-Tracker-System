//package bugtrackerprojectsh;
//package bugtrackerprojectsh.GUI;
//import bugtrackerprojectsh.GUI.LoginFrame;
//import java.io.File;
//import java.util.Scanner;

package bugtrackerprojectsh;

import bugtrackerprojectsh.GUI.LoginFrame; 

public class BugTrackerProjectSH {
    public static void main(String[] args) {
        new LoginFrame(); 
    }
}



        /*String role = login.login("shahd@gmail.com", "1234");
        if (role != null) {
            System.out.println("Login successful! Role: " + role);
        } else {
            System.out.println("Invalid email or password.");
            return; // لو login فشل، نخرج من البرنامج
        }

        // بعد Login
        if (role.equalsIgnoreCase("tester")) {
            Tester tester = new Tester(1, "Sara"); // id واسم الـ tester من users.txt
            tester.displayOpenBugs();

            Scanner sc = new Scanner(System.in);
            System.out.print("\nDo you want to add a new bug? (yes/no): ");
            String choice = sc.nextLine();

            if (choice.equalsIgnoreCase("yes")) {
                tester.addBug();
            }
        }
        if (role.equalsIgnoreCase("developer")) {
    Developer dev = new Developer(2, "Ali"); // id واسم الـ developer من users.txt
    dev.displayAssignedBugs();

    Scanner sc = new Scanner(System.in);
    System.out.print("\nEnter Bug ID to mark as closed: ");
    String bugId = sc.nextLine();

    dev.markBugAsClosed(bugId);

    System.out.println("\nUpdated Assigned Bugs:");
    dev.displayAssignedBugs();
}
        if (role.equalsIgnoreCase("pm")) {
    ProjectManager pm = new ProjectManager();
    pm.displayBugsReport();
}
        if (role.equalsIgnoreCase("admin")) {
    Admin admin = new Admin();

    admin.viewAllBugs();

    Scanner sc = new Scanner(System.in);

    System.out.print("1- Add User\n2- Delete User\n3- Update User\nChoose: ");
    int choice = sc.nextInt();
    sc.nextLine(); // important

    if (choice == 1) {
        admin.addUser();
    } else if (choice == 2) {
        System.out.print("Enter User ID to delete: ");
        String id = sc.nextLine();
        admin.deleteUser(id);
    } else if (choice == 3) {
        System.out.print("Enter User ID to update: ");
        String id = sc.nextLine();
        admin.updateUser(id);
    }
}



    }
}*/
