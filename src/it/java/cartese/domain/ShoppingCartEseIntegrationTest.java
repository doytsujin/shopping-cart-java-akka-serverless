/* This code was initialised by Akka Serverless tooling.
 * As long as this file exists it will not be re-generated.
 * You are free to make changes to this file.
 */

package cartese.domain;

import io.example.Main;
import cartese.CartServiceClient;
import com.akkaserverless.javasdk.testkit.junit.AkkaServerlessTestkitResource;
import org.junit.ClassRule;
import org.junit.Test;

import static java.util.concurrent.TimeUnit.*;

// Example of an integration test calling our service via the Akka Serverless proxy
// Run all test classes ending with "IntegrationTest" using `mvn verify -Pit`
public class ShoppingCartEseIntegrationTest {
    
    /**
     * The test kit starts both the service container and the Akka Serverless proxy.
     */
    @ClassRule
    public static final AkkaServerlessTestkitResource testkit = new AkkaServerlessTestkitResource(Main.SERVICE);
    
    /**
     * Use the generated gRPC client to call the service through the Akka Serverless proxy.
     */
    private final CartServiceClient client;
    
    public ShoppingCartEseIntegrationTest() {
        client = CartServiceClient.create(testkit.getGrpcClientSettings(), testkit.getActorSystem());
    }
    
    @Test
    public void addItemOnNonExistingEntity() throws Exception {
        // TODO: set fields in command, and provide assertions to match replies
        // client.addItem(ShoppingCartEseApi.AddLineItem.newBuilder().build())
        //         .toCompletableFuture().get(2, SECONDS);
    }
    
    @Test
    public void changeItemOnNonExistingEntity() throws Exception {
        // TODO: set fields in command, and provide assertions to match replies
        // client.changeItem(ShoppingCartEseApi.ChangeLineItemQuantity.newBuilder().build())
        //         .toCompletableFuture().get(2, SECONDS);
    }
    
    @Test
    public void removeItemOnNonExistingEntity() throws Exception {
        // TODO: set fields in command, and provide assertions to match replies
        // client.removeItem(ShoppingCartEseApi.RemoveLineItem.newBuilder().build())
        //         .toCompletableFuture().get(2, SECONDS);
    }
    
    @Test
    public void checkoutCartOnNonExistingEntity() throws Exception {
        // TODO: set fields in command, and provide assertions to match replies
        // client.checkoutCart(ShoppingCartEseApi.CheckoutShoppingCart.newBuilder().build())
        //         .toCompletableFuture().get(2, SECONDS);
    }
    
    @Test
    public void getCartOnNonExistingEntity() throws Exception {
        // TODO: set fields in command, and provide assertions to match replies
        // client.getCart(ShoppingCartEseApi.GetShoppingCart.newBuilder().build())
        //         .toCompletableFuture().get(2, SECONDS);
    }
    
    @Test
    public void removeCartOnNonExistingEntity() throws Exception {
        // TODO: set fields in command, and provide assertions to match replies
        // client.removeCart(ShoppingCartEseApi.RemoveShoppingCart.newBuilder().build())
        //         .toCompletableFuture().get(2, SECONDS);
    }
}