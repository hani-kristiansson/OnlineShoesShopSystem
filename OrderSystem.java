import model.*;
import org.w3c.dom.ls.LSOutput;
import repository.CustomerRepository;
import repository.OrderRepository;
import repository.ProductRepository;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class OrderSystem {

    CustomerRepository customerRepository = new CustomerRepository();
    OrderRepository orderRepository = new OrderRepository();
    ProductRepository productRepository = new ProductRepository();
    Customer user; //user who logged in
    Orders activeOrder; //active order

    Scanner scanner = new Scanner(System.in);

    OrderSystem() throws IOException {

        System.out.println("Welcome to online shoes shop.");

        while(true){
            System.out.println("Please enter your user name to log in.");

            String userName = scanner.nextLine();
            System.out.println("Welcome " + userName + ". Please enter your password");
            String password = scanner.nextLine();

            user = customerRepository.getCustomerByUserNameAndPassword(userName, password);

            if (user != null) {

                System.out.println("Welcome " + user.getFirstName() + " " + user.getLastName() + ". You are logged in.");

                while (true) {
                    activeOrder = orderRepository.getActiveOrdersByCustomerId(user.getId());

                    System.out.println("Please choose one of the following options:");
                    System.out.println("1. Show all products, 2. Show cart, 3. Proceed payment, 4. Exit");
                    String choice = scanner.nextLine();

                    switch (choice) {
                        case "1":
                            showAllProductsList();
                            break;

                        case "2":
                            showCartItemList();
                            break;

                        case "3":
                            paymentHandler();
                            break;

                        case "4":
                            System.out.println("Have a good day!");
                            System.exit(0);
                            break;

                        default:
                            System.out.println("Invalid choice");
                            break;
                    }
                }
            } else {
                System.out.println("Check your username or password again");
            }
        }
    }

    public void showAllProductsList(){
        List<Product> productList = productRepository.getProductList();

        String formatForProduct = "%-15s %-15s %-10s%n";

        System.out.printf(formatForProduct, "Product Name", "Brand", "Price");
        System.out.println("------------------------------------------------------");

        for (Product product : productList) {
            System.out.printf(formatForProduct, product.getName(), product.getBrand(), product.getPrice());
        }
        System.out.println();

        while (true) {
            System.out.println("Please choose one of the following products or type '1' to see previous menu:\n");
            String productInput = scanner.nextLine();

            if (productInput.equals("1")) {
                return;
            }
            for (Product product : productList) {
                if (productInput.equalsIgnoreCase(product.getName())) {
                    showAllItemsList(product.getId());
                    //customer types product name, and it converts to ID then it shows all items in stock
                    return;
                }
            }
            System.out.println("Product you typed: " + productInput + "doesn't exist.");
        }
    }

    public void showAllItemsList(int productId){
        List<Item> itemList = productRepository.getItemDetailsByProductId(productId);

        String formatForItem = "%-3d %-10s %-2d%n";
        String formatForItemHeader = "%-3s %-10s %-4s%n";

        System.out.printf(formatForItemHeader, "No.", "Colour", "Size");
        System.out.println("--------------------------------------");

        //to print out all items in the list
        for (int i = 0; i < itemList.size(); i++) {
            Item item = itemList.get(i);
            System.out.printf(formatForItem, i+1, item.getColour(), item.getSize());
        }

        while (true){
            System.out.println();
            String message =
                    """
                    Please choose one of the following items colour and size (ex: 'black 40') 
                    \n or type '1' to go back to previous menu:
                    """;

            System.out.println(message);
            String[] itemInput = scanner.nextLine().trim().split(" ");

            if (itemInput[0].equals("1")) {
                return;
            }

            String colour = itemInput[0];
            int size = Integer.parseInt(itemInput[1]);

            //to see if typed item is matching with DB
            for (Item item : itemList) {
                if (item.getColour().equalsIgnoreCase(colour) && item.getSize() == size) {
                    System.out.println("Item " + item.getColour() + " " + item.getSize() + " has been added to the list.");

                    try {
                        if (activeOrder != null) {
                            orderRepository.callAddToCartSP(user.getId(), activeOrder.getId(), item.getId());
                        }
                        else {
                            orderRepository.callAddToCartSP(user.getId(), null, item.getId());
                            //if there is no active order no., use null as value, then SP will create new order id
                        }
                        return;
                    }
                    catch (Exception e) {
                        System.out.println("Something went wrong. Please try again");
                        System.err.println(e.getMessage());
                        break;
                    }
                }
            }
            System.out.println("Product you typed: " + colour + " " + size + "doesn't exist.");
        }
    }

    public void showCartItemList(){
        if (activeOrder == null) {
            System.out.println("The cart is empty.");
            return;
        }

        List<CartItem> cartItemList = orderRepository.getOrderItemInCart(activeOrder.getId());

        String formatCartItemHeader = "%-15s %-10s %-4s %-5s %-10s%n";
        System.out.printf(formatCartItemHeader, "Product name", "colour", "size", "price", "quantity");

        String formatCartItem = "%-15s %-10s %-4d %-5d %-10d%n";
        System.out.println("--------------------------------------------------");

        for (CartItem cartItem : cartItemList) {
            System.out.printf(formatCartItem, cartItem.getProductName(), cartItem.getColour(),
                    cartItem.getSize(), cartItem.getPrice(), cartItem.getQuantity());
        }
    }

    public void paymentHandler(){
        if (activeOrder == null) {
            System.out.println("The cart is empty so unable to proceed payment.");
            return;
        }

        System.out.println("Please find items in your cart before making payment:");
        showCartItemList();

        while (true) {
            System.out.println("Type '1' to proceed payment, '2' to go to previous menu");
            String userInput = scanner.nextLine();

            if (userInput.equals("1")) {
                orderRepository.proceedPayment(activeOrder.getId());
                System.out.println("Thank you for your order! Have a nice day!");
                return;
            }
            else if (userInput.equals("2")) {
                return;
            }
            else {
                System.out.println("wrong option, please try again");
            }
        }
    }
}
