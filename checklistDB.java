import java.sql.*;
import java.util.ArrayList;

public class checklistDB {
    int id;
    static String title;
    static String description;


//Constructor
    public checklistDB() {}

    public checklistDB(int id, String title, String description) {
        this.id = id;
        this.title = title;
        this.description = description;
    }

    public checklistDB(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public checklistDB(String title) {
        this.title = title;
    }


//Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }



//Method to establish connection
    static Connection addConnection() throws Exception {
        String driver = "com.mysql.cj.jdbc.Driver";
        String url = "jdbc:mysql://localhost:3306/projects";
        String uname = "root";
        String password = "";

        //Loading the driver
        Class.forName(driver);

        //Establishing the connection
        Connection con = DriverManager.getConnection(url, uname, password);

        return con;
    }

//Adding task to database
    void addingATask(Connection con) throws Exception {
        String query = "insert into checklist values(?,?,?);";
        String idQuery = "SELECT * FROM checklist ORDER BY id DESC LIMIT 1;";

        //Creating a statement
        PreparedStatement addValues = con.prepareStatement(query);
        PreparedStatement fetchId = con.prepareStatement(idQuery);

        //Execute the statements
        ResultSet rs = fetchId.executeQuery();
        int id = 0;

        if(rs.next()) {
            id = rs.getInt("id");
        }
        setId(++id);
        addValues.setInt(1, getId());
        addValues.setString(2, getTitle());
        addValues.setString(3, getDescription());

        System.out.println(addValues.executeUpdate() + " rows affected.");
    }

//Removing task to database
    void removingATask(Connection conn) throws Exception {
        String removeQuery = "DELETE FROM checklist WHERE id = ?;";
        String idQuery = "SELECT id FROM checklist ORDER BY id DESC LIMIT 1;";

        // Creating a statement
        PreparedStatement addValues = conn.prepareStatement(removeQuery);
        PreparedStatement fetchId = conn.prepareStatement(idQuery);

        // Execute the idQuery and retrieve the last inserted id
        ResultSet rs = fetchId.executeQuery();
        int lastId = -1; // initialize lastId to -1
        if (rs.next()) {
            lastId = rs.getInt("id");
        }
        rs.close();

        // Set the id parameter of the removeQuery to the last inserted id
        setId(lastId);
        addValues.setInt(1, getId());

        // Execute the removeQuery and print the number of rows affected
        int rowsAffected = addValues.executeUpdate();
        System.out.println(rowsAffected + " row(s) affected.");
    }

//To add tasks into arraylist
    static ArrayList<checklistDB> readAllTasks(Connection con) throws Exception {
        String query = "SELECT * FROM checklist;";
        ArrayList<checklistDB> tasks = new ArrayList<checklistDB>();

        //Creating a statement
        Statement stmt = con.createStatement();

        //Execute the statement
        ResultSet rs = stmt.executeQuery(query);

        //Add to titles array list
        while (rs.next()) {
            int id = rs.getInt("id"); // retrieve the id column from the result set
            String title = rs.getString(2);
            String description = rs.getString(3);

            // create a new checklistDB object with the retrieved values and add it to the array list
            checklistDB task = new checklistDB(title, description);
            task.id = id; // assign the retrieved id to the object's id instance variable
            tasks.add(task);

        }

        rs.close();
        stmt.close();
        con.close();

        return tasks;
    }


    public static void main(String[] args) throws Exception {
        checklistDB database = new checklistDB(title, description);
        Connection paramCon = database.addConnection();
        database.addingATask(paramCon);
        database.removingATask(paramCon);
        database.readAllTasks(paramCon);
    }
}
