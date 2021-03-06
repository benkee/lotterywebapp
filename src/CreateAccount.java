import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.*;
import java.util.ArrayList;

@WebServlet("/CreateAccount")
public class CreateAccount extends HttpServlet {
    // some class variables
    private Connection conn;
    private PreparedStatement stmt;
    private Statement statement;


    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        // MySql database connection info
        String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
        String USER = "user";
        String PASS = "password";

        // URLs to connect to database depending on your development approach
        // (NOTE: please change to option 1 when submitting)

        // 1. use this when running everything in Docker using docker-compose
        String DB_URL = "jdbc:mysql://db:3306/lottery";

        // 2. use this when running tomcat server locally on your machine and mysql database server in Docker
        //String DB_URL = "jdbc:mysql://localhost:33333/lottery";

        // 3. use this when running tomcat and mysql database servers on your machine
        //String DB_URL = "jdbc:mysql://localhost:3306/lottery";

        // get parameter data that was submitted in HTML form (use form attributes 'name')
        String firstname = request.getParameter("firstname");
        String lastname = request.getParameter("lastname");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String role = request.getParameter("role");
        session.setAttribute("role", role);
        // try to hash the inputted password
        try {
            HashPassword.hashPassword(password);
        } catch ( NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
        String hashedPassword = HashPassword.getHashedPassword();
        String salt = HashPassword.getSalt();
        // set the user details as session attributes
        session.setAttribute("firstname", firstname);
        session.setAttribute("lastname",lastname);
        session.setAttribute("username",username);
        session.setAttribute("email",email);
        session.setAttribute("hashedPassword",hashedPassword);
        session.setAttribute("salt",salt);

        try{
            // create database connection and statement
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL,USER,PASS);
            statement = conn.createStatement();
            // query the database
            ResultSet rs = statement.executeQuery("SELECT * FROM userAccounts");
            // create a list of usernames from the database
            ArrayList<String> Usernames = new ArrayList<String>();
            while (rs.next()) {
                Usernames.add(rs.getString("Username"));
            }

            // display account.jsp page with given message if successful and username not already in use
            boolean createAccount = true;
            for (String s : Usernames) {
                if (username.equals(s)) {
                    createAccount = false;
                    break;
                }
            }
            // if username is unique, create account by querying the database and inputting the the users details
            if (createAccount){
                // Create sql query
                String query = "INSERT INTO userAccounts (Firstname, Lastname, Email, Phone, Username, Pwd, Salt, Role)"
                        + " VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

                // set values into SQL query statement
                stmt = conn.prepareStatement(query);
                stmt.setString(1,firstname);
                stmt.setString(2,lastname);
                stmt.setString(3,email);
                stmt.setString(4,phone);
                stmt.setString(5,username);
                stmt.setString(6,hashedPassword);
                stmt.setString(7,salt);
                stmt.setString(8,role);
                // execute query and close connection
                stmt.execute();
                // close connection
                conn.close();
            if (role.equals("admin")){
                // forward user to admin page with given message
                RequestDispatcher dispatcher = request.getRequestDispatcher("/admin/admin_home.jsp");
                request.setAttribute("message", firstname + ", you have successfully created an account");
                dispatcher.forward(request, response);
            }else if(role.equals("public")){
                // forward user to public account page with given message and some user details to display
                RequestDispatcher dispatcher = request.getRequestDispatcher("/account.jsp");
                request.setAttribute("message", firstname + ", you have successfully created an account");
                request.setAttribute("firstname",session.getAttribute("firstname"));
                request.setAttribute("lastname",session.getAttribute("lastname"));
                request.setAttribute("username",session.getAttribute("username"));
                request.setAttribute("email",session.getAttribute("email"));
                dispatcher.forward(request, response);
            }
        }else{
                // close connection to database and throw user to error page with given message
                conn.close();
                RequestDispatcher dispatcher = request.getRequestDispatcher("error.jsp");
                request.setAttribute("message", "The username '" + username + "' is already in use.");
                dispatcher.forward(request, response);
            }

        } catch(Exception se){
            se.printStackTrace();
            // display error.jsp page with given message if unsuccessful
            RequestDispatcher dispatcher = request.getRequestDispatcher("/error.jsp");
            request.setAttribute("message", firstname+", this username/password combination already exists. Please try again");
            dispatcher.forward(request, response);
        }
        finally{
            // close any database statements or connections which are still open
            try{
                if(stmt!=null)
                    stmt.close();
            }
            catch(SQLException se2)
            {se2.printStackTrace();}
            try{
                if(conn!=null)
                    conn.close();
            }catch(SQLException se){
                se.printStackTrace();
            }
        }
    }
}
