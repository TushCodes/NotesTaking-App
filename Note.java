import javax.swing.*;
import java.awt.event.*;
import java.sql.*;


public class Note extends JDialog {

    String title="";
    static JTextField taskDialog;
    static JTextArea descriptionDialog;
    JButton update, cancel;
    AutoSaveThread autoSaveThread;

    public Note() {}
    public Note(String title, String description) {
        // Call the JDialog constructor with a null parent to create a new dialog
        super((JDialog) null, true);
        this.setTitle(title);
        this.setElements(title, description);
        this.setLayout(null);
        this.setBounds(550, 180, 250, 280);

        autoSaveThread = new AutoSaveThread(new checklistDB(),descriptionDialog, title, description);
        AutoSaveThread.running=true;
        autoSaveThread.start();
        this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        // Call setVisible after the dialog is fully constructed to make it appear

        this.setVisible(true);

    }

    void setElements(String title, String description) {
        taskDialog = new JTextField(title);
        descriptionDialog = new JTextArea(description);
        update = new JButton("Update");
        cancel = new JButton("Cancel");
        this.title = taskDialog.getText();

        taskDialog.setBounds(8,10, 200, 42);
        descriptionDialog.setBounds(8, 60, 200, 130);
        descriptionDialog.setLineWrap(true);
        descriptionDialog.setWrapStyleWord(true);
        update.setBounds(8, 200, 80, 30);
        cancel.setBounds(98, 200, 80, 30);

        add(taskDialog);
        add(descriptionDialog);
        add(update);
        add(cancel);
        update.addActionListener(new updateButton());
        cancel.addActionListener(new cancelButton());
    }

    class cancelButton implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
//            autoSaveThread.interrupt();

            AutoSaveThread.running=false;
            dispose();
        }
    }

    class updateButton implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            //get the updated title and description from the text fields
            String updatedTitle = taskDialog.getText();
            String updatedDescription = descriptionDialog.getText();

            //update the task in the database using its title to identify it
            try {
                checklistDB database = new checklistDB(updatedTitle, updatedDescription);
                Connection con = database.addConnection();
                Statement stmt = con.createStatement();
                System.out.println(updatedDescription);
                //use an UPDATE statement to update the row associated with the task being edited
                String sqlQuery = "UPDATE checklist SET description = '" + updatedDescription + "' WHERE title = '" + title + "';";
                int rowsUpdated = stmt.executeUpdate(sqlQuery);

                if (rowsUpdated > 0) {
                    System.out.println("Task with title '" + title + "' has been updated successfully");
                    title = updatedTitle; // update the title field of the Note object
                } else {
                    System.out.println("No task with title '" + title + "' found in the database");
                }

                //close the database connection and dispose of the Note dialog
                stmt.close();
                con.close();
                AutoSaveThread.running=false;
                dispose();
            } catch (Exception ae) {
                ae.printStackTrace();
            }
        }
    }


    public static void main(String[] args) {
        // Create a new Note dialog with title and description, and make it visible
        Note note = new Note("Title", "Description");
    }

}
