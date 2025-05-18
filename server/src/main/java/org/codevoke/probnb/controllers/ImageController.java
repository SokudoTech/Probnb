package org.codevoke.probnb.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.codevoke.probnb.annotations.JwtRequired;
import org.codevoke.probnb.dto.ImageDTO;
import org.codevoke.probnb.service.ImageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/images")
@RequiredArgsConstructor
public class ImageController {
    @Autowired
    private ImageService imageService;
    private static final Logger logger = LoggerFactory.getLogger(ImageController.class);

    @GetMapping(path = "/{id}", produces = MediaType.IMAGE_PNG_VALUE)
    public byte[] getImage(@PathVariable Long id) {
        logger.info("get image by id: {}", id);
        return imageService.getImageById(id);
    }

    @JwtRequired
    @PostMapping
    public ResponseEntity<ImageDTO> uploadImage(@Valid @RequestBody ImageDTO imageDTO) {
        logger.info("create new image");
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(imageService.createImage(imageDTO));
    }
}
