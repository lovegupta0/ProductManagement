import org.junit.jupiter.api.*;
import org.lg.Model.Service;
import org.lg.Model.Store;
import org.lg.Repository.StoreRepository;
import org.lg.common.Response;


import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class StoreRepositoryTest {

    private StoreRepository storeRepo;

    @BeforeEach
    public void setUp() {
        storeRepo = StoreRepository.getInstance();
    }

    @Test
    @Order(1)
    public void testAuthenticateSuccess() {
        Response<Object> response = storeRepo.Authenticate("lg1000", "password");
        assertEquals(200, response.getStatusCode(), "Authentication should return 200");
        assertNotNull(response.getData(), "Authenticated data should not be null");
        System.out.println("Auth Message: " + response.getMessage());
    }

    @Test
    @Order(2)
    public void testAuthenticateFail() {
        Response<Object> response = storeRepo.Authenticate("wronguser", "wrongpass");
        assertNotEquals(200, response.getStatusCode(), "Authentication should fail");
        assertNull(response.getData(), "Failed authentication should return null data");
    }

    @Test
    @Order(3)
    public void testGetUserAfterAuth() {
        storeRepo.Authenticate("lg1000", "password");
        Response<Store> response = storeRepo.getUser();
        assertEquals(200, response.getStatusCode());
        assertNotNull(response.getData());
        assertEquals("lg1000", response.getData().getUsername()); // Adjust field if needed
    }

    @Test
    @Order(4)
    public void testGetService() {
        storeRepo.Authenticate("lg1000", "password");
        Service service = storeRepo.getService();
        assertNotNull(service);
        assertNotNull(service.getPassKey()); // Assuming it has getPassKey
    }

    @Test
    @Order(5)
    public void testLogout() {
        storeRepo.Authenticate("lg1000", "password");
        storeRepo.logout();

        // After logout, user/service should no longer be accessible
        Response<Store> userResponse = storeRepo.getUser();
        assertNotEquals(200, userResponse.getStatusCode());
        assertNull(userResponse.getData());

        Service service = storeRepo.getService();
        assertNull(service);
    }
}
