package ing.assessment.service.impl;

import ing.assessment.db.order.Order;
import ing.assessment.db.order.OrderConstants;
import ing.assessment.db.order.OrderProduct;
import ing.assessment.db.product.Product;
import ing.assessment.db.repository.OrderRepository;
import ing.assessment.db.repository.ProductRepository;
import ing.assessment.dto.Order.OrderRequestDto;
import ing.assessment.dto.Order.OrderResponseDto;
import ing.assessment.dto.Product.ProductRequestDto;
import ing.assessment.model.Location;
import ing.assessment.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@RequiredArgsConstructor
@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    @Override
    public OrderResponseDto createOrder(OrderRequestDto orderRequestDto) {
        List<OrderProduct> orderProducts = new ArrayList<>();
        Set<Location> locations = new HashSet<>();
        double productsTotalCost = 0.0;

        for (ProductRequestDto productRequest : orderRequestDto.getProducts()) {
            Location location = Location.valueOf(productRequest.getLocation().toUpperCase());

            Product product = productRepository
                    .findByProductCk_IdAndProductCk_Location(productRequest.getProductId(), location)
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            double itemCost = product.getPrice() * productRequest.getQuantity();
            productsTotalCost += itemCost;

            locations.add(location);

            OrderProduct orderProduct = new OrderProduct(
                    productRequest.getProductId(),
                    productRequest.getQuantity()
            );
            orderProducts.add(orderProduct);
        }

        double finalCost;
        int deliveryCost;
        if (productsTotalCost > OrderConstants.DISCOUNT_THRESHOLD) {
            deliveryCost = 0;
            finalCost = productsTotalCost * OrderConstants.DISCOUNT_RATE;
        } else if (productsTotalCost > OrderConstants.FREE_DELIVERY_THRESHOLD) {
            deliveryCost = 0;
            finalCost = productsTotalCost;
        } else {
            deliveryCost = OrderConstants.DEFAULT_DELIVERY_COST;
            finalCost = productsTotalCost + deliveryCost;
        }

        int deliveryTime = OrderConstants.DEFAULT_DELIVERY_TIME + ((locations.size() - 1) * 2);

        Order order = new Order();
        order.setTimestamp(new Date());
        order.setOrderProducts(orderProducts);
        order.setOrderCost(finalCost);
        order.setDeliveryCost(deliveryCost);
        order.setDeliveryTime(deliveryTime);
        orderRepository.save(order);

        return new OrderResponseDto(finalCost, deliveryCost, deliveryTime);
    }
}