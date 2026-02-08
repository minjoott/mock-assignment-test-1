package com.minjoott.musinsa.order.domain.entity;

import com.minjoott.musinsa.order.domain.vo.OrderStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class OrderTest {

    @Test
    @DisplayName("주문을 확정한다")
    void confirmOrder() {
        Order order = new Order("ORD-20260208-0001", 100000);

        order.confirm();

        assertThat(order.getStatus()).isEqualTo(OrderStatus.CONFIRMED);
    }

    @Test
    @DisplayName("주문을 취소한다")
    void cancelOrder() {
        Order order = new Order("ORD-20260208-0001", 100000);
        order.confirm();

        order.cancel();

        assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED);
        assertThat(order.getCancelledAt()).isNotNull();
    }

    @Test
    @DisplayName("이미 취소된 주문을 다시 취소하면 예외가 발생한다")
    void throwsExceptionWhenCancellingAlreadyCancelledOrder() {
        Order order = new Order("ORD-20260208-0001", 100000);
        order.confirm();
        order.cancel();

        assertThatThrownBy(order::cancel)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("이미 취소된 주문입니다.");
    }

    @Test
    @DisplayName("주문에 상품을 추가한다")
    void addOrderItem() {
        Order order = new Order("ORD-20260208-0001", 100000);
        Product product = new Product("오버핏 후드", 39000);
        OrderItem orderItem = new OrderItem(product, 2, 39000);

        order.addOrderItem(orderItem);

        assertThat(order.getOrderItems()).hasSize(1);
        assertThat(order.getOrderItems().get(0)).isEqualTo(orderItem);
        assertThat(orderItem.getOrder()).isEqualTo(order);
    }
}
