package school.hei.sarisary.endpoint.rest.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@AllArgsConstructor
@Data
@Builder
@ToString
public class RestImage {
  private String id;
  private String original_url;
  private String transformed_url;
}
