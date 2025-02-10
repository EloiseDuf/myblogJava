package org.wildcodeschool.myblog.controller;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.wildcodeschool.myblog.dto.ArticleDTO;
import org.wildcodeschool.myblog.dto.AuthorDTO;
import org.wildcodeschool.myblog.model.*;
import org.wildcodeschool.myblog.repository.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/articles")

public class ArticleController {

    //injection de dépendances

    private final ArticleRepository articleRepository;
    private final CategoryRepository categoryRepository;
    private final ImageRepository imageRepository;
    private final AuthorRepository authorRepository;
    private final ArticleAuthorRepository articleAuthorRepository;

    public ArticleController(ArticleRepository articleRepository, CategoryRepository categoryRepository, ImageRepository imageRepository, AuthorRepository authorRepository, ArticleAuthorRepository articleAuthorRepository) {
        this.articleRepository = articleRepository;
        this.categoryRepository = categoryRepository;
        this.imageRepository = imageRepository;
        this.authorRepository = authorRepository;
        this.articleAuthorRepository = articleAuthorRepository;
    }


    private ArticleDTO convertToDTO(Article article) {
        ArticleDTO articleDTO = new ArticleDTO();
        articleDTO.setId(article.getId());
        articleDTO.setTitle(article.getTitle());
        articleDTO.setContent(article.getContent());
        articleDTO.setUpdatedAt(article.getUpdatedAt());
        if(article.getCategory()!=null){
            articleDTO.setCategoryName(article.getCategory().getName());
        }

        if(article.getImages()!=null){
            articleDTO.setImageUrls(article.getImages().stream().map(Image::getUrl).collect(Collectors.toList()));
        }

        if (article.getArticleAuthors() != null) {
            articleDTO.setAuthors(article.getArticleAuthors().stream()
                    .filter(articleAuthor -> articleAuthor.getAuthor() != null)
                    .map(articleAuthor -> {
                        AuthorDTO authorDTO = new AuthorDTO();
                        authorDTO.setId(articleAuthor.getAuthor().getId());
                        authorDTO.setFirstname(articleAuthor.getAuthor().getFirstname());
                        authorDTO.setLastname(articleAuthor.getAuthor().getLastname());
                        return authorDTO;
                    })
                    .collect(Collectors.toList()));
        }

        return articleDTO;

    }

