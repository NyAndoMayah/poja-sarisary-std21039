package school.hei.sarisary.service;

import jakarta.transaction.Transactional;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import javax.imageio.ImageIO;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import school.hei.sarisary.endpoint.rest.model.RestImage;
import school.hei.sarisary.file.BucketComponent;
import school.hei.sarisary.repository.ImageRepository;
import school.hei.sarisary.repository.model.Image;

@Service
@NoArgsConstructor
@AllArgsConstructor
public class ImageService {
  private BucketComponent bucketComponent;
  private ImageRepository imageRepository;
  private final Duration DURATION = Duration.ofMinutes(30);

  protected File convertToBlackAndWhite(File inputImage) {
    try {
      BufferedImage bufferedImage = ImageIO.read(inputImage);
      if (bufferedImage == null) {
        return null;
      } else {
        BufferedImage BWBufferedImage =
            new BufferedImage(
                bufferedImage.getWidth(), bufferedImage.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        BWBufferedImage.getGraphics().drawImage(bufferedImage, 0, 0, null);

        ByteArrayOutputStream byteOSImage = new ByteArrayOutputStream();
        ImageIO.write(BWBufferedImage, "jpeg", byteOSImage);
        byte[] bytes = byteOSImage.toByteArray();
        return Files.write(Path.of("/tmp/" + inputImage.getName()), bytes).toFile();
      }
    } catch (IOException e) {
      return null;
    }
  }

  @Transactional
  public byte[] processImage(String id, MultipartFile multipartFile) throws IOException {
    File originalImage = multipartFile.getResource().getFile();
    File modifiedImage = convertToBlackAndWhite(originalImage);

    Image imageToSave =
        Image.builder().id(id).filename(multipartFile.getOriginalFilename()).build();

    Image savedImage = imageRepository.save(imageToSave);

    String originalBucketKey = "original-" + savedImage.getFilename();
    String modifiedBucketKey = "modified-" + savedImage.getFilename();

    bucketComponent.upload(originalImage, originalBucketKey);
    bucketComponent.upload(modifiedImage, modifiedBucketKey);

    return Files.readAllBytes(Path.of(modifiedImage.getPath()));
  }

  public RestImage getImageById(String id) {
    Image image = imageRepository.findById(id).orElseThrow();

    String originalBucketKey = "original-" + image.getFilename();
    String modifiedBucketKey = "transformed-" + image.getFilename();

    RestImage restImage =
        RestImage.builder()
            .transformed_url(bucketComponent.presign(modifiedBucketKey, DURATION).toString())
            .original_url(bucketComponent.presign(originalBucketKey, DURATION).toString())
            .build();
    return restImage;
  }
}
