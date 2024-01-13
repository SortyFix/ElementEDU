package de.gaz.eedu.livechat.chat;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatRepository extends JpaRepository<ChatEntity, Long>
{
    @Query(value = "SELECT c FROM ChatEntity c LEFT JOIN c.users cu GROUP BY c "
            + "HAVING SUM(CASE WHEN cu IN (:users) THEN 1 ELSE -1 END) = :listSize")
    @NotNull Optional<List<ChatEntity>> findAllByUsersIn(@NotNull @Param("users") List<Long> users,
                                                         @NotNull @Param("listSize") Long listSize);
}
