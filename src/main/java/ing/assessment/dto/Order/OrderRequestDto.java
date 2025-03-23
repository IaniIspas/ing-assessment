package ing.assessment.dto.Order;

import ing.assessment.dto.Product.ProductRequestDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequestDto {
    private List<ProductRequestDto> products;
}
