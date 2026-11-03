package com.fungie.exchange.engine;

import com.fungie.exchange.domain.Order;
import com.fungie.exchange.domain.Side;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

public class OrderBookTest {

//    emptyBookHasNoBestPrices: a new book’s bestBid() and bestAsk() are both empty
//    bestBidIsHighestBuyPrice: add buys at 44.90 and 45.00, best bid is 45.00
//    bestAskIsLowestSellPrice: add sells at 45.20 and 45.10, best ask is 45.10
//    marketOrderCannotBeAddedToBook: adding a market order throws IllegalArgumentException
//    orderForMatchingSymbolIsAccepted: a CBA book accepts a CBA order
//
//    Hints:
//
//    Assertions on Optional: assertThat(book.bestBid()).isEmpty() and assertThat(book.bestBid()).contains(new BigDecimal("45.00"))
//    In tests 2 and 3, add the orders in the “wrong” order (the worse price first). That proves the TreeMap is doing the sorting, not the order you happened to add them in.

    @Test
    void emptyBookHasNoBestPrices() {
        OrderBook book = new OrderBook("BHP");
        assertThat(book.bestBid().isEmpty());
        assertThat(book.bestAsk().isEmpty());
    }
//    Your turn: bestBidIsHighestBuyPrice
//
//    Using those three questions:
//
//    Arrange: create a BHP book, and two limit buy orders, at 44.90 and 45.00
//    Act: add both orders to the book (add 44.90 first)
//    Assert: a normal result, so use assertThat(book.bestBid()).contains(...) with 45.00
//

    @Test
    void bestBidIsHighestBuyPrice() {
        OrderBook book = new OrderBook("BHP");
        Order lowerBid = Order.limit("BHP", Side.BUY, new BigDecimal("44.90"), 100);
        Order higherBid = Order.limit("BHP", Side.BUY, new BigDecimal("45.00"), 100);

        book.add(lowerBid);
        book.add(higherBid);

        assertThat(book.bestBid()).contains(new BigDecimal("45.00"));
    }

    @Test
    void bestAskIsLowestSellPrice() {
        OrderBook book = new OrderBook("BHP");
        Order lowerAsk = Order.limit("BHP", Side.SELL, new BigDecimal("45.10"), 100);
        Order higherAsk = Order.limit("BHP", Side.SELL, new BigDecimal("45.20"), 100);

        book.add(higherAsk);
        book.add(lowerAsk);

        assertThat(book.bestAsk()).contains(new BigDecimal("45.10"));
    }

    @Test
    void marketOrderCannotBeAddedToBook() {
        OrderBook book = new OrderBook("BHP");
        Order marketOrder = Order.market("BHP", Side.BUY, 100);

        assertThatThrownBy(() -> book.add(marketOrder))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Only LIMIT orders");
    }

    @Test
    void orderForMatchingSymbolIsAccepted() {
        OrderBook book = new OrderBook("CBA");
        Order order = Order.limit("CBA", Side.BUY, new BigDecimal("100.00"), 10);

        book.add(order);

        assertThat(book.bestBid()).contains(new BigDecimal("100.00"));
    }

//    orderForOtherSymbolIsRejected: a BHP book, and adding a CBA limit order throws IllegalArgumentException

    @Test
    void orderForOtherSymbolIsRejected() {
        OrderBook book = new OrderBook("BHP");
        Order order = Order.limit("CBA", Side.BUY, new BigDecimal("100.00"), 10);

        assertThatThrownBy(() -> book.add(order))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("CBA");
    }

}
