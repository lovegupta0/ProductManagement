import org.junit.jupiter.api.*;
import org.lg.Model.Product;
import org.lg.Repository.ProductRepo;
import org.lg.Repository.ProductRepository;
import org.lg.Repository.StoreRepo;
import org.lg.Repository.StoreRepository;
import org.lg.common.Response;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProductRepoTest {

    private ProductRepo productRepo;
    private Product testProduct;


    @BeforeEach
    public void setUp() {
        // Mock the ProductRepo interface
        productRepo = ProductRepository.getInstance();
        testProduct = new Product("cabbage", 55, 10, 20, 0, 1);
        StoreRepo storeRepo = StoreRepository.getInstance();
        storeRepo.Authenticate("lg1000", "password");
    }

    @Test
    @Order(1)
    public void testAddProduct() {
        Response<Product> response = productRepo.addProduct(testProduct);
        assertEquals(200, response.getStatusCode(), "Product should be added successfully");
        assertEquals("OK", response.getMessage());
        assertNotNull(response.getData(), "Returned product should not be null");
        testProduct = response.getData();  // Save UUID for future tests

    }

    @Test
    @Order(2)
    public void testFindProductById() {
        System.out.println(testProduct);
        Response<Product> response = productRepo.findProductById(getUUID());
        assertEquals(200, response.getStatusCode());
        assertNotNull(response.getData());
        assertEquals("cabbage", response.getData().getName());
    }

    @Test
    @Order(3)
    public void testGetAllProducts() {
        Response<List<Product>> response = productRepo.getAllProducts();
        assertEquals(200, response.getStatusCode());
        assertTrue(response.getData().size() > 0, "Product list should not be empty");
    }

    @Test
    @Order(4)
    public void testUpdateProduct() {
        testProduct.setDiscount(1.5f);
        testProduct.setUUID(getUUID());
        Response<Product> response = productRepo.updateProduct(testProduct);
        assertEquals(200, response.getStatusCode());
        assertEquals(1.5f, response.getData().getDiscount());
    }

    @Test
    @Order(5)
    public void testUpdateProductInventory() {
        Response<Boolean> response = productRepo.updateProductInventory(getUUID(), 5f);
        assertEquals(200, response.getStatusCode());
        assertTrue(response.getData());
    }

    @Test
    @Order(6)
    public void testUpdateProducts() {
        testProduct.setUUID(getUUID());
        List<Product> products = List.of(testProduct);
        Response<Object> response = productRepo.updateProducts(products);
        assertEquals(200, response.getStatusCode());
    }

    @Test
    @Order(7)
    public void testDeleteProduct() {
        String s=getUUID();
        Response<Boolean> response = productRepo.deleteProduct(s);
        assertEquals(200, response.getStatusCode());
        assertTrue(response.getData());

        Response<Product> checkDeleted = productRepo.findProductById(s);
        System.out.println(checkDeleted.getMessage());
        System.out.println(checkDeleted.getStatusCode());
        assertNotEquals(200, checkDeleted.getStatusCode(), "Product should not be found after deletion");
        assertNull(checkDeleted.getData());
    }
    private String getUUID(){
        Response<List<Product>> lst = productRepo.getAllProducts();

        return lst.getData().stream().filter(e->e.getName().equals("cabbage")).map(e->e.getUUID()).findAny().get();
    }
}
