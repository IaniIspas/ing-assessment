package ing.assessment.dto.product;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequestDto {
    @NotNull(message = "Product id must be provided")
    private Integer productId;

    @NotBlank(message = "Location must be provided")
    private String location;

    @NotNull(message = "Quantity must be provided")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;
}
