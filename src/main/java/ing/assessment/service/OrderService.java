package ing.assessment.service;

import ing.assessment.dto.Order.OrderRequestDto;
import ing.assessment.dto.Order.OrderResponseDto;

public interface OrderService {
    OrderResponseDto createOrder(OrderRequestDto orderRequestDto);
}