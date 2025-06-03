package net.mooh.forumservice.service;

import net.mooh.forumservice.dtos.MessageDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MessageService {

    MessageDto creerMessage(MessageDto messageDto);

    MessageDto repondreMessage(Long messageParentId, MessageDto messageDto);

    MessageDto getMessageById(Long id, Long utilisateurId);

    Page<MessageDto> getMessagesBySujetId(Long sujetId, Pageable pageable, Long utilisateurId);

    List<MessageDto> getMessagesByAuteurId(Long auteurId);

    Page<MessageDto> getMessagesNonValides(Long sujetId, Pageable pageable);

    MessageDto updateMessage(Long id, MessageDto messageDto, Long utilisateurId);

    void deleteMessage(Long id, Long utilisateurId);

    void validerMessage(Long id, Long moderateurId);

    void like(Long id, Long utilisateurId);

    void unlike(Long id, Long utilisateurId);
}
