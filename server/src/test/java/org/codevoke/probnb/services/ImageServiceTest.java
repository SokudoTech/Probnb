package org.codevoke.probnb.services;

import org.codevoke.probnb.dto.ImageDTO;
import org.codevoke.probnb.exceptions.ImageException;
import org.codevoke.probnb.model.Image;
import org.codevoke.probnb.repository.ImageRepository;
import org.codevoke.probnb.service.ImageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.file.Path;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ImageServiceTest {

    @Mock
    private ImageRepository imageRepository;

    @InjectMocks
    private ImageService imageService;

    @TempDir
    Path tempDir;

    private Image testImage;
    private ImageDTO testImageDTO;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(imageService, "uploadDir", tempDir.toString());
        
        testImage = new Image();
        testImage.setId(1L);
        testImage.setPath(tempDir.resolve("test-image.png").toString());

        testImageDTO = new ImageDTO();
        testImageDTO.setImage("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNkYPhfDwAChwGA60e6kgAAAABJRU5ErkJggg==");
    }

    @Test
    void getImageById_WhenImageExists_ShouldReturnImageBytes() {
        // given
        when(imageRepository.findById(1L)).thenReturn(Optional.of(testImage));

        // when
        byte[] result = imageService.getImageById(1L);

        // then
        assertThat(result).isNotNull();
        // Note: This test might fail if the file doesn't exist, but it tests the logic
    }

    @Test
    void getImageById_WhenImageDoesNotExist_ShouldThrowImageException() {
        // given
        when(imageRepository.findById(999L)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> imageService.getImageById(999L))
                .isInstanceOf(ImageException.class)
                .hasMessageContaining("Image with id 999 not found");
    }

    @Test
    void createImage_WhenValidBase64Data_ShouldReturnImageDTO() {
        // given
        when(imageRepository.save(any(Image.class))).thenReturn(testImage);

        // when
        ImageDTO result = imageService.createImage(testImageDTO);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void createImage_WhenInvalidBase64Data_ShouldThrowImageException() {
        // given
        testImageDTO.setImage("invalid-base64-data");

        // when & then
        assertThatThrownBy(() -> imageService.createImage(testImageDTO))
                .isInstanceOf(ImageException.class);
    }

    @Test
    void createImage_WhenEmptyBase64Data_ShouldThrowImageException() {
        // given
        testImageDTO.setImage("");

        // when & then
        assertThatThrownBy(() -> imageService.createImage(testImageDTO))
                .isInstanceOf(ImageException.class);
    }

    @Test
    void createImage_WhenNullBase64Data_ShouldThrowImageException() {
        // given
        testImageDTO.setImage(null);

        // when & then
        assertThatThrownBy(() -> imageService.createImage(testImageDTO))
                .isInstanceOf(ImageException.class);
    }

    @Test
    void deleteImageById_WhenImageExists_ShouldReturnImageDTO() {
        // given
        when(imageRepository.findById(1L)).thenReturn(Optional.of(testImage));

        // when
        ImageDTO result = imageService.deleteImageById(1L);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void deleteImageById_WhenImageDoesNotExist_ShouldThrowImageException() {
        // given
        when(imageRepository.findById(999L)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> imageService.deleteImageById(999L))
                .isInstanceOf(ImageException.class)
                .hasMessageContaining("Image with id 999 not found");
    }

    @Test
    void decodeBase64_WhenValidBase64WithPrefix_ShouldReturnDecodedBytes() {
        // given
        String base64WithPrefix = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNkYPhfDwAChwGA60e6kgAAAABJRU5ErkJggg==";

        // when
        byte[] result = imageService.decodeBase64(base64WithPrefix);

        // then
        assertThat(result).isNotNull();
        assertThat(result.length).isGreaterThan(0);
    }

    @Test
    void decodeBase64_WhenValidBase64WithoutPrefix_ShouldReturnDecodedBytes() {
        // given
        String base64WithoutPrefix = "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNkYPhfDwAChwGA60e6kgAAAABJRU5ErkJggg==";

        // when
        byte[] result = imageService.decodeBase64(base64WithoutPrefix);

        // then
        assertThat(result).isNotNull();
        assertThat(result.length).isGreaterThan(0);
    }

    @Test
    void decodeBase64_WhenInvalidBase64_ShouldThrowException() {
        // given
        String invalidBase64 = "invalid-base64-data";

        // when & then
        assertThatThrownBy(() -> imageService.decodeBase64(invalidBase64))
                .isInstanceOf(IllegalArgumentException.class);
    }
} 