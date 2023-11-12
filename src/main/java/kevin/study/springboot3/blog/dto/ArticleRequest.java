package kevin.study.springboot3.blog.dto;


import kevin.study.springboot3.blog.domain.Article;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class ArticleRequest {
    private String title;
    private String content;

    public Article toEntity(String author) {
        return Article.builder()
                      .title(title)
                      .content(content)
                      .author(author)
                      .build();
    }

    @Builder
    public ArticleRequest(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
