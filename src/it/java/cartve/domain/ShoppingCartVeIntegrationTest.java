/* This code was initialised by Akka Serverless tooling.
 * As long as this file exists it will not be re-generated.
 * You are free to make changes to this file.
 */

package cartve.domain;

import io.example.Main;
import cartve.CartServiceClient;
import com.akkaserverless.javasdk.testkit.junit.AkkaServerlessTestkitResource;
import org.junit.ClassRule;
import org.junit.Test;

import static java.util.concurrent.TimeUnit.*;

// Example of an integration test calling our service via the Akka Serverless proxy
// Run all test classes ending with "IntegrationTest" using `mvn verify -Pit`
public class ShoppingCartVeIntegrationTest {
    
    /**
     * The test kit starts both the service container and the Akka Serverless proxy.
     */
    @ClassRule
    public static final AkkaServerlessTestkitResource testkit = new AkkaServerlessTestkitResource(Main.SERVICE);
    
    /**
     * Use the generated gRPC client to call the service through the Akka Serverless proxy.
     */
    private final CartServiceClient client;
    
    public ShoppingCartVeIntegrationTest() {
        client = CartServiceClient.create(testkit.getGrpcClientSettings(), testkit.getActorSystem());
    }
    
    @Test
    public void addItemOnNonExistingEntity() throws Exception {
        // TODO: set fields in command, and provide assertions to match replies
        // client.addItem(ShoppingCartVeApi.AddLineItem.newBuilder().build())
        //         .toCompletableFuture().get(2, SECONDS);
    }
    
    @Test
    public void changeItemOnNonExistingEntity() throws Exception {
        // TODO: set fields in command, and provide assertions to match replies
        // client.changeItem(ShoppingCartVeApi.ChangeLineItemQuantity.newBuilder().build())
        //         .toCompletableFuture().get(2, SECONDS);
    }
    
    @Test
    public void removeItemOnNonExistingEntity() throws Exception {
        // TODO: set fields in command, and provide assertions to match replies
        // client.removeItem(ShoppingCartVeApi.RemoveLineItem.newBuilder().build())
        //         .toCompletableFuture().get(2, SECONDS);
    }
    
    @Test
    public void checkoutCartOnNonExistingEntity() throws Exception {
        // TODO: set fields in command, and provide assertions to match replies
        // client.checkoutCart(ShoppingCartVeApi.CheckoutShoppingCart.newBuilder().build())
        //         .toCompletableFuture().get(2, SECONDS);
    }
    
    @Test
    public void getCartOnNonExistingEntity() throws Exception {
        // TODO: set fields in command, and provide assertions to match replies
        // client.getCart(ShoppingCartVeApi.GetShoppingCart.newBuilder().build())
        //         .toCompletableFuture().get(2, SECONDS);
    }
    
    @Test
    public void removeCartOnNonExistingEntity() throws Exception {
        // TODO: set fields in command, and provide assertions to match replies
        // client.removeCart(ShoppingCartVeApi.RemoveShoppingCart.newBuilder().build())
        //         .toCompletableFuture().get(2, SECONDS);
    }
}