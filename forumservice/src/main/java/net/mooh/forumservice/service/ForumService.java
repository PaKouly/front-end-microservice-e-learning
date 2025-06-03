package net.mooh.forumservice.service;

import net.mooh.forumservice.dtos.ForumDto;

import java.util.List;

public interface ForumService {

    ForumDto creerForum(ForumDto forumDto);

    ForumDto getForumById(Long id);

    ForumDto getForumByFormationId(Long formationId);

    List<ForumDto> getAllForums();

    ForumDto updateForum(Long id, ForumDto forumDto);

    void deleteForum(Long id);

    void activerForum(Long id);

    void desactiverForum(Long id);
}