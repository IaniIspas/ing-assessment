package ing.assessment.service.impl;

import ing.assessment.db.order.Order;
import ing.assessment.db.product.Product;
import ing.assessment.db.product.ProductCK;
import ing.assessment.db.repository.OrderRepository;
import ing.assessment.db.repository.ProductRepository;
import ing.assessment.dto.order.OrderRequestDto;
import ing.assessment.dto.order.OrderResponseDto;
import ing.assessment.dto.product.ProductRequestDto;
import ing.assessment.exception.InvalidOrderException;
import ing.assessment.exception.OutOfStockException;
import ing.assessment.exception.ProductNotFoundException;
import ing.assessment.model.Location;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class OrderServiceImplTest {
    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    private OrderServiceImpl orderServiceImpl;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        orderServiceImpl = new OrderServiceImpl(orderRepository, productRepository);
    }

    @Test
    void givenValidOrder_whenCreateOrder_thenSuccessful() {
        List<ProductRequestDto> productRequests = List.of(
            new ProductRequestDto(1, "MUNICH", 1),
            new ProductRequestDto(2, "COLOGNE", 2),
            new ProductRequestDto(3, "FRANKFURT", 1)
        );
        OrderRequestDto orderRequestDto = new OrderRequestDto(productRequests);

        Product product1 = new Product(new ProductCK(1, Location.MUNICH), "Shoes", 400.0, 50);
        Product product2 = new Product(new ProductCK(2, Location.COLOGNE), "Shirt", 100.0, 50);
        Product product3 = new Product(new ProductCK(3, Location.FRANKFURT), "Jeans", 200.0, 50);

        when(productRepository.findByProductCkIdAndProductCkLocation(1, Location.MUNICH))
            .thenReturn(Optional.of(product1));
        when(productRepository.findByProductCkIdAndProductCkLocation(2, Location.COLOGNE))
            .thenReturn(Optional.of(product2));
        when(productRepository.findByProductCkIdAndProductCkLocation(3, Location.FRANKFURT))
            .thenReturn(Optional.of(product3));

        OrderResponseDto responseDto = orderServiceImpl.createOrder(orderRequestDto);

        assertEquals(800.0, responseDto.getOrderCost());
        assertEquals(0, responseDto.getDeliveryCost());
        assertEquals(6, responseDto.getDeliveryTime());

        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void givenEmptyOrder_whenCreateOrder_thenInvalidOrderException() {
        OrderRequestDto orderRequestDto = new OrderRequestDto(Collections.emptyList());
        assertThrows(InvalidOrderException.class, () -> orderServiceImpl.createOrder(orderRequestDto));
    }

    @Test
    void givenNonExistingProduct_whenCreateOrder_thenProductNotFoundException() {
        OrderRequestDto orderRequestDto = new OrderRequestDto(List.of(
            new ProductRequestDto(999, "MUNICH", 1)
        ));

        when(productRepository.findByProductCkIdAndProductCkLocation(999, Location.MUNICH))
            .thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> orderServiceImpl.createOrder(orderRequestDto));
    }

    @Test
    void givenInsufficientStock_whenCreateOrder_thenOutOfStockException() {
        OrderRequestDto orderRequestDto = new OrderRequestDto(List.of(
            new ProductRequestDto(1, "MUNICH", 100)
        ));

        Product product = new Product(new ProductCK(1, Location.MUNICH), "Shoes", 400.0, 50);
        when(productRepository.findByProductCkIdAndProductCkLocation(1, Location.MUNICH))
            .thenReturn(Optional.of(product));

        assertThrows(OutOfStockException.class, () -> orderServiceImpl.createOrder(orderRequestDto));
    }
}
