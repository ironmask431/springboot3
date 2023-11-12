package kevin.study.springboot3.blog.controller;


import kevin.study.springboot3.blog.domain.Article;
import kevin.study.springboot3.blog.dto.ArticleRequest;
import kevin.study.springboot3.blog.dto.ArticleResponse;
import kevin.study.springboot3.blog.service.BlogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class BlogApiController {
    private final BlogService blogService;

    @PostMapping("/api/article")
    public ResponseEntity<Article> addArticle(@RequestBody ArticleRequest request,
                                              Principal principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
                             .body(blogService.save(request, principal.getName()));
        //반환 타입을 ResponseEntity로 감싸서 reponse 의 httpStatus를 설정할 수 있다.
    }

    @GetMapping("/api/articles")
    public ResponseEntity<List<ArticleResponse>> findAllArticles() {
        return ResponseEntity.ok()
                             .body(blogService.findAll());
    }

    @GetMapping("/api/article/{id}")
    public ResponseEntity<ArticleResponse> findArticle(@PathVariable Long id) {
        return ResponseEntity.ok()
                             .body(blogService.findById(id));
    }

    @DeleteMapping("/api/article/{id}")
    public ResponseEntity<Void> deleteArticle(@PathVariable Long id) {
        blogService.delete(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/api/article/{id}")
    public ResponseEntity<ArticleResponse> updateArticle(@PathVariable Long id,
                                                         @RequestBody ArticleRequest request) {
        return ResponseEntity.ok()
                             .body(blogService.update(id, request));
    }
}
