package ing.assessment.service;

import ing.assessment.dto.order.OrderRequestDto;
import ing.assessment.dto.order.OrderResponseDto;

public interface OrderService {
    OrderResponseDto createOrder(OrderRequestDto orderRequestDto);
}