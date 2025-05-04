package kz.jarkyn.backend.global.controller;

import kz.jarkyn.backend.core.model.dto.IdDto;
import kz.jarkyn.backend.core.model.dto.ImmutableIdDto;
import kz.jarkyn.backend.global.model.dto.FileDto;
import kz.jarkyn.backend.global.model.dto.ImageResponse;
import kz.jarkyn.backend.global.service.FileService;
import kz.jarkyn.backend.global.service.ImageService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/file")
public class FileController {

    private final FileService fileService;
    private final ImageService imageService;

    public FileController(
            FileService fileService,
            ImageService imageService) {
        this.fileService = fileService;
        this.imageService = imageService;
    }

    @PostMapping("/upload")
    public IdDto upload(@RequestParam("payload") MultipartFile payload) {
        FileDto file = toFileDto(payload);
        UUID id = fileService.upload(file);
        return ImmutableIdDto.of(id);
    }

    @PostMapping("/uploadImage")
    public ImageResponse uploadImage(@RequestParam("payload") MultipartFile payload) {
        FileDto file = toFileDto(payload);
        return imageService.upload(file);
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> downloadFile(@PathVariable UUID id) {
        FileDto file = fileService.downloadFile(id);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(file.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, file.getContentDisposition())
                .contentLength(file.getSize())
                .body(new InputStreamResource(file.getInputStream()));
    }

    private FileDto toFileDto(MultipartFile multipartFile) {
        try {
            return new FileDto(
                    multipartFile.getInputStream(),
                    "attachment; filename=\"" + multipartFile.getOriginalFilename() + "\"",
                    Optional.ofNullable(multipartFile.getContentType()).orElse("application/octet-stream"),
                    multipartFile.getSize()
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}