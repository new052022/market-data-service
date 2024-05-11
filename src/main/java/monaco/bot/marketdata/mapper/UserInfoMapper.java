package monaco.bot.marketdata.mapper;

import monaco.bot.marketdata.dto.UserInfoRequestDto;
import monaco.bot.marketdata.dto.UserInfoResponseDto;
import monaco.bot.marketdata.model.UserInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserInfoMapper {

    @Mapping(target = "id", ignore = true)
    UserInfo toUserInfo(UserInfoRequestDto request);

    UserInfoResponseDto toUserInfoResponseDto(UserInfo savedUser);

}
