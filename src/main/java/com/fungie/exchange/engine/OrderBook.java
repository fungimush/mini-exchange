package com.fungie.exchange.engine;

import com.fungie.exchange.domain.Order;
import com.fungie.exchange.domain.OrderType;
import com.fungie.exchange.domain.Side;

import java.math.BigDecimal;
import java.util.*;

public class OrderBook {

    private final String symbol;
    private final NavigableMap<BigDecimal, Deque<Order>> bids = new TreeMap<>(Comparator.reverseOrder());
    private final NavigableMap<BigDecimal, Deque<Order>> asks = new TreeMap<>();

    public OrderBook(String symbol) {
        this.symbol = symbol;
    }

    private NavigableMap<BigDecimal, Deque<Order>> sideOf(Side side) {
        return side == Side.BUY ? bids : asks;
    }

    public Optional<BigDecimal> bestBid() {
        return bids.isEmpty() ? Optional.empty() : Optional.of(bids.firstKey());
    }

    public Optional<BigDecimal> bestAsk() {
        return asks.isEmpty() ? Optional.empty() : Optional.of(asks.firstKey());
    }

    public void add(Order order) {
        if (order.getType() != OrderType.LIMIT) {
            throw new IllegalArgumentException("Only LIMIT orders can rest on the book");
        }
        if (!order.getSymbol().equals(symbol)) {
            throw new IllegalArgumentException( "Order for " + order.getSymbol() + " sent to the " + symbol + " book");
        }
        sideOf(order.getSide())
                .computeIfAbsent(order.getPrice(), p -> new ArrayDeque<>())
                .addLast(order);
    }

    public String getSymbol() {
        return symbol;
    }
}
