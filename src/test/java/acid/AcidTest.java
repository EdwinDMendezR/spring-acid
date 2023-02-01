package acid;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("acid")
@AutoConfigureMockMvc
public class AcidTest {

    private Connection connection;

    @Before
    public void setUp() {

        try {
            Connection connection = DriverManager.getConnection("jdbc:h2:mem:testdb");
            Statement statement = connection.createStatement();
            String sqlInventory = "CREATE TABLE INVENTORY (PRODUCT_ID INT PRIMARY KEY, QUANTITY INT)";
            String sqlBankAccount = "CREATE TABLE BANK_ACCOUNT (ACCOUNT_ID INT PRIMARY KEY, BALANCE DECIMAL)";

            String insert1 = "INSERT INTO INVENTORY (PRODUCT_ID, QUANTITY) VALUES (1, 10)";
            String insert2 = "INSERT INTO INVENTORY (PRODUCT_ID, QUANTITY) VALUES (2, 20)";

            String insert3 = "INSERT INTO BANK_ACCOUNT (ACCOUNT_ID, BALANCE) VALUES (1, 100.00)";
            String insert4 = "INSERT INTO BANK_ACCOUNT (ACCOUNT_ID, BALANCE) VALUES (2, 200.00)";

            statement.executeUpdate(sqlInventory);
            statement.executeUpdate(sqlBankAccount);

            statement.executeUpdate(insert1);
            statement.executeUpdate(insert2);
            statement.executeUpdate(insert3);
            statement.executeUpdate(insert4);

        } catch (Exception e) {
            Assert.fail("Error en el setUp............");
        }

    }


    @Test
    public void methodTest() {


        try (Connection connection = DriverManager.getConnection("jdbc:h2:mem:testdb")) {
            connection.setAutoCommit(false); // desactivamos el auto-commit para manejar la transacción manualmente
            try (Statement statement = connection.createStatement()) {
                // Ejecutamos una sentencia SQL que aumente la cantidad de un producto en el inventario
                statement.executeUpdate("UPDATE INVENTORY SET QUANTITY = QUANTITY + 1 WHERE PRODUCT_ID = 1");
                // Ejecutamos otra sentencia SQL que disminuya el saldo de una cuenta bancaria
                statement.executeUpdate("UPDATE BANK_ACCOUNT SET BALANCE = BALANCE - 10 WHERE ACCOUNT_ID = 1");
                connection.commit(); // realizamos el commit para guardar los cambios
                System.out.println("Transacción completada con éxito");
                ResultSet rss = statement.executeQuery("SELECT * FROM INVENTORY");
            } catch (SQLException e) {
                connection.rollback(); // si algo sale mal, realizamos el rollback para revertir los cambios
                System.out.println("Transacción fallida, los cambios se han revertido");

            }
        } catch (SQLException e) {
            System.out.println("Error al conectarse a la base de datos");
        }



    }

}
