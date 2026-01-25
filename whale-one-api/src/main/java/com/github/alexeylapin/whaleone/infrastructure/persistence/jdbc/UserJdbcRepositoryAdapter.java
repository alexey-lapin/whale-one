package com.github.alexeylapin.whaleone.infrastructure.persistence.jdbc;

import com.github.alexeylapin.whaleone.domain.Page;
import com.github.alexeylapin.whaleone.domain.model.User;
import com.github.alexeylapin.whaleone.domain.model.UserListElement;
import com.github.alexeylapin.whaleone.domain.repo.UserRepository;
import com.github.alexeylapin.whaleone.infrastructure.config.MappingConfig;
import com.github.alexeylapin.whaleone.infrastructure.persistence.jdbc.util.DefaultPage;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class UserJdbcRepositoryAdapter implements UserRepository {

    private final UserJdbcRepository repository;
    private final UserMapper mapper;

    @Override
    public User save(User user) {
        UserEntity entity = mapper.map(user);
        UserEntity saved = repository.save(entity);
        return mapper.map(saved).toBuilder()
                .createdBy(user.createdBy())
                .lastUpdatedBy(user.lastUpdatedBy())
                .build();
    }

    @Override
    public Optional<User> findById(long id) {
        return repository.findOneById(id).map(mapper::map);
    }

    @Override
    public Page<UserListElement> findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("username"));
        var items = repository.findAllByUsernameNotIn(List.of("admin"), pageable.getPageSize(), pageable.getOffset());
        var aPage = PageableExecutionUtils.getPage(items, pageable, repository::count);
        return new DefaultPage<>(aPage);
    }

    @Mapper(config = MappingConfig.class)
    interface UserMapper {

        @Mapping(source = "createdBy.id", target = "createdById")
        @Mapping(source = "lastUpdatedBy.id", target = "lastUpdatedById")
        UserEntity map(User source);

        @Mapping(source = "createdById", target = "createdBy.id")
//        @Mapping(source = "createdByName", target = "createdBy.name")
        @Mapping(source = "lastUpdatedById", target = "lastUpdatedBy.id")
//        @Mapping(source = "lastUpdatedByName", target = "lastUpdatedBy.name")
        User map(UserEntity source);

        @Mapping(source = "createdById", target = "createdBy.id")
        @Mapping(source = "createdByName", target = "createdBy.name")
        @Mapping(source = "lastUpdatedById", target = "lastUpdatedBy.id")
        @Mapping(source = "lastUpdatedByName", target = "lastUpdatedBy.name")
        User map(UserJdbcRepository.UserProjection source);

        default UserAuthorityEntity map(String value) {
            return new UserAuthorityEntity(value);
        }

        default String map(UserAuthorityEntity entity) {
            return entity.name();
        }

    }

}
