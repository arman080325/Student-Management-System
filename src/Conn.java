package src;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class Conn {

    Connection connection;
    Statement statement;

    Conn() {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            connection = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "system", "arman0803");
            statement =connection.createStatement();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}