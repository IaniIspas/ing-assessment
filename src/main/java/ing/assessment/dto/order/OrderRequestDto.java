package ing.assessment.dto.order;

import ing.assessment.dto.product.ProductRequestDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequestDto {
    @NotEmpty(message = "At least one product must be provided")
    private List<@Valid ProductRequestDto> products;
}
