package hieunnm.utils;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBAcess {

    public static Connection getConnection() throws Exception {

        Connection conn = null;
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        String url = "jdbc:sqlserver://HIEUCAN\\MINHHIEU:1433;databaseName=ArmorDB";
        conn = DriverManager.getConnection(url, "sa", "123456");
        return conn;

    }
}
