package com.fungie.exchange.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class OrderTest {

    @Test
    void partialFillLeavesOrderPartiallyFilled() {
        Order order = Order.limit("BHP", Side.BUY, new BigDecimal("45.00"), 100);

        order.fill(30);

        assertThat(order.getStatus()).isEqualTo(OrderStatus.PARTIALLY_FILLED);
        assertThat(order.remainingQuantity()).isEqualTo(70);
    }

    //fillingAllSharesMakesOrderFilled: create an order for 100, fill 100,
    // then assert the status is FILLED and the remaining quantity is 0.

    @Test
    void fillingAllSharesMakesOrderFilled() {
        Order order = Order.limit("BHP", Side.BUY, new BigDecimal("45.00"), 100);

        order.fill(100);

        assertThat(order.getStatus()).isEqualTo(OrderStatus.FILLED);
        assertThat(order.remainingQuantity()).isZero();
    }

    //fillingMoreThanRemainingThrows: order for 100, then assert that fill(150) throws IllegalArgumentException.

    @Test
    void fillingMoreThanRemainingThrows() {
        Order order = Order.limit("BHP", Side.BUY, new BigDecimal("45.00"), 100);

        assertThatThrownBy(() -> order.fill(150)).isInstanceOf(IllegalArgumentException.class);
    }
    //cancelledOrderCannotBeFilled: cancel an order, then fill(10) should throw IllegalStateException

    @Test
    void cancelledOrderCannotBeFilled() {
        Order order = Order.limit("BHP", Side.BUY, new BigDecimal("45.00"), 100);

        order.cancel();

        assertThatThrownBy(() -> order.fill(10)).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void limitOrderWithNegativePriceIsRejected() {

        assertThatThrownBy(() -> Order.limit("BHP", Side.BUY, new BigDecimal("-1.00"), 100))
                .isInstanceOf(IllegalArgumentException.class);
    }

}