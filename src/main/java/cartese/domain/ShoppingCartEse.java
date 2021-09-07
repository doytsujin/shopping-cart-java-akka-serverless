/* This code was initialised by Akka Serverless tooling.
 * As long as this file exists it will not be re-generated.
 * You are free to make changes to this file.
 */

package cartese.domain;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import com.akkaserverless.javasdk.EntityId;
import com.akkaserverless.javasdk.Reply;
import com.akkaserverless.javasdk.eventsourcedentity.CommandContext;
import com.akkaserverless.javasdk.eventsourcedentity.EventSourcedEntity;
import com.akkaserverless.javasdk.reply.MessageReply;
import com.google.protobuf.Empty;

import cartese.ShoppingCartEseApi;
import cartese.ShoppingCartEseApi.AddLineItem;
import cartese.ShoppingCartEseApi.ChangeLineItemQuantity;
import cartese.ShoppingCartEseApi.CheckoutShoppingCart;
import cartese.ShoppingCartEseApi.RemoveLineItem;
import cartese.ShoppingCartEseApi.RemoveShoppingCart;
import cartese.domain.ShoppingCartEseDomain.CartRemoved;
import cartese.domain.ShoppingCartEseDomain.CheckedOut;
import cartese.domain.ShoppingCartEseDomain.ItemAdded;
import cartese.domain.ShoppingCartEseDomain.ItemChangedQuantity;
import cartese.domain.ShoppingCartEseDomain.ItemRemoved;

/** An event sourced entity. */
@EventSourcedEntity(entityType = "cart_ese")
public class ShoppingCartEse extends AbstractShoppingCartEse {
  @SuppressWarnings("unused")
  private final String entityId;
  private Cart cart;

  public ShoppingCartEse(@EntityId String entityId) {
    this.entityId = entityId;
    cart = Cart.toCart(ShoppingCartEseDomain.Cart.newBuilder().build());
  }

  @Override
  public ShoppingCartEseDomain.Cart snapshot() {
    return cart.toDomain();
  }

  @Override
  public void handleSnapshot(ShoppingCartEseDomain.Cart snapshot) {
    cart = Cart.toCart(snapshot);
  }

  @Override
  public Reply<Empty> addItem(ShoppingCartEseApi.AddLineItem command, CommandContext context) {
    cart.verify(command, context);
    context.emit(
        ShoppingCartEseDomain.ItemAdded
            .newBuilder()
            .setItem(toItem(command))
            .build());
    return Reply.message(Empty.getDefaultInstance());
  }

  private ShoppingCartEseDomain.LineItem toItem(AddLineItem command) {
    return ShoppingCartEseDomain.LineItem.newBuilder()
        .setProductId(command.getProductId())
        .setName(command.getName())
        .setQuantity(command.getQuantity())
        .build();
  }

  @Override
  public Reply<Empty> changeItem(ShoppingCartEseApi.ChangeLineItemQuantity command, CommandContext context) {
    cart.verify(command, context);
    context.emit(
        ShoppingCartEseDomain.ItemChangedQuantity
            .newBuilder()
            .setProductId(command.getProductId())
            .setQuantity(command.getQuantity())
            .build());
    return Reply.message(Empty.getDefaultInstance());
  }

  @Override
  public Reply<Empty> removeItem(ShoppingCartEseApi.RemoveLineItem command, CommandContext context) {
    cart.verify(command, context);
    context.emit(ShoppingCartEseDomain.ItemRemoved
        .newBuilder()
        .setProductId(command.getProductId())
        .build());
    return Reply.message(Empty.getDefaultInstance());
  }

  @Override
  public Reply<Empty> checkoutCart(ShoppingCartEseApi.CheckoutShoppingCart command, CommandContext context) {
    cart.verify(command, context);
    context.emit(ShoppingCartEseDomain.CheckedOut
        .newBuilder()
        .build());
    return Reply.message(Empty.getDefaultInstance());
  }

  @Override
  public MessageReply<ShoppingCartEseApi.Cart> getCart(ShoppingCartEseApi.GetShoppingCart command, CommandContext context) {
    return Reply.message(cart.toApi());
  }

  @Override
  public Reply<Empty> removeCart(ShoppingCartEseApi.RemoveShoppingCart command, CommandContext context) {
    cart.verify(command, context);
    context.emit(
        ShoppingCartEseDomain.CartRemoved
            .newBuilder()
            .build());
    return Reply.message(Empty.getDefaultInstance());
  }

