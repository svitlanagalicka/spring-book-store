package mate.academy.intro.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import mate.academy.intro.model.Status;

@Data
public class UpdateOrderStatusRequestDto {
    @NotNull
    private Status status;
}
