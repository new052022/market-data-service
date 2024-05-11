package monaco.bot.marketdata.mapper;

import monaco.bot.marketdata.dto.NewUserExchangeRequestDto;
import monaco.bot.marketdata.dto.UserExchangeResponseDto;
import monaco.bot.marketdata.model.UserExchangeInfo;
import monaco.bot.marketdata.model.UserInfo;
import monaco.bot.marketdata.service.interfaces.UserInfoService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class UserExchangeInfoMapper {

    @Autowired
    private UserInfoService userInfoService;

    @Mapping(target = "userInfo", source = "userId", qualifiedByName = "toUserInfo")
    public abstract UserExchangeInfo toExchangeInfo(NewUserExchangeRequestDto dto);

    @Named("toUserInfo")
    public UserInfo toUserInfo(Long userId) {
        return userInfoService.getUser(userId);
    }

    @Mapping(target = "userId", source = "userInfo.id")
    public abstract UserExchangeResponseDto toExchangeInfoResponseDto(UserExchangeInfo savedExchangeInfo);

    public abstract List<UserExchangeResponseDto> toExchangeInfoResponseList(List<UserExchangeInfo> exchanges);

}
