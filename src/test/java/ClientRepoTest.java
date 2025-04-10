import org.junit.jupiter.api.*;
import org.lg.Model.Client;
import org.lg.Repository.ClientRepo;
import org.lg.Repository.ClientRepository;
import org.lg.Repository.StoreRepo;
import org.lg.Repository.StoreRepository;
import org.lg.common.Response;

import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ClientRepoTest {

    private static ClientRepo clientRepo;
    private static String clientId;

    @BeforeAll
    public static void setup() {
        clientRepo = ClientRepository.getInstance();
        StoreRepo storeRepo = StoreRepository.getInstance();
        storeRepo.Authenticate("lg1000", "password");
    }

    @Test
    @Order(1)
    public void testAddClient() {
        Client client = new Client("John Doe","jlswr" ,"john@example.com", "1234567890",null);
        Response<Client> response = clientRepo.addClient(client);
        assertEquals(200, response.getStatusCode());
        assertNotNull(response.getData());
        clientId = response.getData().getUUID(); // save for later use
    }

    @Test
    @Order(2)
    public void testGetAllClients() {
        Response<List<Client>> response = clientRepo.getAllClient();
        assertEquals(200, response.getStatusCode());
        assertNotNull(response.getData());
        assertTrue(response.getData().size() > 0);
    }

    @Test
    @Order(3)
    public void testFindById() {
        Response<Client> response = clientRepo.findById(clientId);
        assertEquals(200, response.getStatusCode());
        assertNotNull(response.getData());
        assertEquals("John Doe", response.getData().getName());
    }

    @Test
    @Order(4)
    public void testUpdateClient() {
        Response<Client> response = clientRepo.findById(clientId);
        assertEquals(200, response.getStatusCode());
        Client client = response.getData();
        client.setName("John Updated");
        Response<Client> updateResponse = clientRepo.updateClient(client);
        assertEquals(200, updateResponse.getStatusCode());
        assertEquals("John Updated", updateResponse.getData().getName());
    }


}
