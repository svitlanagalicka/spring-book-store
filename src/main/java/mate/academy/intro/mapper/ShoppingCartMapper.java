package mate.academy.intro.mapper;

import mate.academy.intro.config.MapperConfig;
import mate.academy.intro.dto.ShoppingCartDto;
import mate.academy.intro.dto.ShoppingCartRequestDto;
import mate.academy.intro.model.ShoppingCart;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface ShoppingCartMapper {

    ShoppingCartDto toShoppingCartDto(ShoppingCart shoppingCart);

    ShoppingCart toModel(ShoppingCartRequestDto shoppingCartRequestDto);
}
