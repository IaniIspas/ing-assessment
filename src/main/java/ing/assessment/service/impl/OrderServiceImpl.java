package ing.assessment.service.impl;

import ing.assessment.db.order.Order;
import ing.assessment.db.order.OrderConstants;
import ing.assessment.db.order.OrderProduct;
import ing.assessment.db.product.Product;
import ing.assessment.db.repository.OrderRepository;
import ing.assessment.db.repository.ProductRepository;
import ing.assessment.dto.order.OrderRequestDto;
import ing.assessment.dto.order.OrderResponseDto;
import ing.assessment.dto.product.ProductRequestDto;
import ing.assessment.exception.InvalidOrderException;
import ing.assessment.exception.OutOfStockException;
import ing.assessment.exception.ProductNotFoundException;
import ing.assessment.model.Location;
import ing.assessment.service.OrderService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@RequiredArgsConstructor
@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional
    public OrderResponseDto createOrder(OrderRequestDto orderRequestDto) {
        if (orderRequestDto.getProducts() == null || orderRequestDto.getProducts().isEmpty()) {
            throw new InvalidOrderException();
        }

        List<OrderProduct> orderProducts = new ArrayList<>();
        Set<Location> locations = new HashSet<>();

        double productsTotalCost = processProducts(orderRequestDto, orderProducts, locations);

        int deliveryCost = calculateDeliveryCost(productsTotalCost);
        double finalCost = calculateFinalCost(productsTotalCost);

        int deliveryTime = calculateDeliveryTime(locations);

        Order order = new Order();
        order.setTimestamp(new Date());
        order.setOrderProducts(orderProducts);
        order.setOrderCost(finalCost);
        order.setDeliveryCost(deliveryCost);
        order.setDeliveryTime(deliveryTime);
        orderRepository.save(order);

        return new OrderResponseDto(finalCost, deliveryCost, deliveryTime);
    }

    private double processProducts(OrderRequestDto orderRequestDto, List<OrderProduct> orderProducts, Set<Location> locations) {
        double productsTotalCost = 0.0;

        for (ProductRequestDto productRequest : orderRequestDto.getProducts()) {
            Location location = Location.valueOf(productRequest.getLocation().toUpperCase());

            Product product = productRepository
                    .findByProductCkIdAndProductCkLocation(productRequest.getProductId(), location)
                   .orElseThrow(ProductNotFoundException::new);

            if (product.getQuantity() < productRequest.getQuantity()) {
                throw new OutOfStockException();
            }

            double itemCost = product.getPrice() * productRequest.getQuantity();
            productsTotalCost += itemCost;

            locations.add(location);

            OrderProduct orderProduct = new OrderProduct(
                    productRequest.getProductId(),
                    productRequest.getQuantity()
            );
            orderProducts.add(orderProduct);
        }
        return productsTotalCost;
    }

    private int calculateDeliveryCost(double productsTotalCost) {
        if (productsTotalCost > OrderConstants.FREE_DELIVERY_THRESHOLD) {
            return 0;
        } else {
            return OrderConstants.DEFAULT_DELIVERY_COST;
        }
    }

    private double calculateFinalCost(double productsTotalCost) {
        if (productsTotalCost > OrderConstants.DISCOUNT_THRESHOLD) {
            return productsTotalCost * OrderConstants.DISCOUNT_RATE;
        } else if (productsTotalCost > OrderConstants.FREE_DELIVERY_THRESHOLD) {
            return productsTotalCost;
        } else {
            return productsTotalCost + OrderConstants.DEFAULT_DELIVERY_COST;
        }
    }

    private int calculateDeliveryTime(Set<Location> locations) {
        return OrderConstants.DEFAULT_DELIVERY_TIME + ((locations.size() - 1) * 2);
    }
}