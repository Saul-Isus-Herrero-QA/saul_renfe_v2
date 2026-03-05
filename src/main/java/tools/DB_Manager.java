package tools;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;

public class DB_Manager {

    public static void main(String[] args) {
        // Data connection
        String url = "jdbc:sqlserver://localhost:1433;"
                + "database=WONKA_BACKOFFICE_TEST_18547;"
                + "encrypt=false;"
                + "trustServerCertificate=true;";
        String user = "sa";
        String password = "Password_01";

        // Query
        String query = "SELECT * FROM dbo.Taxes_TaxGroup "
                + "WHERE Name = 'Saul Test 03MARZO2026';";

        try (Connection con = DriverManager.getConnection(url, user, password);
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(query)) {

            System.out.println("Conexión establecida correctamente.");
            System.out.println("Ejecutando consulta:");
            System.out.println(query);
            System.out.println("Resultados:\n");

            // Displays query results
            int columnCount = rs.getMetaData().getColumnCount();

            // Print column names
            for (int i = 1; i <= columnCount; i++) {
                System.out.print(rs.getMetaData().getColumnName(i));
                if (i < columnCount) {
                    System.out.print(" | ");
                }
            }
            System.out.println();
            System.out.println("--------------------------------------------------");

            // Print row names
            boolean hasRows = false;
            while (rs.next()) {
                hasRows = true;
                for (int i = 1; i <= columnCount; i++) {
                    Object value = rs.getObject(i);
                    System.out.print(value);
                    if (i < columnCount) {
                        System.out.print(" | ");
                    }
                }
                System.out.println();
            }

            if (!hasRows) {
                System.out.println("The query doesn´t return any result");
            }

        } catch (SQLException e) {
            System.err.println("Connection or Query execution error:");
            e.printStackTrace();
        }
    }
}
