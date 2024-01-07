package kevin.study.springboot3.blog.service;


import kevin.study.springboot3.blog.domain.Article;
import kevin.study.springboot3.blog.dto.ArticleRequest;
import kevin.study.springboot3.blog.dto.ArticleResponse;
import kevin.study.springboot3.blog.repository.BlogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BlogService {
    private final BlogRepository blogRepository;

    @Transactional
    public Article save(ArticleRequest request, String userName) {
        return blogRepository.save(request.toEntity(userName));
    }

    @Transactional(readOnly = true)
    public List<ArticleResponse> findAll() {
        List<ArticleResponse> responses = blogRepository.findAll()
                                                        .stream()
                                                        .map(article -> new ArticleResponse(article))
                                                        .collect(Collectors.toList());
        return responses;
    }

    @Transactional(readOnly = true)
    public ArticleResponse findById(Long id) {
        Article article = blogRepository.findById(id)
                                        .orElseThrow(() -> new IllegalArgumentException("not found id : " + id));
        return new ArticleResponse(article);
    }

    @Transactional
    public void delete(Long id, Principal principal) {
        Article article = blogRepository.findById(id)
                                        .orElseThrow(() -> new IllegalArgumentException("not found id : " + id));
        authorizeArticleAuthor_2(article, principal);
        blogRepository.delete(article);
    }



    @Transactional
    public ArticleResponse update(Long id, ArticleRequest request) {
        Article article = blogRepository.findById(id)
                                        .orElseThrow(() -> new IllegalArgumentException("not found id : " + id));
        authorizeArticleAuthor(article);
        article.update(request.getTitle(), request.getContent());
        return new ArticleResponse(article);
    }


    //현재 인증객체 securityContext의 유저이름과 article의 등록자 이름을 비교함.
    //(자신의 article만 수정, 삭제 가능하도록)
    private static void authorizeArticleAuthor(Article article){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if(!article.getAuthor().equals(username)){
            throw new IllegalArgumentException("not authorized");
        }
    }

    //현재 인증객체 securityContext의 유저이름과 article의 등록자 이름을 비교함.
    //2번째 방법. Principal 객체를 파라미터로 받아서 확인
    private static void authorizeArticleAuthor_2(Article article, Principal principal){
        String username = principal.getName();
        if(!article.getAuthor().equals(username)){
            throw new IllegalArgumentException("not authorized");
        }
    }


}
