package db;

import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class MySQLTest {
    @Test
    public void testConnection() throws Exception {
        Class.forName("com.mysql.jdbc.Driver");

        try(Connection conn = DriverManager.getConnection("jdbc:mysql://mercu.ipdisk.co.kr:13306/mercu", "", "")) {
            System.out.println(conn);

            Statement st = conn.createStatement();

            ResultSet rs = st.executeQuery("show tables");

            while (rs.next()) {
                System.out.println(rs.toString());
            }

        }
    }
}
