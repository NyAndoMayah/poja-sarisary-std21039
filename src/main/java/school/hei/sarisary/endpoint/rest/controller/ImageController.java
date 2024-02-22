package school.hei.sarisary.endpoint.rest.controller;

import java.io.IOException;
import lombok.AllArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import school.hei.sarisary.endpoint.rest.model.RestImage;
import school.hei.sarisary.service.ImageService;

@RestController
@AllArgsConstructor
public class ImageController {
  private ImageService imageService;

  @PutMapping(value = "/blacks/{id}", produces = MediaType.IMAGE_JPEG_VALUE)
  public ResponseEntity<byte[]> convertToBlackAndWhite(
      @PathVariable String id, @RequestBody byte[] coloredImage) throws IOException {
    ByteArrayResource resource = new ByteArrayResource(coloredImage);
    MultipartFile multipartFile =
        new MockMultipartFile(
            "file", "image.jpg", MediaType.IMAGE_JPEG_VALUE, resource.getInputStream());
    byte[] bytes = imageService.processImage(id, multipartFile);
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.IMAGE_JPEG);

    return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
  }

  @GetMapping(value = "/blacks/{id}")
  public RestImage getImagesById(@PathVariable String id) {
    return imageService.getImageById(id);
  }
}
