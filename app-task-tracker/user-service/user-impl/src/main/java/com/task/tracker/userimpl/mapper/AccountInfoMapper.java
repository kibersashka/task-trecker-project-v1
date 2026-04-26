package com.task.tracker.userimpl.mapper;

import com.task.tracker.commonlib.dto.AccountUpdateRequest;
import com.task.tracker.commonlib.dto.AccountUpdateResponse;
import com.task.tracker.userimpl.entity.AccountInfo;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface AccountInfoMapper {

    @Mapping(source = "xp", target = "xpCount")
    AccountUpdateResponse toResponse(AccountInfo accountInfo);

    @Mapping(target = "xp", ignore = true)
    @Mapping(target = "id", ignore = true)
    AccountInfo toEntity(AccountUpdateRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "xp", ignore = true)
    @Mapping(target = "id", ignore = true)
    void updateFromDto(AccountUpdateRequest request, @MappingTarget AccountInfo entity);
}