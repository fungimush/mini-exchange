package com.fungie.exchange.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

public class Order {

    private String symbol;
    private Side side;
    private OrderType type;
    private BigDecimal price;
    private long quantity;
    private long filledQuantity;
    private OrderStatus status;
    private Instant createdAt;

    private Order(String symbol, Side side, OrderType type, BigDecimal price, long quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        this.symbol = Objects.requireNonNull(symbol);
        this.side = Objects.requireNonNull(side);
        this.type = Objects.requireNonNull(type);
        this.price = price;
        this.quantity = quantity;
        this.filledQuantity = 0;
        this.status = OrderStatus.NEW;
        this.createdAt = Instant.now();
    }

    public static Order limit(String symbol, Side side, BigDecimal price, long quantity) {
        if (price == null || price.signum() <= 0) {
            throw new IllegalArgumentException("A limit order needs a positive price");
        }
        return new Order(symbol, side, OrderType.LIMIT, price, quantity);
    }

    public static Order market(String symbol, Side side, long quantity) {
        return new Order(symbol, side, OrderType.MARKET, null, quantity);
    }

    public long remainingQuantity() {
        return quantity - filledQuantity;
    }

    public boolean isOpen() {
        return status.isOpen();
    }

    public void fill(long qty) {
        if (!isOpen()) {
            throw new IllegalStateException("Cannot fill an order that is " + status);
        }
        if (qty <= 0 || qty > remainingQuantity()) {
            throw new IllegalArgumentException("Invalid fill quantity " + qty + ", remaining is " + remainingQuantity());
        }
        filledQuantity += qty;
        status = remainingQuantity() == 0 ? OrderStatus.FILLED : OrderStatus.PARTIALLY_FILLED;
    }

    public void cancel() {
        if (!isOpen()) {
            throw new IllegalStateException("Cannot cancel an order that is " + status);
        }
        status = OrderStatus.CANCELLED;
    }

    public String getSymbol() {
        return symbol;
    }

    public Side getSide() {
        return side;
    }

    public OrderType getType() {
        return type;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public long getQuantity() {
        return quantity;
    }

    public long getFilledQuantity() {
        return filledQuantity;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
