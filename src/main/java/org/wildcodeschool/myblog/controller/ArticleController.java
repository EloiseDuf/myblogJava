package org.wildcodeschool.myblog.controller;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.wildcodeschool.myblog.dto.ArticleDTO;
import org.wildcodeschool.myblog.dto.AuthorDTO;
import org.wildcodeschool.myblog.model.*;
import org.wildcodeschool.myblog.repository.*;
import org.wildcodeschool.myblog.service.ArticleService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/articles")

public class ArticleController {

    //injection de dépendances

    private final ArticleService articleService;


    public ArticleController(ArticleService articleService) {
        this.articleService=articleService;
    }

    @GetMapping
    public ResponseEntity<List<ArticleDTO>> getAllArticles() {
        List<ArticleDTO> articles = articleService.getAllArticles();
        if (articles.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(articles);
    }


    @GetMapping("/{id}")
    public ResponseEntity<ArticleDTO> getArticleById(@PathVariable Long id) {

        ArticleDTO article = articleService.getArticleById(id);
        if (article == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(article);
    }

    @GetMapping ("/search-title")
    public ResponseEntity<List<ArticleDTO>> getArticlesByTitle(@RequestParam String searchTerms) {
        List<ArticleDTO> articles=articleService.getArticlesByTitle(searchTerms);
        if (articles.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(articles);
    }

    @GetMapping("/search-content")
    public ResponseEntity<List<ArticleDTO>> getArticlesByContent(@RequestParam String keyword) {
        List<ArticleDTO> articles=articleService.getArticlesByContent(keyword);
        if(articles.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(articles);
    }

    @GetMapping("/search-top5")
    public ResponseEntity<List<ArticleDTO>> getFiveLastArticles() {
        List<ArticleDTO> articles=articleService.getFiveLastArticles();
        if(articles.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(articles);
    }

    @GetMapping("/search-createAfter")
    public  ResponseEntity<List<ArticleDTO>> getArticlesByCreateAfter(@RequestParam String createAfter) {
        List<ArticleDTO> articles=articleService.getArticlesByCreateAfter(createAfter);
        if(articles.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(articles);
    }

    @PostMapping
    public ResponseEntity<ArticleDTO> createArticle(@RequestBody Article article) {
        ArticleDTO savedArticle = articleService.createArticle(article);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedArticle);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ArticleDTO> updateArticle(@PathVariable Long id, @RequestBody Article articleDetails) {

        ArticleDTO updatedarticle = articleService.updateArticle(id,articleDetails);

        if(updatedarticle==null){
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(updatedarticle);

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteArticle(@PathVariable Long id) {
        if (articleService.deleteArticle(id)) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
