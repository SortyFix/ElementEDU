package de.gaz.eedu.livechat.message;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<MessageEntity, Long>
{
    public MessageEntity findMessageEntityByMessageId(Long id);
}
