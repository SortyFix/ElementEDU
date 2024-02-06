package de.gaz.eedu.livechat.message;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<MessageEntity, Long>
{
    MessageEntity findMessageEntityByMessageId(Long id);
}
