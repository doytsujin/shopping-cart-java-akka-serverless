/* This code was initialised by Akka Serverless tooling.
 * As long as this file exists it will not be re-generated.
 * You are free to make changes to this file.
 */

package cartve.domain;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import com.akkaserverless.javasdk.valueentity.CommandContext;

import org.junit.Test;

import cartve.ShoppingCartVeApi;

public class ShoppingCartVeTest {
  private String entityId = "entityId1";
  private ShoppingCartVe entity;

  @SuppressWarnings("unchecked")
  private CommandContext<ShoppingCartVeDomain.Cart> context = mock(CommandContext.class);

  @Test
  public void addItemTest() {
    entity = new ShoppingCartVe(entityId);

    when(context.getState()).thenReturn(Optional.empty());

    var addLineItem = ShoppingCartVeApi.AddLineItem.newBuilder()
        .setCartId("cart1")
        .setProductId("item4")
        .setName("item4")
        .setQuantity(4)
        .build();

    entity.addItem(addLineItem, context);

    var state = ShoppingCartVeDomain.Cart.newBuilder()
        .addItems(getAddLineItem("item4", "item4", 4))
        .build();

    verify(context).updateState(state);
  }

  @Test(expected = RuntimeException.class)
  public void addItemTestWithInvalidQuantity() {
    entity = new ShoppingCartVe(entityId);

    when(context.getState()).thenReturn(Optional.empty());

    var addLineItem = ShoppingCartVeApi.AddLineItem.newBuilder()
        .setCartId("cart1")
        .setProductId("item4")
        .setName("item4")
        .setQuantity(0)
        .build();

    entity.addItem(addLineItem, context);
  }

  @Test
  public void changeItemTest() {
    entity = new ShoppingCartVe(entityId);

    var stateBefore = ShoppingCartVeDomain.Cart.newBuilder()
        .addItems(getAddLineItem("item4", "item4", 4))
        .build();

    when(context.getState()).thenReturn(Optional.of(stateBefore));

    var changeLineItemQuantity = ShoppingCartVeApi.ChangeLineItemQuantity.newBuilder()
        .setCartId("cart1")
        .setProductId("item4")
        .setQuantity(5)
        .build();

    entity.changeItem(changeLineItemQuantity, context);

    var stateAfter = ShoppingCartVeDomain.Cart.newBuilder()
        .addItems(getAddLineItem("item4", "item4", 5))
        .build();

    verify(context).updateState(stateAfter);
  }

  @Test(expected = RuntimeException.class)
  public void changeItemTestWithInvalidQuantity() {
    entity = new ShoppingCartVe(entityId);

    var stateBefore = ShoppingCartVeDomain.Cart.newBuilder()
        .addItems(getAddLineItem("item4", "item4", 4))
        .build();

    when(context.getState()).thenReturn(Optional.of(stateBefore));
    when(context.fail(anyString())).thenThrow(new RuntimeException());

    var changeLineItemQuantity = ShoppingCartVeApi.ChangeLineItemQuantity.newBuilder()
        .setCartId("cart1")
        .setProductId("item4")
        .setQuantity(0)
        .build();

    entity.changeItem(changeLineItemQuantity, context);
  }

  @Test
  public void removeItemTest() {
    entity = new ShoppingCartVe(entityId);

    var stateBefore = ShoppingCartVeDomain.Cart.newBuilder()
        .addItems(getAddLineItem("item1", "item1", 1))
        .addItems(getAddLineItem("item2", "item2", 2))
        .addItems(getAddLineItem("item3", "item3", 3))
        .addItems(getAddLineItem("item4", "item4", 4))
        .addItems(getAddLineItem("item5", "item5", 5))
        .build();

    when(context.getState()).thenReturn(Optional.of(stateBefore));

    var removeLineItem = ShoppingCartVeApi.RemoveLineItem.newBuilder()
        .setCartId("cart1")
        .setProductId("item3")
        .build();

    entity.removeItem(removeLineItem, context);

    var stateAfter = ShoppingCartVeDomain.Cart.newBuilder()
        .addItems(getAddLineItem("item1", "item1", 1))
        .addItems(getAddLineItem("item2", "item2", 2))
        .addItems(getAddLineItem("item4", "item4", 4))
        .addItems(getAddLineItem("item5", "item5", 5))
        .build();

    verify(context).updateState(stateAfter);
  }

  @Test
  public void checkoutCartTest() {
    entity = new ShoppingCartVe(entityId);

    var stateBefore = ShoppingCartVeDomain.Cart.newBuilder()
        .addItems(getAddLineItem("item1", "item1", 1))
        .addItems(getAddLineItem("item2", "item2", 2))
        .build();

    when(context.getState()).thenReturn(Optional.of(stateBefore));

    var checkoutCart = ShoppingCartVeApi.CheckoutShoppingCart.newBuilder()
        .setCartId("cart1")
        .build();

    entity.checkoutCart(checkoutCart, context);

    var stateAfter = ShoppingCartVeDomain.Cart.newBuilder()
        .setCheckedOut(true)
        .addItems(getAddLineItem("item1", "item1", 1))
        .addItems(getAddLineItem("item2", "item2", 2))
        .build();

    verify(context).updateState(stateAfter);
  }

  @Test
  public void getCartTest() {
    entity = new ShoppingCartVe(entityId);

    when(context.getState())
        .thenReturn(Optional.of(ShoppingCartVeDomain.Cart
            .newBuilder()
            .setCartId("cart1")
            .addItems(getAddLineItem("productId1", "item1", 1))
            .addItems(getAddLineItem("productId2", "item2", 2))
            .addItems(getAddLineItem("productId3", "item3", 3))
            .addItems(getAddLineItem("productId4", "item4", 4))
            .build()));

    ShoppingCartVeApi.GetShoppingCart getShoppingCart = ShoppingCartVeApi.GetShoppingCart
        .newBuilder()
        .setCartId("cart1")
        .build();

    var reply = entity.getCart(getShoppingCart, context);
    assertEquals(4, reply.payload().getItemsList().size());
    assertEquals("productId1", reply.payload().getItems(0).getProductId());
    assertEquals("productId2", reply.payload().getItems(1).getProductId());
    assertEquals("productId3", reply.payload().getItems(2).getProductId());
    assertEquals("productId4", reply.payload().getItems(3).getProductId());
  }

  @Test
  public void removeCartTest() {
    entity = new ShoppingCartVe(entityId);

    var stateBefore = ShoppingCartVeDomain.Cart.newBuilder()
        .addItems(getAddLineItem("item1", "item1", 1))
        .addItems(getAddLineItem("item2", "item2", 2))
        .build();

    when(context.getState()).thenReturn(Optional.of(stateBefore));

    var removeCart = ShoppingCartVeApi.RemoveShoppingCart.newBuilder()
        .setCartId("cart1")
        .build();

    entity.removeCart(removeCart, context);

    var stateAfter = ShoppingCartVeDomain.Cart.newBuilder()
        .setDeleted(true)
        .addItems(getAddLineItem("item1", "item1", 1))
        .addItems(getAddLineItem("item2", "item2", 2))
        .build();

    verify(context).updateState(stateAfter);
  }

  private static ShoppingCartVeDomain.LineItem getAddLineItem(String productId, String itemId, int quantity) {
    return ShoppingCartVeDomain.LineItem.newBuilder()
        .setProductId(productId)
        .setQuantity(quantity)
        .setName(itemId)
        .build();
  }
}
