package org.example.chap05.exec;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.Properties;
import java.util.Scanner;

public class ExecSQL {
    public static void main(String[] args) throws IOException {
        try (Scanner in = args.length == 0 ? new Scanner(System.in) : new Scanner(Paths.get(args[0]), StandardCharsets.UTF_8)) {
            try (Connection conn = getConnection();
                 Statement stat = conn.createStatement()) {
                while (true) {
                    if (args.length == 0) {
                        System.out.println("Enter command or EXIT to exit:");
                    }
                    if (!in.hasNextLine()) {
                        return;
                    }
                    String line = in.nextLine().trim();
                    if (line.equalsIgnoreCase("EXIT")) {
                        return;
                    }
                    if (line.endsWith(";")) {
                        line = line.substring(0,line.length() - 1);
                    }

                    try {
                        boolean isResult = stat.execute(line);
                        if (isResult) {
                            try (ResultSet rs = stat.getResultSet()) {
                                showResultSet(rs);
                            }
                        } else {
                            int update = stat.getUpdateCount();
                            System.out.println(update + " rows updates");
                        }
                    } catch (SQLException e) {
                        for (Throwable t : e) {
                            t.printStackTrace();
                        }
                    }
                }
            }
        } catch (SQLException e) {
            for (Throwable t : e) {
                t.printStackTrace();
            }
        }
    }

    public static Connection getConnection() throws SQLException, IOException {
        Properties props = new Properties();
        try (InputStream in = Files.newInputStream(Paths.get("src/main/java/org/example/chap05/database.properties"))) {
            props.load(in);
        }
        String driver = props.getProperty("jdbc.url");
        if (driver != null) {
            System.setProperty("jdbc.drivers",driver);
        }
        String url = props.getProperty("jdbc,url");
        String username = props.getProperty("jdbc.username");
        String password = props.getProperty("jdbc.password");

        return DriverManager.getConnection(url,username,password);
    }

    public static void showResultSet(ResultSet resultSet) throws SQLException {
        ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();

        for (int i = 1; i <= columnCount; i++) {
            if (i > 1) {
                System.out.print(", ");
                System.out.print(metaData.getColumnLabel(i));
            }
        }
        System.out.println();

        while (resultSet.next()) {
            for (int i = 1; i <= columnCount; i++) {
                if (i > 1) {
                    System.out.print(", ");
                }
                System.out.print(resultSet.getString(i));
            }
            System.out.println();
        }
    }
}
