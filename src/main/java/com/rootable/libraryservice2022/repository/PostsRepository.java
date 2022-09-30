package com.rootable.libraryservice2022.repository;

import com.rootable.libraryservice2022.domain.Member;
import com.rootable.libraryservice2022.domain.Posts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostsRepository extends JpaRepository<Posts, Long> {

    @Query("select p from Posts p order by p.id desc")
    List<Posts> findPosts();

    @Query("select p from Posts p join p.member m where m.id = :memberId order by p.id desc")
    List<Posts> findMyPosts(@Param("memberId") Long memberId);


}
