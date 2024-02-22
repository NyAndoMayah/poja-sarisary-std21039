package school.hei.sarisary.endpoint.rest.controller;

import java.io.IOException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import school.hei.sarisary.endpoint.rest.model.RestImage;
import school.hei.sarisary.service.ImageService;

@RestController
@AllArgsConstructor
public class ImageController {
  private ImageService imageService;

  @PutMapping(value = "/blacks/{id}", produces = MediaType.IMAGE_JPEG_VALUE)
  public ResponseEntity<byte[]> convertToBlackAndWhite(
      @PathVariable String id, @RequestBody byte[] coloredImage) throws IOException {
    byte[] bytes = imageService.processImage(id, coloredImage);
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.IMAGE_JPEG);

    return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
  }

  @GetMapping(value = "/blacks/{id}")
  public RestImage getImagesById(@PathVariable String id) {
    return imageService.getImageById(id);
  }
}
