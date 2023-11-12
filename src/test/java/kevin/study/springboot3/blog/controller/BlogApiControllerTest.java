package kevin.study.springboot3.blog.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kevin.study.springboot3.blog.domain.Article;
import kevin.study.springboot3.blog.dto.ArticleRequest;
import kevin.study.springboot3.blog.repository.BlogRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class BlogApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper; //직렬화, 역직렬화를 위한 클래스

    @Autowired
    private BlogRepository blogRepository;

    @BeforeEach
    void mockMvcSetup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                                      .build();
        blogRepository.deleteAll();
    }

    @Test
    @DisplayName("블로그 글 추가 api 테스트")
    void addArticleTest() throws Exception {
        //given
        final String url = "/api/article";
        final String title = "제목";
        final String content = "내용";
        final ArticleRequest request = ArticleRequest.builder()
                                                     .title(title)
                                                     .content(content)
                                                     .build();

        //request 객체를 String (JSON 형태)으로 직렬화
        final String requestBody = objectMapper.writeValueAsString(request);

        //when
        ResultActions result = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody));

        //then
        result.andExpect(status().isCreated());

        List<Article> articles = blogRepository.findAll();

        assertThat(articles.size()).isEqualTo(1);
        assertThat(articles.get(0).getTitle()).isEqualTo(title);
        assertThat(articles.get(0).getContent()).isEqualTo(content);
    }

    @Test
    @DisplayName("블로그전체 글 조회 api 테스트")
    void findAllArticlesTest() throws Exception {
        //given
        final String url = "/api/articles";
        final String title = "제목";
        final String content = "내용";
        final String author = "user1";

        createSavedArticle(title, content, author);

        //when & then
        mockMvc.perform(get(url).accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$[0].title").value(title))
               .andExpect(jsonPath("$[0].content").value(content));
    }

    @Test
    @DisplayName("블로그 단일글 조회 api 테스트")
    void findArticleTest() throws Exception {
        //given
        final String url = "/api/article/{id}";
        //pathVariable 형태의 URL도 아래와 mockMvc.perform() 에서 사용 가능

        final String title = "제목";
        final String content = "내용";
        final String author = "user1";

        final Article savedArticle = createSavedArticle(title, content, author);

        //when
        ResultActions result = mockMvc.perform(get(url, savedArticle.getId()));

        //then
        result.andExpect(status().isOk())
              .andExpect(jsonPath("$.title").value(title))
              .andExpect(jsonPath("$.content").value(content));
    }

    @Test
    @DisplayName("블로그 단일글 삭제 api 테스트")
    void deleteArticleTest() throws Exception {
        //given
        final String url = "/api/article/{id}";
        //pathVariable 형태의 URL도 아래와 mockMvc.perform() 에서 사용 가능

        Article savedArticle = createSavedArticle("제목", "내용","user1");

        Long articleId = savedArticle.getId();
        //when
        mockMvc.perform(delete(url, articleId));

        //then
        Article article = blogRepository.findById(articleId)
                                        .orElse(null);

        Assertions.assertEquals(article, null);
    }

    @Test
    @DisplayName("블로그 글 수정 api 테스트")
    void updateArticleTest() throws Exception {
        //given
        Article savedArticle = createSavedArticle("제목", "내용","user1");

        final String url = "/api/article/{id}";
        final String title = "수정된 제목";
        final String content = "수정된 내용";

        final ArticleRequest request = ArticleRequest.builder()
                                                     .title(title)
                                                     .content(content)
                                                     .build();


        //when
        ResultActions result = mockMvc.perform(put(url, savedArticle.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        //then
        result.andExpect(status().isOk());

        Article articles = blogRepository.findById(savedArticle.getId())
                                         .orElseThrow(() ->
                                                 new IllegalArgumentException("not found id : " + savedArticle.getId()));

        assertThat(articles.getTitle()).isEqualTo(title);
        assertThat(articles.getContent()).isEqualTo(content);
    }

    private Article createSavedArticle(String title, String content, String author) {
        Article article = Article.builder()
                                 .title(title)
                                 .content(content)
                                 .author(author)
                                 .build();
        return blogRepository.save(article);
    }
}