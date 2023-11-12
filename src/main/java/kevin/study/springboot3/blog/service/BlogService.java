package kevin.study.springboot3.blog.service;


import kevin.study.springboot3.blog.domain.Article;
import kevin.study.springboot3.blog.dto.ArticleRequest;
import kevin.study.springboot3.blog.dto.ArticleResponse;
import kevin.study.springboot3.blog.repository.BlogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BlogService {
    private final BlogRepository blogRepository;

    public Article save(ArticleRequest request, String userName) {
        return blogRepository.save(request.toEntity(userName));
    }

    public List<ArticleResponse> findAll() {
        List<ArticleResponse> responses = blogRepository.findAll()
                                                        .stream()
                                                        .map(article -> new ArticleResponse(article))
                                                        .collect(Collectors.toList());
        return responses;
    }

    public ArticleResponse findById(Long id) {
        Article article = blogRepository.findById(id)
                                        .orElseThrow(() -> new IllegalArgumentException("not found id : " + id));
        return new ArticleResponse(article);
    }

    public void delete(Long id) {
        blogRepository.deleteById(id);
    }

    @Transactional
    public ArticleResponse update(Long id, ArticleRequest request) {
        Article article = blogRepository.findById(id)
                                        .orElseThrow(() -> new IllegalArgumentException("not found id : " + id));
        article.update(request.getTitle(), request.getContent());
        return new ArticleResponse(article);
    }


}
