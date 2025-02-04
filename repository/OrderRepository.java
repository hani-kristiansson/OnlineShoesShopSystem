package repository;

import model.CartItem;
import model.Customer;
import model.Item;
import model.Orders;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class OrderRepository {

    Properties p = new Properties();

    public OrderRepository() throws IOException {
        p.load(new FileInputStream("src/Setting.properties"));
    }

    String queryToFindActiveOrder = "SELECT * FROM Orders WHERE customerId = ? and status = 'active'";

    public Orders getActiveOrdersByCustomerId(int customerId) {
        try (Connection con = DriverManager.getConnection(
                p.getProperty("connection"),
                p.getProperty("username"),
                p.getProperty("password"));

             PreparedStatement stmt = con.prepareStatement(queryToFindActiveOrder)) {

            stmt.setInt(1, customerId);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {

                Orders o = new Orders();

                o.setId(rs.getInt("id"));
                o.setCustomerId(rs.getInt("customerId"));
                o.setOrderCreateDate(rs.getTimestamp("orderCreateDate").toLocalDateTime());
                o.setOrderUpdateDate(rs.getTimestamp("orderUpdateDate").toLocalDateTime());
                o.setStatus(rs.getString("status"));

                return o;
            }
            else {
                return null; //if there is no active order , return null
            }
        } catch(SQLException e){
            throw new RuntimeException(e);
        }
    }

    String queryToCallSP = "call addToCart(?,?,?)";

    //// TODO present SP
    public void callAddToCartSP(int customerId, Integer orderId, int itemId) {
        //set orderId as Integer so it can handle null. Integer is class (reference data type) so it can have null as value
        try (Connection con = DriverManager.getConnection(
                p.getProperty("connection"),
                p.getProperty("username"),
                p.getProperty("password"));

             CallableStatement cstmt = con.prepareCall(queryToCallSP)) {

            cstmt.setInt(1, customerId);

            if (orderId != null) {
                cstmt.setInt(2, orderId);
            }
            else {
                cstmt.setNull(2, Types.INTEGER);
            }

            cstmt.setInt(3, itemId);
            cstmt.executeQuery();

        } catch(SQLException e){
            throw new RuntimeException(e);
        }
    }

    String queryToShowCart =
            """
            SELECT product.name as productName, item.colour as colour, item.size as size, product.price as price, order_item.quantity as quantity 
            FROM Order_item 
                inner join item on item.id = order_item.itemId 
                inner join product on product.id = item.productId 
            where order_item.orderId = ? 
            """;

    public List<CartItem> getOrderItemInCart (int orderId) {
        try (Connection con = DriverManager.getConnection(
                p.getProperty("connection"),
                p.getProperty("username"),
                p.getProperty("password"));

             PreparedStatement stmt = con.prepareStatement(queryToShowCart)) {

            stmt.setInt(1, orderId);

            ResultSet rs = stmt.executeQuery();

            List<CartItem> cartItemList = new ArrayList<>();

            while (rs.next()) {

                CartItem ci = new CartItem();

                ci.setProductName(rs.getString("productName"));
                ci.setColour(rs.getString("colour"));
                ci.setSize(rs.getInt("size"));
                ci.setPrice(rs.getInt("price"));
                ci.setQuantity(rs.getInt("quantity"));

                cartItemList.add(ci);
            }
            return cartItemList;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    String queryToUpdateOrderStatus = "UPDATE Orders set status = 'paid' where id = ?";

    public void proceedPayment (int orderId) {
        try (Connection con = DriverManager.getConnection(
                p.getProperty("connection"),
                p.getProperty("username"),
                p.getProperty("password"));

             PreparedStatement stmt = con.prepareStatement(queryToUpdateOrderStatus)) {

            stmt.setInt(1, orderId);

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
