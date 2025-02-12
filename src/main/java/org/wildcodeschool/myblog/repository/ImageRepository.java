package org.wildcodeschool.myblog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.wildcodeschool.myblog.model.Image;
import java.util.List;


public interface ImageRepository extends JpaRepository<Image, Long> {
    Image findByUrl(String url);
}
