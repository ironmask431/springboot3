package kevin.study.springboot3.blog.controller;


import kevin.study.springboot3.blog.dto.ArticleResponse;
import kevin.study.springboot3.blog.service.BlogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class BlogViewController {

    private final BlogService blogService;

    @GetMapping("/articles")
    public String getArticles(Model model) {
        List<ArticleResponse> articles = blogService.findAll();
        model.addAttribute("articles", articles);
        return "articles";
    }

    @GetMapping("/articles/{id}")
    public String getArticle(@PathVariable Long id, Model model){
        ArticleResponse article = blogService.findById(id);
        model.addAttribute("article", article);
        return "article";
    }

    @GetMapping("/new-article")
    public String newArticle(@RequestParam(required = false) Long id, Model model){
        if(id == null){
            model.addAttribute("article", new ArticleResponse());
        }else{
            model.addAttribute("article", blogService.findById(id));
        }
        return "newArticle";
    }
}
