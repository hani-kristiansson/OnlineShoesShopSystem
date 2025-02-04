package repository;

import model.Item;
import model.Product;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class ProductRepository {

    Properties p = new Properties();

    public ProductRepository() throws IOException {
        p.load(new FileInputStream("src/Setting.properties"));
    }

    String queryToShowProductList = "SELECT id,name,brand,price FROM product";

    public List<Product> getProductList () {
        try (Connection con = DriverManager.getConnection(
                p.getProperty("connection"),
                p.getProperty("username"),
                p.getProperty("password"));

             PreparedStatement stmt = con.prepareStatement(queryToShowProductList)) {

            ResultSet rs = stmt.executeQuery();

            List<Product> productList = new ArrayList<>();

            while (rs.next()) {

                Product p = new Product();

                p.setId(rs.getInt("id"));
                p.setName(rs.getString("name"));
                p.setBrand(rs.getString("brand"));
                p.setPrice(rs.getInt("price"));

                productList.add(p);
            }
            return productList;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    String queryToShowItemList = "SELECT * FROM item where productId = ? and stock > 0";

    public List<Item> getItemDetailsByProductId (int productId) {
        try (Connection con = DriverManager.getConnection(
                p.getProperty("connection"),
                p.getProperty("username"),
                p.getProperty("password"));

             PreparedStatement stmt = con.prepareStatement(queryToShowItemList)) {

            stmt.setInt(1, productId);

            ResultSet rs = stmt.executeQuery();

            List<Item> itemList = new ArrayList<>();

            while (rs.next()) {

                Item i = new Item();

                i.setId(rs.getInt("id"));
                i.setProductId(rs.getInt("productId"));
                i.setColour(rs.getString("colour"));
                i.setSize(rs.getInt("size"));
                i.setStock(rs.getInt("stock"));

                itemList.add(i);
            }
            return itemList;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
