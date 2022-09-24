package com.rootable.libraryservice2022.service;

import com.rootable.libraryservice2022.domain.File;
import com.rootable.libraryservice2022.repository.FileRepository;
import com.rootable.libraryservice2022.web.dto.FileDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FileService {

    private final FileRepository fileRepository;

    /*
     * 파일 저장
     * */
    @Transactional
    public Long saveFile(FileDto fileDto) {
        return fileRepository.save(fileDto.toEntity()).getId();
    }

    /*
     * 저장한 파일 조회
     * */
    @Transactional
    public FileDto getFile(Long id) {

        File file = fileRepository.findById(id).get();

        FileDto fileDto = FileDto.builder()
                .id(id)
                .originFilename(file.getOriginFilename())
                .filename(file.getFilename())
                .filePath(file.getFilePath())
                .build();

        return fileDto;

    }

}
