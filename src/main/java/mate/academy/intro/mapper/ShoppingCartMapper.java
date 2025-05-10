package mate.academy.intro.mapper;

import mate.academy.intro.config.MapperConfig;
import mate.academy.intro.dto.ShoppingCartDto;
import mate.academy.intro.model.ShoppingCart;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class, uses = CartItemMapper.class)
public interface ShoppingCartMapper {

    ShoppingCartDto toShoppingCartDto(ShoppingCart shoppingCart);
}
