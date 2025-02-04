package repository;

import model.Customer;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;

public class CustomerRepository {

    Properties p = new Properties();

    public CustomerRepository() throws IOException {
        p.load(new FileInputStream("src/Setting.properties"));
    }

    String queryToFindCustomer = "SELECT * FROM customer WHERE userName = ? and password = ?";

    public Customer getCustomerByUserNameAndPassword(String userName, String password) {
        try (Connection con = DriverManager.getConnection(
                p.getProperty("connection"),
                p.getProperty("username"),
                p.getProperty("password"));

             PreparedStatement stmt = con.prepareStatement(queryToFindCustomer)) {

            stmt.setString(1, userName);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {

                Customer c = new Customer();

                c.setId(rs.getInt("id"));
                c.setFirstName(rs.getString("firstName"));
                c.setLastName(rs.getString("lastName"));
                c.setUserName(rs.getString("userName"));
                c.setPassword(rs.getString("password"));
                c.setAddress(rs.getString("address"));
                c.setDistrict(rs.getString("district"));

                return c;
            }

            else {
                return null; //if there is no matching username or password
            }

        } catch(SQLException e){
            throw new RuntimeException(e);
        }
    }

}
