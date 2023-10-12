package kevin.study.springboot3.blog.dto;


import kevin.study.springboot3.blog.domain.Article;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ArticleResponse {
    private final Long id;
    private final String title;
    private final String content;
    private final LocalDateTime createdAt;

    @Builder
    public ArticleResponse(Article article) {
        this.id = article.getId();
        this.title = article.getTitle();
        this.content = article.getContent();
        this.createdAt = article.getCreatedAt();
    }
}
