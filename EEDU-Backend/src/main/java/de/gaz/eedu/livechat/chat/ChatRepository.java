package de.gaz.eedu.livechat.chat;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRepository extends JpaRepository<ChatEntity, Long>
{
    public ChatEntity findChatEntitiesByChatId(Long id);


}
