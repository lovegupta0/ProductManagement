import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Order;
import org.lg.Repository.*;
import org.lg.common.Response;
import org.lg.Model.*;

import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class OrderRepoTest {

    private static OrderRepo orderRepo;
    private static ProductRepo productRepo;
    private static ClientRepo clientRepo;
    private static String orderId;

    @BeforeAll
    public static void setup() {
        orderRepo = OrderRepository.getInstance();
        productRepo = ProductRepository.getInstance();
        clientRepo = ClientRepository.getInstance();
        StoreRepo storeRepo = StoreRepository.getInstance();
        storeRepo.Authenticate("lg1000", "password");
    }

    @Test
    @Order(1)
    public void testDoCheckOut() {
        // Prepare a client
        Client client = new Client("Checkout Tester", "jlswr","checkout@example.com", "9999999999",null);
        client = clientRepo.addClient(client).getData();

        // Prepare a product
        Product product = new Product("Banana", 25, 10, 100, 0, 0);
        product = productRepo.addProduct(product).getData();

        // Prepare order item and order
        OrderRecords item = new OrderRecords(product, 5,6,7,8,8); // Assume 5 qty
        List<OrderRecords> itemList = new ArrayList<>();
        itemList.add(item);

        org.lg.Model.Order order = new org.lg.Model.Order(client, itemList);
        Response<org.lg.Model.Order> response = orderRepo.doCheckOut(order);

        assertEquals(200, response.getStatusCode());
        assertNotNull(response.getData());

        orderId = response.getData().getUUID(); // Save for later
    }

    @Test
    @Order(2)
    public void testFindByOrderId() {
        Response<org.lg.Model.Order> response = orderRepo.findByOrderId(orderId);
        assertEquals(200, response.getStatusCode());
        assertNotNull(response.getData());
        assertEquals(orderId, response.getData().getUUID());
    }

    @Test
    @Order(3)
    public void testGetAllOrder() {
        Response<List<org.lg.Model.Order>> response = orderRepo.getAllOrder();
        assertEquals(200, response.getStatusCode());
        assertTrue(response.getData().size() > 0);
    }

    @Test
    @Order(4)
    public void testIsAbleToCheckOut() {
        Response<org.lg.Model.Order> response = orderRepo.findByOrderId(orderId);
        org.lg.Model.Order order = response.getData();
        Response<Boolean> checkoutResponse = orderRepo.isAbleToCheckOut(order);
        assertEquals(200, checkoutResponse.getStatusCode());
        assertTrue(checkoutResponse.getData()); // Assuming it is still valid
    }

    @Test
    @Order(5)
    public void testUpdateOrder() {
        Response<org.lg.Model.Order> response = orderRepo.findByOrderId(orderId);
        org.lg.Model.Order order = response.getData();
        order.getOrderList().get(0).setSoldQuantity(1);// assuming a status field
        Response<org.lg.Model.Order> updateResponse = orderRepo.updateOrder(order);
        assertEquals(200, updateResponse.getStatusCode());
        assertEquals(1f, order.getOrderList().get(0).getSoldQuantity());
    }

    @Test
    @Order(6)
    public void testDeleteOrder() {
        Response<Boolean> response = orderRepo.deleteOrder(orderId);
        assertEquals(200, response.getStatusCode());
        assertTrue(response.getData());

        // Now check if deleted
        Response<org.lg.Model.Order> findResponse = orderRepo.findByOrderId(orderId);
        assertNotEquals(200, findResponse.getStatusCode());
        assertNull(findResponse.getData());
    }
}
