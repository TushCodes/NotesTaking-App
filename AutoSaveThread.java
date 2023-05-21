import javax.swing.*;
import java.sql.*;

public class AutoSaveThread extends Thread {
    public static volatile boolean running=true;
    private checklistDB database;
    JTextArea descDialog;
    private String title;
    private String description;

    public AutoSaveThread(checklistDB database, JTextArea descDialog, String title, String description) {
        this.database = database;
        this.title = title;
        this.description = description;
        this.descDialog=descDialog;
    }

    @Override
    public void run() {
        while (running) {
            try {
                Thread.sleep(60000); // Wait for 1 minute
                saveChanges();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveChanges() {
        // Update the task with the given title in the database
        try {
            Connection con = database.addConnection();
            Statement stmt = con.createStatement();
            String updatedDescription=descDialog.getText();

            // Use an UPDATE statement to update the row associated with the task being edited
            String sqlQuery = "UPDATE checklist SET description = '" + updatedDescription + "' WHERE title = '" + title + "';";
            int rowsUpdated = stmt.executeUpdate(sqlQuery);

            if (rowsUpdated > 0) {
                System.out.println("saved");
//                System.out.println("Task with title '" + title + "' has been updated successfully");
            } else {
                System.out.println("error saving");
//                System.out.println("No task with title '" + title + "' found in the database");
            }

            // Close the database connection
            stmt.close();
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
