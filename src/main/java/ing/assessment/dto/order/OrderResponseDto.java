package ing.assessment.dto.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponseDto {
    private Double orderCost;
    private Integer deliveryCost;
    private Integer deliveryTime;
}
