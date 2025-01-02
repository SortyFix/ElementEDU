package de.gaz.eedu.livechat.message;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<MessageEntity, Long>
{
    MessageEntity findMessageEntityByMessageId(Long id);
}
