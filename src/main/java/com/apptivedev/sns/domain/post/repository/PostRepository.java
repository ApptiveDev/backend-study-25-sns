package com.apptivedev.sns.domain.post.repository;

import com.apptivedev.sns.domain.post.entity.Post;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Override
    @EntityGraph(attributePaths = "author")
    List<Post> findAll();

    @Override
    @EntityGraph(attributePaths = "author")
    Page<Post> findAll(Pageable pageable);

    @Override
    @EntityGraph(attributePaths = "author")
    Optional<Post> findById(Long id);

    void deleteAllByAuthor_Id(Long authorId);
}
