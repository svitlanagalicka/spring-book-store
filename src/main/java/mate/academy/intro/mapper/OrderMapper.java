package mate.academy.intro.mapper;

import mate.academy.intro.config.MapperConfig;
import mate.academy.intro.dto.OrderRequestDto;
import mate.academy.intro.dto.OrderResponseDto;
import mate.academy.intro.model.Order;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class, uses = OrderItemMapper.class)
public interface OrderMapper {
    OrderResponseDto toOrderDto(Order order);

    Order toOrder(OrderRequestDto orderRequestDto);
}
