package rs.ac.bg.fon.accommodationservice.dto.message;

import lombok.*;

import java.io.Serializable;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IdMessageDto implements Serializable {
    Long id;
}
