package com.rootable.libraryservice2022.service;

import com.rootable.libraryservice2022.domain.*;
import com.rootable.libraryservice2022.repository.BookRepository;
import com.rootable.libraryservice2022.repository.MemberRepository;
import com.rootable.libraryservice2022.repository.PostsRepository;
import com.rootable.libraryservice2022.web.dto.BookSaveDto;
import com.rootable.libraryservice2022.web.dto.CommentRequestDto;
import com.rootable.libraryservice2022.web.dto.MemberDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.*;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class CommentServiceTest {

    @Autowired CommentService commentService;
    @Autowired PostsRepository postsRepository;
    @Autowired MemberRepository memberRepository;
    @Autowired BookRepository bookRepository;
    @Autowired EntityManager entityManager;

    @Test
    public void commentSave() {

        String com = "Hello";

        //given
        MemberDto memberDto = MemberDto.builder()
                .name("kim")
                .loginId("test2")
                .password("1111!!qq")
                .email("test2@gmail.com")
                .role(Role.USER)
                .build();

        Member member = createMember(memberDto);
        memberRepository.save(member);

        BookSaveDto bookDto = BookSaveDto.builder()
                .title("자바 기초")
                .writer("TonyJoo")
                .price(10000)
                .stock(3)
                .status(Status.PERMISSION)
                .build();

        Book book = createBook(bookDto);
        bookRepository.save(book);

        Posts posts = Posts.builder()
                .title("aa")
                .member(member)
                .book(book)
                .build();

        CommentRequestDto dto = CommentRequestDto.builder()
                .comment(com)
                .member(member)
                .posts(posts)
                .build();

        postsRepository.save(posts);

        //when
        Long savedId = commentService.commentSave(member.getId(), posts.getId(), dto);

        //then
        Comment comment = commentService.getComment(savedId);
        assertThat(comment.getComment()).isEqualTo(com);

    }

    @Test
    public void update() {

        String com = "comment";
        String newCom = "new";

        //given
        MemberDto memberDto = MemberDto.builder()
                .name("kim")
                .loginId("test2")
                .password("1111!!qq")
                .email("test2@gmail.com")
                .role(Role.USER)
                .build();

        Member member = createMember(memberDto);
        memberRepository.save(member);

        BookSaveDto bookDto = BookSaveDto.builder()
                .title("자바 기초")
                .writer("TonyJoo")
                .price(10000)
                .stock(3)
                .status(Status.PERMISSION)
                .build();

        Book book = createBook(bookDto);
        bookRepository.save(book);

        Posts posts = Posts.builder()
                .title("aa")
                .member(member)
                .book(book)
                .build();

        CommentRequestDto dto = CommentRequestDto.builder()
                .comment(com)
                .member(member)
                .posts(posts)
                .build();

        CommentRequestDto updateDto = CommentRequestDto.builder()
                .comment(newCom)
                .member(member)
                .posts(posts)
                .build();

        postsRepository.save(posts);
        Long savedId = commentService.commentSave(member.getId(), posts.getId(), dto);

        //when
        Long updatedId = commentService.update(savedId, updateDto);
        Comment comment = commentService.getComment(updatedId);

        //then
        assertThat(comment.getComment()).isEqualTo(newCom);

    }

    @Test
    public void delete() {

        //given
        MemberDto memberDto = MemberDto.builder()
                .name("kim")
                .loginId("test2")
                .password("1111!!qq")
                .email("test2@gmail.com")
                .role(Role.USER)
                .build();

        Member member = createMember(memberDto);
        memberRepository.save(member);

        BookSaveDto bookDto = BookSaveDto.builder()
                .title("자바 기초")
                .writer("TonyJoo")
                .price(10000)
                .stock(3)
                .status(Status.PERMISSION)
                .build();

        Book book = createBook(bookDto);
        bookRepository.save(book);

        Posts posts = Posts.builder()
                .title("aa")
                .member(member)
                .book(book)
                .build();

        CommentRequestDto dto = CommentRequestDto.builder()
                .comment("bb")
                .member(member)
                .posts(posts)
                .build();

        postsRepository.save(posts);
        Long savedId = commentService.commentSave(member.getId(), posts.getId(), dto);

        //when
        Comment comment = commentService.getComment(savedId);
        commentService.delete(savedId);

        //then
        assertThat(entityManager.contains(comment)).isFalse();

    }

    public Member createMember(MemberDto dto) {
        return Member.builder()
                .name(dto.getName())
                .loginId(dto.getLoginId())
                .password(dto.getPassword())
                .email(dto.getEmail())
                .role(dto.getRole())
                .build();
    }

    public Book createBook(BookSaveDto dto) {
        return Book.builder()
                .title(dto.getTitle())
                .writer(dto.getWriter())
                .price(dto.getPrice())
                .stock(dto.getStock())
                .status(dto.getStatus())
                .build();
    }

}