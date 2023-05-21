import javax.swing.*;
import java.awt.event.*;

public class addTaskDialog extends JDialog {

    JLabel taskName, taskDescription;
    JTextField taskDialog;
    JTextArea descriptionDialog;
    JButton add, cancel;
    //String taskToAdd;
    public addTaskDialog(String name) {

        this.setTitle(name);
        this.setVisible(true);
        this.setElements();
        this.setLayout(null);
        this.setBounds(550,180,250,280); //setting size of window
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE); //closing of window


    }


    void setElements() {
        taskName = new JLabel("Add your task");
        taskDescription = new JLabel("Add description...");
        taskDialog = new JTextField("default task");
        descriptionDialog = new JTextArea();
        add = new JButton("Add");
        cancel = new JButton("Cancel");

        taskName.setBounds(8,5,200,22);
        taskDialog.setBounds(8,32, 200, 22);
        taskDescription.setBounds(8,60, 200, 22);
        descriptionDialog.setBounds(8, 90, 200, 100);
        descriptionDialog.setLineWrap(true);
        descriptionDialog.setWrapStyleWord(true);
        add.setBounds(8, 200, 80, 30);
        cancel.setBounds(98, 200, 80, 30);

        add(taskName);
        add(taskDialog);
        add(taskDescription);
        add(descriptionDialog);
        add(add);
        add(cancel);
        add.addActionListener(new addTaskButton());
        cancel.addActionListener(new cancelButton());
    }

    class cancelButton implements ActionListener {

        public void actionPerformed(ActionEvent e) {

            dispose();
        }
    }

    class addTaskButton implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            checklistGUI checklistTask = new checklistGUI();
            checklistTask.defaultList.addElement(taskDialog.getText());

            //add to database
            checklistDB database = new checklistDB(taskDialog.getText(), descriptionDialog.getText());
            try {
                database.addingATask(new checklistDB().addConnection());
            } catch (Exception ae) {
                ae.printStackTrace();
            }
        }
    }
}
