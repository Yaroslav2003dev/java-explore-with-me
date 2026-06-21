package ru.practicum.explore_with_me.user.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.explore_with_me.user.model.User;

import java.util.List;

public interface UserRepository extends JpaRepository<User,Long> {
    @Query("Select u FROM User u Where u.id in :ids")
    List<User> findAllByIds(@RequestParam("ids") List<Long> ids, Pageable pageable);

    boolean existsByEmail(String email);
}
