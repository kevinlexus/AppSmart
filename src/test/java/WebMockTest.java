import com.ast.app.dto.Customer;
import com.ast.app.dto.Product;
import com.ast.app.service.App;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(
        webEnvironment = RANDOM_PORT,
        classes = App.class)
@AutoConfigureMockMvc
@TestPropertySource(
        locations = "classpath:application.properties")
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class WebMockTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String token;

    @BeforeAll
    public void authorizeBeforeTest() throws Exception {
        MvcResult result = this.mockMvc.perform(post("/api/v1/auth/login")
                .content("{" +
                        "    \"email\":\"admin@mail.ru\"," +
                        "    \"password\":\"test1\"" +
                        "}")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();
        String strResult = result.getResponse().getContentAsString();
        token = JsonPath.parse(strResult).read("$.token").toString();
    }

    @Test
    @Rollback(false)
    public void checkCreateCustomer() throws Exception {
        // create Customer

        UUID customerId = addCustomer("Customer-1");
        UUID customer2Id = addCustomer("Customer-2");

        addCustomer("Customer-3");
        addCustomer("Customer-4");
        addCustomer("Customer-5");

        // edit Customer
        this.mockMvc.perform(put("/customers/{customerId}", customerId)
                .header("Authorization", token)
                .content(objectMapper.writeValueAsString(
                        new Customer(customerId, "Customer-1 changed title")))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        // get Customer
        this.mockMvc.perform(get("/customers/{customerId}", customerId)
                .header("Authorization", token))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json("{'title':'Customer-1 changed title'}"));


        // create new Product for Customer
        final BigDecimal price = BigDecimal.valueOf(10.23);
        final BigDecimal price2 = BigDecimal.valueOf(15.40);
        UUID productId = addProduct(customerId, price, "Product-1", "Description of Product-1");
        addProduct(customerId, price, "Product-2", "Description of Product-2");
        addProduct(customerId, price, "Product-3", "Description of Product-3");
        addProduct(customerId, price, "Product-4", "Description of Product-4");
        addProduct(customerId, price, "Product-5", "Description of Product-5");
        addProduct(customerId, price, "Product-6", "Description of Product-5");

        // edit Product
        this.mockMvc.perform(put("/products/{productId}", productId)
                .header("Authorization", token)
                .content(objectMapper.writeValueAsString(
                        new Product(customer2Id, "Product-1 Updated", "Changed description of Product-1",
                                price2)))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        // get Product by Id
        this.mockMvc.perform(get("/products/{productId}", productId)
                .header("Authorization", token))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json("{'customerId':" + customer2Id + "}"))
                .andExpect(content().json("{'title':'Product-1 Updated'}"))
                .andExpect(content().json("{'description':'Changed description of Product-1'}"))
                .andExpect(content().json("{'price':" + price2.toString() + "}"));

        // get paginated list of all Customers
        this.mockMvc.perform(get("/customers")
                .header("Authorization", token))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].title", hasSize(5)))
                .andExpect(jsonPath("$[0].title").value("Customer-1 changed title"))
                .andExpect(jsonPath("$[1].title").value("Customer-2"))
                .andExpect(jsonPath("$[2].title").value("Customer-3"))
                .andExpect(jsonPath("$[3].title").value("Customer-4"))
                .andExpect(jsonPath("$[4].title").value("Customer-5"));

        // get paginated list of all customer Products
        this.mockMvc.perform(get("/customers/{customerId}/products", customerId)
                .header("Authorization", token))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].title", hasSize(5)))
                .andExpect(jsonPath("$[0].title").value("Product-2"))
                .andExpect(jsonPath("$[1].title").value("Product-3"))
                .andExpect(jsonPath("$[2].title").value("Product-4"))
                .andExpect(jsonPath("$[3].title").value("Product-5"))
                .andExpect(jsonPath("$[4].title").value("Product-6"));

        // delete Product
        this.mockMvc.perform(delete("/products/{productId}", productId)
                .header("Authorization", token))
                .andDo(print())
                .andExpect(status().isOk());

        // delete Customer
        this.mockMvc.perform(delete("/customers/{customerId}", customerId)
                .header("Authorization", token))
                .andDo(print())
                .andExpect(status().isOk());

        // make sure all Products of the Customer was deleted
        this.mockMvc.perform(get("/customers/{customerId}/products", customerId)
                .header("Authorization", token))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].title", hasSize(0)));

    }

    private UUID addProduct(UUID customerId, BigDecimal price, String productTitle, String productDescription) throws Exception {
        MvcResult result;
        String strUuid;
        result = this.mockMvc.perform(post("/customers/{customerId}/products", customerId)
                .header("Authorization", token)
                .content(objectMapper.writeValueAsString(
                        new Product(customerId, productTitle, productDescription,
                                price)))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();
        strUuid = result.getResponse().getContentAsString().replace("\"", "");
        return UUID.fromString(strUuid);
    }

    private UUID addCustomer(String customerName) throws Exception {
        MvcResult result;
        String strUuid;
        result = this.mockMvc.perform(post("/customers")
                .header("Authorization", token)
                .content(objectMapper.writeValueAsString(new Customer(null, customerName)))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();
        strUuid = result.getResponse().getContentAsString().replace("\"", "");
        return UUID.fromString(strUuid);
    }

}