  @Override
  public void itemAdded(ShoppingCartEseDomain.ItemAdded event) {
    cart.handle(event);
  }

  @Override
  public void itemChangedQuantity(ShoppingCartEseDomain.ItemChangedQuantity event) {
    cart.handle(event);
  }

  @Override
  public void itemRemoved(ShoppingCartEseDomain.ItemRemoved event) {
    cart.handle(event);
  }

  @Override
  public void checkedOut(ShoppingCartEseDomain.CheckedOut event) {
    cart.handle(event);
  }

  @Override
  public void cartRemoved(ShoppingCartEseDomain.CartRemoved event) {
    cart.handle(event);
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

    void verify(AddLineItem command, CommandContext context) {
      verifyNotCheckedOutOrDeleted(context);
      if (command.getQuantity() <= 0) {
        throw context.fail("Quantity must be greater than 0");
      }
    }

    void verify(ChangeLineItemQuantity command, CommandContext context) {
      verifyNotCheckedOutOrDeleted(context);
      if (command.getQuantity() <= 0) {
        throw context.fail("Quantity must be greater than 0");
      }
    }

    void verify(RemoveLineItem command, CommandContext context) {
      verifyNotCheckedOutOrDeleted(context);
    }

    void verify(CheckoutShoppingCart command, CommandContext context) {
      verifyNotDeleted(context);
      if (items.isEmpty()) {
        throw context.fail("Cart is empty");
      }
    }

    void verify(RemoveShoppingCart command, CommandContext context) {
      verifyNotDeleted(context);
    }

    void handle(ItemAdded event) {
      items.put(event.getItem().getProductId(), toItem(event.getItem()));
    }

    void handle(ItemChangedQuantity event) {
      items.computeIfPresent(event.getProductId(), ((key, value) -> new LineItem(key, value.name, event.getQuantity())));
    }

    void handle(ItemRemoved event) {
      items.remove(event.getProductId());
    }

    void handle(CheckedOut event) {
      checkedOut = true;
    }

    void handle(CartRemoved event) {
      deleted = true;
    }

    void verifyNotCheckedOutOrDeleted(CommandContext context) {
      verifyNotCheckedOut(context);
      verifyNotDeleted(context);
    }

    void verifyNotCheckedOut(CommandContext context) {
      if (checkedOut) {
        throw context.fail("Cart is already checked");
      }
    }

    void verifyNotDeleted(CommandContext context) {
      if (deleted) {
        throw context.fail("Cart has been deleted");
      }
    }

    private static LineItem toItem(ShoppingCartEseDomain.LineItem item) {
      return new LineItem(item.getProductId(), item.getName(), item.getQuantity());
    }

    static Cart toCart(ShoppingCartEseDomain.Cart domain) {
      var cart = new Cart();
      cart.cartId = domain.getCartId();
      cart.checkedOut = domain.getCheckedOut();
      cart.deleted = domain.getDeleted();
      domain.getItemsList().forEach(item -> cart.items.put(item.getProductId(), toItem(item)));
      return cart;
    }

    ShoppingCartEseApi.Cart toApi() {
      return ShoppingCartEseApi.Cart
          .newBuilder()
          .setCheckedOut(checkedOut)
          .setDeleted(deleted)
          .addAllItems(items.values().stream().map(item -> toApiLineItem(item)).collect(Collectors.toList()))
          .build();
    }

    private ShoppingCartEseApi.LineItem toApiLineItem(LineItem item) {
      return ShoppingCartEseApi.LineItem
          .newBuilder()
          .setProductId(item.productId)
          .setName(item.name)
          .setQuantity(item.quantity)
          .build();
    }

    ShoppingCartEseDomain.Cart toDomain() {
      return ShoppingCartEseDomain.Cart
          .newBuilder()
          .setCartId(cartId)
          .setCheckedOut(checkedOut)
          .setDeleted(deleted)
          .addAllItems(items.values().stream().map(item -> toDomainLineItem(item)).collect(Collectors.toList()))
          .build();
    }

    private ShoppingCartEseDomain.LineItem toDomainLineItem(LineItem item) {
      return ShoppingCartEseDomain.LineItem
          .newBuilder()
          .setProductId(item.productId)
          .setName(item.name)
          .setQuantity(item.quantity)
          .build();
    }
  }
}
