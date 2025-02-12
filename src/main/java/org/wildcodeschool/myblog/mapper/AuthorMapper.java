package org.wildcodeschool.myblog.mapper;

import org.springframework.stereotype.Component;
import org.wildcodeschool.myblog.dto.AuthorContributionDTO;
import org.wildcodeschool.myblog.dto.AuthorDTO;
import org.wildcodeschool.myblog.exception.ResourceNotFoundException;
import org.wildcodeschool.myblog.model.Author;
import org.wildcodeschool.myblog.service.AuthorService;

@Component
public class AuthorMapper {

    public AuthorDTO convertToDTO(Author author) {
        AuthorDTO authorDTO = new AuthorDTO();
        authorDTO.setId(author.getId());
        authorDTO.setFirstname(author.getFirstname());
        authorDTO.setLastname(author.getLastname());
        if (author.getArticleAuthors() != null) {
            authorDTO.setArticleIds(author.getArticleAuthors().stream()
                    .filter(articleAuthor -> articleAuthor.getArticle() != null)
                    .map( articleAuthor -> {
                        return articleAuthor.getArticle().getId();
                    })
                    .toList());
        }
        return authorDTO;
    }

}
