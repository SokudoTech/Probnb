package org.codevoke.probnb.service;

import org.codevoke.probnb.dto.ImageDTO;
import org.codevoke.probnb.exceptions.ImageException;
import org.codevoke.probnb.model.Image;
import org.codevoke.probnb.repository.ImageRepository;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.*;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ImageService {
    @Value("${image.upload.dir:uploads}")
    private String uploadDir;
    private static final String IMAGE_FORMAT = "png";
    private static final String ENDPOINT = "/api/images/";

    private final ImageRepository imageRepository;
    private final Logger logger = LoggerFactory.getLogger(ImageService .class);

    public byte[] getImageById(Long id) {
        Image image = getImageEntityById(id);
        try {
            return Files.readAllBytes(Path.of(image.getPath()));
        } catch (IOException e) {
            throw ImageException.IOException();
        }
    }

    protected Image getImageEntityById(Long id) {
        return imageRepository.findById(id)
                .orElseThrow(() -> ImageException.ImageNotFound(id));
    }

    public ImageDTO createImage(ImageDTO imageDTO) {
        try {
            // decode and save on disk
            logger.info("decoding and saving image");
            byte[] imageBytes = decodeBase64(imageDTO.getImage());
            String path = saveImage(imageBytes, Paths.get(uploadDir));

            Image image = new Image();
            image.setPath(path);
            imageRepository.save(image);
            return convertToDTO(image);
        } catch (IllegalArgumentException e) {      // parsing base64 error
            logger.error(e.getLocalizedMessage());
            throw ImageException.InvalidFormat();
        } catch (IOException e) {                   // saving error
            logger.error(e.getLocalizedMessage());
            throw ImageException.IOException();
        }
    }

    public ImageDTO deleteImageById(Long id) {
        Image image = getImageEntityById(id);
        try {
            Files.delete(Path.of(image.getPath()));
            return convertToDTO(image);
        } catch (IOException e) {
            throw ImageException.IOException();
        }
    }

    private byte[] decodeBase64(String base64encoded) {
        String[] parts = base64encoded.split(",");
        if (parts.length > 1)
            base64encoded = parts[1];

        return Base64.getDecoder().decode(base64encoded);
    }

    public String saveImage(byte[] image, Path savePath) throws IOException {
        String filename = UUID.randomUUID() + "." + IMAGE_FORMAT;
        Path filePath = savePath.resolve(filename);

        InputStream imageBytesStream = new ByteArrayInputStream(image);
        BufferedImage imageBuffer = ImageIO.read(imageBytesStream);

        if (!Files.exists(savePath))
            Files.createDirectories(savePath);

        ImageIO.write(imageBuffer, IMAGE_FORMAT, filePath.toFile());
        return filePath.toString();
    }

    private ImageDTO convertToDTO(Image image) {
        return ImageDTO.builder()
                .id(image.getId())
                .image(ENDPOINT + image.getId())
                .build();
    }
}
