package kz.jarkyn.backend.global.service;

import kz.jarkyn.backend.global.mapper.ImageMapper;
import kz.jarkyn.backend.global.model.ImageEntity;
import kz.jarkyn.backend.global.model.dto.FileDto;
import kz.jarkyn.backend.global.model.dto.ImageResponse;
import kz.jarkyn.backend.global.repository.ImageRepository;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.http.ContentDisposition;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;

@Service
public class ImageService {
    private final FileService fileService;
    private final ImageRepository imageRepository;
    private final ImageMapper imageMapper;

    public ImageService(
            FileService fileService,
            ImageRepository imageRepository,
            ImageMapper imageMapper) {
        this.fileService = fileService;
        this.imageRepository = imageRepository;
        this.imageMapper = imageMapper;
    }

    @Transactional
    public ImageResponse upload(FileDto file) {
        try {
            byte[] bytes = file.getInputStream().readAllBytes();
            BufferedImage originalImage = ImageIO.read(new ByteArrayInputStream(bytes));
            FileDto originalFile = new FileDto(new ByteArrayInputStream(bytes),
                    file.getContentDisposition(), file.getContentType(), bytes.length);
            UUID originalId = fileService.upload(originalFile);

            BufferedImage mediumImage = Thumbnails.of(originalImage).size(128, 128).asBufferedImage();
            FileDto mediumFile = toFileDto(mediumImage, originalFile);
            UUID mediumId = fileService.upload(mediumFile);

            BufferedImage thumbnailImage = Thumbnails.of(originalImage).size(24, 24).asBufferedImage();
            FileDto thumbnailDto = toFileDto(thumbnailImage, originalFile);
            UUID thumbnailId = fileService.upload(thumbnailDto);

            ContentDisposition disposition = ContentDisposition.parse(file.getContentDisposition());
            String filename = disposition.getFilename();

            ImageEntity image = new ImageEntity();
            image.setOriginalFileId(originalId);
            image.setMediumFileId(mediumId);
            image.setThumbnailFileId(thumbnailId);
            imageRepository.save(image);
            return imageMapper.toResponse(image);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private FileDto toFileDto(BufferedImage image, FileDto file) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            String formatName = switch (file.getContentType()) {
                case "image/jpeg" -> "jpg";
                case "image/png" -> "png";
                default -> throw new IllegalArgumentException("Unsupported format: " + file.getContentType());
            };
            ImageIO.write(image, formatName, byteArrayOutputStream);
            byte[] bytes = byteArrayOutputStream.toByteArray();
            return new FileDto(new ByteArrayInputStream(bytes),
                    file.getContentDisposition(), file.getContentType(), bytes.length
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}