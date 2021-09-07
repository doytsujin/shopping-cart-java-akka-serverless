/* This code was initialised by Akka Serverless tooling.
 * As long as this file exists it will not be re-generated.
 * You are free to make changes to this file.
 */

package cartve.domain;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.akkaserverless.javasdk.EntityId;
import com.akkaserverless.javasdk.Reply;
import com.akkaserverless.javasdk.reply.MessageReply;
import com.akkaserverless.javasdk.valueentity.CommandContext;
import com.akkaserverless.javasdk.valueentity.ValueEntity;
import com.google.protobuf.Empty;

import cartve.ShoppingCartVeApi;
import cartve.ShoppingCartVeApi.AddLineItem;
import cartve.ShoppingCartVeApi.ChangeLineItemQuantity;
import cartve.ShoppingCartVeApi.CheckoutShoppingCart;
import cartve.ShoppingCartVeApi.RemoveLineItem;
import cartve.ShoppingCartVeApi.RemoveShoppingCart;

/** A value entity. */
@ValueEntity(entityType = "cart_ve")
public class ShoppingCartVe extends AbstractShoppingCartVe {
  @SuppressWarnings("unused")
  private final String entityId;

  public ShoppingCartVe(@EntityId String entityId) {
    this.entityId = entityId;
  }

  @Override
  public Reply<Empty> addItem(ShoppingCartVeApi.AddLineItem command, CommandContext<ShoppingCartVeDomain.Cart> context) {
    var cart = Cart.toCart(context.getState());
    cart.addItem(command, context);
    context.updateState(cart.toState());
    return Reply.message(Empty.getDefaultInstance());
  }

  @Override
  public Reply<Empty> changeItem(ShoppingCartVeApi.ChangeLineItemQuantity command, CommandContext<ShoppingCartVeDomain.Cart> context) {
    var cart = Cart.toCart(context.getState());
    cart.changeItem(command, context);
    context.updateState(cart.toState());
    return Reply.message(Empty.getDefaultInstance());
  }

  @Override
  public Reply<Empty> removeItem(ShoppingCartVeApi.RemoveLineItem command, CommandContext<ShoppingCartVeDomain.Cart> context) {
    var cart = Cart.toCart(context.getState());
    cart.removeItem(command, context);
    context.updateState(cart.toState());
    return Reply.message(Empty.getDefaultInstance());
  }

  @Override
  public Reply<Empty> checkoutCart(ShoppingCartVeApi.CheckoutShoppingCart command, CommandContext<ShoppingCartVeDomain.Cart> context) {
    var cart = Cart.toCart(context.getState());
    cart.checkoutCart(command, context);
    context.updateState(cart.toState());
    return Reply.message(Empty.getDefaultInstance());
  }

  @Override
  public MessageReply<ShoppingCartVeApi.Cart> getCart(ShoppingCartVeApi.GetShoppingCart command, CommandContext<ShoppingCartVeDomain.Cart> context) {
    return Reply.message(Cart.toCart(context.getState()).toApi());
  }

  @Override
  public Reply<Empty> removeCart(ShoppingCartVeApi.RemoveShoppingCart command, CommandContext<ShoppingCartVeDomain.Cart> context) {
    var cart = Cart.toCart(context.getState());
    cart.removeCart(command, context);
    context.updateState(cart.toState());
    return Reply.message(Empty.getDefaultInstance());
  }

  static class Cart {
    String cartId;
    boolean checkedOut;
    boolean deleted;
    Map<String, LineItem> items = new LinkedHashMap<>();

    static class LineItem {
      String productId;
      String name;
      int quantity;

      LineItem(String productId, String name, int quantity) {
        this.productId = productId;
        this.name = name;
        this.quantity = quantity;
      }
    }

    void addItem(AddLineItem command, CommandContext<ShoppingCartVeDomain.Cart> context) {
      verifyNotCheckedOutOrDeleted(context);
      if (command.getQuantity() <= 0) {
        throw context.fail("Quantity must be greater than 0");
      }
      items.put(command.getProductId(), new LineItem(command.getProductId(), command.getName(), command.getQuantity()));
    }

    void changeItem(ChangeLineItemQuantity command, CommandContext<ShoppingCartVeDomain.Cart> context) {
      verifyNotCheckedOutOrDeleted(context);
      if (command.getQuantity() <= 0) {
        throw context.fail("Quantity must be greater than 0");
      }
      items.computeIfPresent(command.getProductId(), (key, value) -> new LineItem(key, value.name, command.getQuantity()));
    }

    void removeItem(RemoveLineItem command, CommandContext<ShoppingCartVeDomain.Cart> context) {
      verifyNotCheckedOutOrDeleted(context);
      items.remove(command.getProductId());
    }

    void checkoutCart(CheckoutShoppingCart command, CommandContext<ShoppingCartVeDomain.Cart> context) {
      verifyNotDeleted(context);
      checkedOut = true;
    }

    void removeCart(RemoveShoppingCart command, CommandContext<ShoppingCartVeDomain.Cart> context) {
      deleted = true;
    }

    void verifyNotCheckedOutOrDeleted(CommandContext<ShoppingCartVeDomain.Cart> context) {
      verifyNotCheckedOut(context);
      verifyNotDeleted(context);
    }

    void verifyNotCheckedOut(CommandContext<ShoppingCartVeDomain.Cart> context) {
      if (checkedOut) {
        throw context.fail("Cart is already checked");
      }
    }

    void verifyNotDeleted(CommandContext<ShoppingCartVeDomain.Cart> context) {
      if (deleted) {
        throw context.fail("Cart has been deleted");
      }
    }

    static Cart toCart(Optional<ShoppingCartVeDomain.Cart> cart) {
      return toCart(cart.orElse(ShoppingCartVeDomain.Cart.newBuilder().build()));
    }

    static Cart toCart(ShoppingCartVeDomain.Cart domain) {
      var cart = new Cart();
      cart.cartId = domain.getCartId();
      cart.checkedOut = domain.getCheckedOut();
      cart.deleted = domain.getDeleted();
      domain.getItemsList().forEach(item -> cart.items.put(item.getProductId(), new LineItem(item.getProductId(), item.getName(), item.getQuantity())));
      return cart;
    }

    ShoppingCartVeDomain.Cart toState() {
      var lineItems = items.values().stream().map(item -> ShoppingCartVeDomain.LineItem
          .newBuilder()
          .setProductId(item.productId)
          .setName(item.name)
          .setQuantity(item.quantity)
          .build())
          .collect(Collectors.toList());
      return ShoppingCartVeDomain.Cart
          .newBuilder()
          .setCartId(cartId)
          .setCheckedOut(checkedOut)
          .setDeleted(deleted)
          .addAllItems(lineItems)
          .build();
    }

    ShoppingCartVeApi.Cart toApi() {
      var lineItems = items.values().stream().map(item -> ShoppingCartVeApi.LineItem
          .newBuilder()
          .setProductId(item.productId)
          .setName(item.name)
          .setQuantity(item.quantity)
          .build())
          .collect(Collectors.toList());
      return ShoppingCartVeApi.Cart
          .newBuilder()
          .setCheckedOut(checkedOut)
          .setDeleted(deleted)
          .addAllItems(lineItems)
          .build();
    }
  }
}
