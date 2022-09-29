package com.rootable.libraryservice2022.controller;

import com.rootable.libraryservice2022.domain.Member;
import com.rootable.libraryservice2022.domain.Posts;
import com.rootable.libraryservice2022.domain.Role;
import com.rootable.libraryservice2022.service.BookService;
import com.rootable.libraryservice2022.service.FileService;
import com.rootable.libraryservice2022.service.PostsService;
import com.rootable.libraryservice2022.web.MySecured;
import com.rootable.libraryservice2022.web.dto.FileDto;
import com.rootable.libraryservice2022.web.dto.PostDto;
import com.rootable.libraryservice2022.web.file.FileStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;

@Slf4j
@Controller
@RequiredArgsConstructor
public class PostsRestfulController {

    private final PostsService postsService;
    private final BookService bookService;
    private final FileService fileService;
    private final FileStore fileStore;

    @MySecured(role = Role.GUEST)
    @PostMapping("/posts/add")
    public String write(@Validated @ModelAttribute("posts") PostDto postDto, BindingResult bindingResult,
                        @RequestParam("file") MultipartFile files, HttpServletRequest request) {

        log.info("게시글 등록");

        HttpSession session = request.getSession();
        Member member = (Member) session.getAttribute("loginMember");

        postDto.setMember(member);

        //파일 -> 서버 (저장/업로드)
        try {
            String originFilename = files.getOriginalFilename(); //고객이 업로드한 파일명
            //업로드 파일이 없는 경우
            if ("".equals(originFilename)) {
                throw new IOException();
            }
            String storeFileName = fileStore.createStoreFileName(originFilename); //서버 저장 파일명
            String saveDir = fileStore.getFileDir(); //서버 저장 디렉토리
            //저장 디렉토리가 없는 경우
            if (!new File(saveDir).exists()) {
                try {
                    new File(saveDir).mkdir(); //생성
                } catch (Exception e) {
                    e.getStackTrace();
                }
            }
            String filePath = fileStore.getFullPath(originFilename);
            files.transferTo(new File(filePath)); //업로드

            FileDto fileDto = new FileDto();
            fileDto.setOriginFilename(originFilename);
            fileDto.setFilename(storeFileName);
            fileDto.setFilePath(filePath);

            Long fileId = fileService.saveFile(fileDto);
            postDto.setFileId(fileId);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            ////Bean Validation
            if (bindingResult.hasFieldErrors("title") || bindingResult.hasFieldErrors("book")) {
                log.info("검증 에러 errors={}", bindingResult);
                return "posts/addPost";
            }

            postsService.savePost(postDto);
        }

        return "redirect:/posts";

    }

    @MySecured(role = Role.GUEST)
    @PutMapping("/posts/{postId}/edit")
    public String edit(@PathVariable Long postId, @Validated @ModelAttribute("posts") PostDto requestDto,
                       BindingResult bindingResult, Model model, HttpServletRequest request) {

        log.info("게시글 수정");

        model.addAttribute("bookList", bookService.books());

        HttpSession session = request.getSession();
        Member member = (Member) session.getAttribute("loginMember");

        requestDto.setMember(member);

        PostDto savedPost = postsService.getPost(postId);

        //뷰 파일명 전달
        if (savedPost.getFileId() != null) {
            FileDto file = fileService.getFile(savedPost.getFileId());
            requestDto.setFileId(file.getId());
            model.addAttribute("filename", file.getOriginFilename());
        }

        //Bean Validation
        if (bindingResult.hasFieldErrors("title") || bindingResult.hasFieldErrors("book")) {
            log.info("검증 에러 errors={}", bindingResult);
            return "posts/editPost";
        }

        postsService.update(postId, requestDto);

        return "redirect:/posts/" + postId;

    }

    @MySecured(role = Role.GUEST)
    @DeleteMapping("/posts/{postId}/edit")
    public String delete(@PathVariable Long postId) {

        log.info("게시글 삭제");

        postsService.delete(postId);
        return "redirect:/posts";

    }

    @MySecured(role = Role.GUEST)
    @PostMapping("/posts/{postId}/edit")
    public String renewFile(@PathVariable Long postId, @RequestParam("file") MultipartFile files, Model model) {

        log.info("첨부파일 수정");

        model.addAttribute("bookList", bookService.books());

        try {
            String originFilename = files.getOriginalFilename(); //고객이 업로드한 파일명
            //업로드 파일이 없는 경우
            if ("".equals(originFilename)) {
                throw new IOException();
            }
            String storeFileName = fileStore.createStoreFileName(originFilename); //서버 저장 파일명
            String saveDir = fileStore.getFileDir(); //서버 저장 디렉토리
            //저장 디렉토리가 없는 경우
            if (!new File(saveDir).exists()) {
                try {
                    new File(saveDir).mkdir(); //생성
                } catch (Exception e) {
                    e.getStackTrace();
                }
            }
            String filePath = fileStore.getFullPath(originFilename);
            files.transferTo(new File(filePath)); //업로드

            //FileDto 셋팅
            FileDto fileDto = new FileDto();
            fileDto.setOriginFilename(originFilename);
            fileDto.setFilename(storeFileName);
            fileDto.setFilePath(filePath);

            //DB File 저장
            Long fileId = fileService.saveFile(fileDto);

            //현재 게시글 조회 + fileId 변경
            PostDto postDto = postsService.getPost(postId);
            postDto.setFileId(fileId);
            Posts posts = postsService.updateFile(postId, postDto);

            //뷰 파일명 전달
            if (postDto.getFileId() != null) {
                FileDto file = fileService.getFile(posts.getFileId());
                model.addAttribute("filename", file.getOriginFilename());
            }

            model.addAttribute("posts", posts);
        } catch (IOException e) {
            model.addAttribute("postId", postId);
            e.printStackTrace();
            return "error/emptyFileError"; //오류 페이지 이동
        }

        return "redirect:/posts/" + postId + "/edit";
    }

    @MySecured(role = Role.GUEST)
    @DeleteMapping("/posts/{postId}")
    public String deleteFile(@PathVariable Long postId, Model model) {

        log.info("첨부파일 삭제");

        File file = null;

        try {
            PostDto postDto = postsService.getPost(postId); //현재 게시글 조회
            FileDto fileDto = fileService.getFile(postDto.getFileId()); //현재 게시글의 파일 조회
            file = new File(fileDto.getFilePath());
            if (file.delete()) { //서버로 업로드된 파일 삭제
                log.info("파일 삭제 성공");
            } else {
                log.info("파일 삭제 실패");
            }
            //삭제된 파일명 출력
            String originFileName = fileDto.getOriginFilename();
            log.info("removed file = {}", originFileName);
            //DB File 제거
            fileService.delete(fileDto.getId());
            //DB Posts.fileId 제거
            postDto.setFileId(null);
            Posts post = postsService.updateFile(postId, postDto);
            //갱신한 Posts 뷰로 전달
            model.addAttribute("post", post);
            model.addAttribute("bookList", bookService.books());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "redirect:/posts/" + postId;

    }

}