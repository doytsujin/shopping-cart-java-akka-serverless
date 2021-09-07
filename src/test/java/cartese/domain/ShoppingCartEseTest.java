/* This code was initialised by Akka Serverless tooling.
 * As long as this file exists it will not be re-generated.
 * You are free to make changes to this file.
 */

package cartese.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.akkaserverless.javasdk.eventsourcedentity.CommandContext;

import org.junit.Test;
import org.mockito.Mockito;

import cartese.ShoppingCartEseApi;

public class ShoppingCartEseTest {
  private String entityId = "entityId1";
  private ShoppingCartEse entity;
  private CommandContext context = Mockito.mock(CommandContext.class);

  @Test
  public void addItemTest() {
    entity = new ShoppingCartEse(entityId);

    var command = ShoppingCartEseApi.AddLineItem
        .newBuilder()
        .setCartId("cartId1")
        .setProductId("itemId1")
        .setName("name1")
        .setQuantity(1)
        .build();

    entity.addItem(command, context);

    var item = ShoppingCartEseDomain.LineItem
        .newBuilder()
        .setProductId("itemId1")
        .setName("name1")
        .setQuantity(1)
        .build();
    var event = ShoppingCartEseDomain.ItemAdded
        .newBuilder()
        .setItem(item)
        .build();

    verify(context).emit(event);

    entity.itemAdded(event);

    var cart = entity.snapshot();
    assertEquals(1, cart.getItemsList().size());
    assertEquals(item, cart.getItems(0));
  }

  private static ShoppingCartEseDomain.ItemAdded itemAddedEvent(String productId, String name, int quantity) {
    var item = ShoppingCartEseDomain.LineItem
        .newBuilder()
        .setProductId(productId)
        .setName(name)
        .setQuantity(quantity)
        .build();

    return ShoppingCartEseDomain.ItemAdded
        .newBuilder()
        .setItem(item)
        .build();
  }

  @Test
  public void changeItemTest() {
    entity = new ShoppingCartEse(entityId);

    entity.itemAdded(itemAddedEvent("productId1", "name1", 1));
    entity.itemAdded(itemAddedEvent("productId2", "name2", 2));
    entity.itemAdded(itemAddedEvent("productId3", "name3", 3));

    var command = ShoppingCartEseApi.ChangeLineItemQuantity
        .newBuilder()
        .setCartId("cartId1")
        .setProductId("productId2")
        .setQuantity(4)
        .build();

    entity.changeItem(command, context);

    var event = ShoppingCartEseDomain.ItemChangedQuantity
        .newBuilder()
        .setProductId("productId2")
        .setQuantity(4)
        .build();

    verify(context).emit(event);

    entity.itemChangedQuantity(event);

    var cart = entity.snapshot();
    assertEquals(3, cart.getItemsList().size());
    assertEquals(4, cart.getItems(1).getQuantity());
  }

  @Test
  public void removeItemTest() {
    entity = new ShoppingCartEse(entityId);

    entity.itemAdded(itemAddedEvent("productId1", "name1", 1));
    entity.itemAdded(itemAddedEvent("productId2", "name2", 2));
    entity.itemAdded(itemAddedEvent("productId3", "name3", 3));

    var command = ShoppingCartEseApi.RemoveLineItem
        .newBuilder()
        .setCartId("cartId1")
        .setProductId("productId2")
        .build();

    entity.removeItem(command, context);

    var event = ShoppingCartEseDomain.ItemRemoved
        .newBuilder()
        .setProductId("productId2")
        .build();

    verify(context).emit(event);

    entity.itemRemoved(event);

    var cart = entity.snapshot();
    assertEquals(2, cart.getItemsList().size());
    assertEquals("productId1", cart.getItems(0).getProductId());
    assertEquals("productId3", cart.getItems(1).getProductId());
  }

  @Test
  public void checkoutCartTest() {
    entity = new ShoppingCartEse(entityId);

    entity.itemAdded(itemAddedEvent("productId1", "name1", 1));
    entity.itemAdded(itemAddedEvent("productId2", "name2", 2));
    entity.itemAdded(itemAddedEvent("productId3", "name3", 3));

    var command = ShoppingCartEseApi.CheckoutShoppingCart
        .newBuilder()
        .setCartId("cartId1")
        .build();

    entity.checkoutCart(command, context);

    var event = ShoppingCartEseDomain.CheckedOut
        .newBuilder()
        .build();

    verify(context).emit(event);

    entity.checkedOut(event);

    var cart = entity.snapshot();
    assertTrue(cart.getCheckedOut());
  }

  @Test(expected = RuntimeException.class)
  public void checkoutCartTestWithEmptyCart() {
    entity = new ShoppingCartEse(entityId);

    var command = ShoppingCartEseApi.CheckoutShoppingCart
        .newBuilder()
        .setCartId("cartId1")
        .build();

    when(context.fail(anyString())).thenThrow(new RuntimeException());

    entity.checkoutCart(command, context);
  }

  @Test
  public void getCartTest() {
    entity = new ShoppingCartEse(entityId);

    entity.itemAdded(itemAddedEvent("productId1", "name1", 1));
    entity.itemAdded(itemAddedEvent("productId2", "name2", 2));
    entity.itemAdded(itemAddedEvent("productId3", "name3", 3));

    var command = ShoppingCartEseApi.GetShoppingCart
        .newBuilder()
        .setCartId("cartId1")
        .build();

    var reply = entity.getCart(command, context);

    assertNotNull(reply);
    assertEquals(3, reply.payload().getItemsList().size());
    assertEquals("productId1", reply.payload().getItems(0).getProductId());
    assertEquals("productId2", reply.payload().getItems(1).getProductId());
    assertEquals("productId3", reply.payload().getItems(2).getProductId());
  }

  @Test
  public void removeCartTest() {
    entity = new ShoppingCartEse(entityId);

    entity.itemAdded(itemAddedEvent("productId1", "name1", 1));
    entity.itemAdded(itemAddedEvent("productId2", "name2", 2));
    entity.itemAdded(itemAddedEvent("productId3", "name3", 3));

    var command = ShoppingCartEseApi.RemoveShoppingCart
        .newBuilder()
        .setCartId("cartId1")
        .build();

    entity.removeCart(command, context);

    var event = ShoppingCartEseDomain.CartRemoved
        .newBuilder()
        .build();

    verify(context).emit(event);

    entity.cartRemoved(event);

    var cart = entity.snapshot();
    assertTrue(cart.getDeleted());
  }
}
