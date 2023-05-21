import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class checklistGUI extends JFrame {
    static JList<String> myList;
    static DefaultListModel<String> defaultList;
    JButton addTask, remove;
    static JLabel statusLabel;
    String status = "List status is displayed here";
    static JTextField task;
    JButton plus;
    JPanel taskManipulation; //panel for task manipulation
    ListModel model;
    checklistDB db;


    public checklistGUI(String name) throws Exception {
        super(name);
        this.setVisible(true); //opening and showing of window
        this.setComponents();
        this.setLayout(null);
        this.setBounds(0, 0, 300, 400); //setting size of window
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //closing of window
   }

    checklistGUI() {
    }

    void setComponents() throws Exception {

        //Creating a default list
        defaultList = new DefaultListModel<String>();

        //store from database to list
        Connection con = new checklistDB().addConnection();
        Statement stmt = con.createStatement();

        ResultSet rs = stmt.executeQuery("SELECT * FROM checklist");
        while (rs.next()) {
            defaultList.addElement(rs.getString("title"));
        }

        rs.close();
        stmt.close();
        con.close();

        myList = new JList<String>(defaultList);
        myList.setSelectionBackground(Color.LIGHT_GRAY);
        myList.setBounds(8, 35, 200, 200);

        //for deselection of selected item
        myList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Do nothing when clicking inside the list
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {
                // Do nothing when releasing inside the list
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                // Deselect the selected item when mouse exits the list
                if (myList.getSelectedIndex() != -1) {
                    myList.clearSelection();
                }
            }
        });

        //Creating task adding panel
        task = new JTextField("Quick add");
        plus = new JButton("+");
        plus.setBackground(new Color(90, 170, 250)); // blue background
        plus.setForeground(Color.WHITE); // white text
        plus.setBounds(168, 5, 45, 20);
        task.setBounds(8, 5, 150, 20);

        //Task handling panel
        taskManipulation = new JPanel(null);
        taskManipulation.setOpaque(true);
        taskManipulation.setBounds(8, 250, 200, 80);

            //Adding buttons
            addTask = new JButton("Create");
            addTask.setBounds(0, 0, 90, 30);
            addTask.setBackground(new Color(90, 170, 250)); // blue background
            addTask.setForeground(Color.WHITE); // white text
            addTask.addMouseListener(new addButton());
            taskManipulation.add(addTask);

            remove = new JButton("Remove");
            remove.setBounds(100, 0, 90, 30);
            remove.setBackground(new Color(250, 90, 90)); // red background
            remove.setForeground(Color.WHITE); // white text
            remove.addMouseListener(new removeButton());
            taskManipulation.add(remove);

            //Adding label
            statusLabel = new JLabel(status);
            statusLabel.setBounds(0, 30, 200, 30);
            statusLabel.setForeground(Color.GRAY);
        taskManipulation.add(statusLabel);
        add(taskManipulation);

        add(task);
        add(plus);
        plus.addMouseListener(new plusButton());


        myList.addListSelectionListener(new openNote());

        add(myList); //this was the headache error
    }

//Actions

    class openNote implements ListSelectionListener {
        public void valueChanged(ListSelectionEvent e) {
            if (!e.getValueIsAdjusting()) {
                Object selected = myList.getSelectedValue();

                if (selected != null) { // Check if selected is null
                    String title = selected.toString();

                    db = new checklistDB();
                    db.title = title;

                    try {
                        Connection conn = db.addConnection();
                        // create a SQL statement to select the description from the database
                        String sql = "SELECT description FROM checklist WHERE title = ?";
                        PreparedStatement pstmt = conn.prepareStatement(sql);
                        pstmt.setString(1, title);

                        // execute the query and get the result set
                        ResultSet rs = pstmt.executeQuery();
                        if (rs.next()) {
                            // get the description from the result set
                            String desc = rs.getString("description");
                            // create a new Note object with the title and description
                            Note note= new Note(title, desc);
                            
                        }
                        pstmt.close();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
    }



    //plus button actions
    class plusButton implements MouseListener {
        public void mouseClicked(MouseEvent pBe) {
            String taskTitle = task.getText();
            String taskDesc = "";
            if (!taskTitle.equals("Quick add")) {
                // Show a dialog box to get the task description from the user
                taskDesc = JOptionPane.showInputDialog("Enter task description:");

                if (taskDesc == null) {
                    // User cancelled the input dialog, do nothing
                    return;
                }
            }

            // Add the task to the list and database
            defaultList.addElement(taskTitle);
            checklistDB database = new checklistDB(taskTitle, taskDesc);
            try {
                database.addingATask(new checklistDB().addConnection());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            statusLabel.setText("A task added");
            if (!taskTitle.equals("Quick add")) {
                task.setText("");
            }
        }
        public void mousePressed(MouseEvent e) {
        }
        public void mouseReleased(MouseEvent e) {
        }
        public void mouseEntered(MouseEvent e) {
        }

        public void mouseExited(MouseEvent e) {
            statusLabel.setText(status);
        }
    }

    //remove button actions
    class removeButton implements MouseListener {

        public void mouseClicked(MouseEvent e) {
            model = myList.getModel();
            if (model.getSize() != 0) {
                Object item = model.getElementAt(model.getSize() - 1);

                //Minor glitches (deleting the selected note only; not the last one)
//                if (!myList.isSelectionEmpty()) {
//                    item = myList.getSelectedValue();
//                }

                defaultList.removeElement(item);
                statusLabel.setText("Task removed");
            }

            //removing from database
            checklistDB database = new checklistDB(task.getText());
            try {
                database.removingATask(new checklistDB().addConnection());
            } catch (Exception ae) {
                throw new RuntimeException(ae);
            }
            statusLabel.setText("A task added");
            if (!task.getText().equals("Quick add")) {
                task.setText("");
            }
        }
        public void mousePressed(MouseEvent e) {
        }
        public void mouseReleased(MouseEvent e) {
        }
        public void mouseEntered(MouseEvent e) {
        }
        public void mouseExited(MouseEvent e) {
            statusLabel.setText(status);
        }

    }

    class addButton implements MouseListener {
        public void mouseClicked(MouseEvent e) {
            new addTaskDialog("Add a task");
            statusLabel.setText("Task adding dialog box opened");
        }
        public void mousePressed(MouseEvent e) {

        }
        public void mouseReleased(MouseEvent e) {

        }
        public void mouseEntered(MouseEvent e) {

        }

        public void mouseExited(MouseEvent e) {
            statusLabel.setText(status);
        }
    }

    public static void main(String[] args) throws Exception {
        new checklistGUI("Notes App");
    }
}