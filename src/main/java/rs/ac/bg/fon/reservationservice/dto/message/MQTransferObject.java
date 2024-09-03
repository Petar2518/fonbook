package rs.ac.bg.fon.reservationservice.dto.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MQTransferObject<T> implements Serializable {
    String eventType;
    String entityType;
    T message;


}