    @Transactional
    @GetMapping
    public ResponseEntity<List<ArticleDTO>> getAllArticles() {
        List<Article> articles = articleRepository.findAll();
        if (articles.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        List<ArticleDTO> articlesDTOs = articles.stream().map(this::convertToDTO).collect(Collectors.toList());
        return ResponseEntity.ok(articlesDTOs);
    }

    @Transactional
    @GetMapping("/{id}")
    public ResponseEntity<ArticleDTO> getArticleById(@PathVariable Long id) {

        Article article = articleRepository.findById(id).orElse(null);
        if (article == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(convertToDTO(article));
    }

    @GetMapping ("/search-title")
    public ResponseEntity<List<ArticleDTO>> getArticlesByTitle(@RequestParam String searchTerms) {
        List<Article> articles=articleRepository.findByTitle(searchTerms);
        if (articles.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        List<ArticleDTO> articlesDTOs = articles.stream().map(this::convertToDTO).collect(Collectors.toList());
        return ResponseEntity.ok(articlesDTOs);
    }

    @GetMapping("/search-content")
    public ResponseEntity<List<ArticleDTO>> getArticlesByContent(@RequestParam String keyword) {
        List<Article> articles=articleRepository.findByContentContaining(keyword);
        if(articles.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        List<ArticleDTO> articlesDTOs = articles.stream().map(this::convertToDTO).collect(Collectors.toList());
        return ResponseEntity.ok(articlesDTOs);
    }

    @GetMapping("/search-top5")
    public ResponseEntity<List<ArticleDTO>> getFiveLastArticles() {
        List<Article> articles=articleRepository.findTop5ByOrderByCreatedAtDesc();
        if(articles.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        List<ArticleDTO> articlesDTOs = articles.stream().map(this::convertToDTO).collect(Collectors.toList());
        return ResponseEntity.ok(articlesDTOs);
    }

    @GetMapping("/search-createAfter")
    public  ResponseEntity<List<ArticleDTO>> getArticlesByCreateAfter(@RequestParam String createAfter) {
        LocalDate createdAt = LocalDate.parse(createAfter);
        List<Article> articles=articleRepository.findByCreatedAtGreaterThanOrderByCreatedAtDesc(createdAt.atStartOfDay());
        if(articles.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        List<ArticleDTO> articlesDTOs = articles.stream().map(this::convertToDTO).collect(Collectors.toList());
        return ResponseEntity.ok(articlesDTOs);
    }

    @PostMapping
    public ResponseEntity<ArticleDTO> createArticle(@RequestBody Article article) {
        article.setCreatedAt(LocalDateTime.now());
        article.setUpdatedAt(LocalDateTime.now());

        //ajout catégorie existante

        if (article.getCategory() != null) {
            Category category = categoryRepository.findById(article.getCategory().getId()).orElse(null);
            if (category == null) {
                return ResponseEntity.badRequest().body(null);
            }
            article.setCategory(category);
        }
        //ajout  image existante

        if(article.getImages()!=null && !article.getImages().isEmpty()){
            List<Image> validImages=new ArrayList<>();
            for(Image image:article.getImages()){
                if(image.getId()!=null){
                    Image existingImages=imageRepository.findById(image.getId()).orElse(null);
                    if(existingImages!=null){
                        validImages.add(existingImages);
                    } else {
                        return ResponseEntity.badRequest().body(null);
                    }
                } else {
                    Image savedImage=imageRepository.save(image);
                    validImages.add(savedImage);

            }
        }
            article.setImages(validImages);
        }


        Article savedArticle = articleRepository.save(article);

        if(article.getArticleAuthors()!=null){
            for(ArticleAuthor articleAuthor :article.getArticleAuthors()){
                Author author=articleAuthor.getAuthor();
                author=authorRepository.findById(author.getId()).orElse(null);
                if(author==null){
                    return ResponseEntity.badRequest().body(null);
                }

                articleAuthor.setAuthor(author);
                articleAuthor.setArticle(savedArticle);
                articleAuthor.setContribution(articleAuthor.getContribution());

                articleAuthorRepository.save(articleAuthor);
            }
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(convertToDTO(savedArticle));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ArticleDTO> updateArticle(@PathVariable Long id, @RequestBody Article articleDetails) {

        Article article = articleRepository.findById(id).orElse(null);
        if (article == null) {
            return ResponseEntity.notFound().build();
        }

        article.setTitle(articleDetails.getTitle());
        article.setContent(articleDetails.getContent());
        article.setUpdatedAt(LocalDateTime.now());

        if (articleDetails.getCategory() != null) {
            Category category = categoryRepository.findById(articleDetails.getCategory().getId()).orElse(null);
            if (category == null) {
                return ResponseEntity.badRequest().body(null);
            }
            article.setCategory(category);
        }

        if(articleDetails.getImages()!=null){
            List <Image> validImages =new ArrayList<>();
            for(Image image: articleDetails.getImages()){
                if(image.getId()!=null){
                    Image existingImage=imageRepository.findById(image.getId()).orElse(null);
                    if(existingImage!=null){
                        validImages.add(existingImage);
                    } else {
                        return ResponseEntity.badRequest().build();
                    }
                } else {
                    Image savedImage=imageRepository.save(image);
                    validImages.add(savedImage);
                }
            }
            article.setImages(validImages);
        } else {
            article.getImages().clear();
        }

        if(articleDetails.getArticleAuthors()!=null){
            // Supprimer manuellement les anciens ArticleAuthor
            for(ArticleAuthor oldArticleAuthor :article.getArticleAuthors()){
                articleAuthorRepository.delete(oldArticleAuthor);
            }

            List<ArticleAuthor> updatedArticleAuthors =new ArrayList<>();

            for(ArticleAuthor articleAuthorDetails : articleDetails.getArticleAuthors()){
                Author author = articleAuthorDetails.getAuthor();
                author=authorRepository.findById(author.getId()).orElse(null);
                if(author==null){
                    return ResponseEntity.badRequest().build();
                }

                // Créer et associer la nouvelle relation ArticleAuthor

                ArticleAuthor newArticleAuthor =new ArticleAuthor();
                newArticleAuthor.setAuthor(author);
                newArticleAuthor.setArticle(article);
                newArticleAuthor.setContribution(articleAuthorDetails.getContribution());

                updatedArticleAuthors.add(newArticleAuthor);
            }

            for (ArticleAuthor articleAuthor :updatedArticleAuthors){
                articleAuthorRepository.save(articleAuthor);
            }

            article.setArticleAuthors(updatedArticleAuthors);
        }


        Article updatedArticle = articleRepository.save(article);
        return ResponseEntity.ok(convertToDTO(updatedArticle));

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteArticle(@PathVariable Long id) {

        Article article = articleRepository.findById(id).orElse(null);
        if (article == null) {
            return ResponseEntity.notFound().build();
        }

        // Supprimer les associations ArticleAuthor manuellement

        if(article.getArticleAuthors()!=null){
            for(ArticleAuthor articleAuthor : article.getArticleAuthors()){
                articleAuthorRepository.delete(articleAuthor);
            }
        }

        // Supprimer l'article lui-même
        articleRepository.delete(article);
        return ResponseEntity.noContent().build();
    }
}
