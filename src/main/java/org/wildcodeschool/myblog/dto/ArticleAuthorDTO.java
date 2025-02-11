package org.wildcodeschool.myblog.dto;

import org.wildcodeschool.myblog.model.Article;
import org.wildcodeschool.myblog.model.Author;

public class ArticleAuthorDTO {
    private Long id;
    private Long articleId;
    private Long authorId;
    private String contribution;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getArticleId() {
        return articleId;
    }

    public void setArticleId(Long articleId) {
        this.articleId = articleId;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }

    public String getContribution() {
        return contribution;
    }

    public void setContribution(String contribution) {
        this.contribution = contribution;
    }
}
