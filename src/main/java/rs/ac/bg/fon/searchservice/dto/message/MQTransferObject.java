package rs.ac.bg.fon.searchservice.dto.message;

import rs.ac.bg.fon.searchservice.util.MessageEventType;
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
    MessageEventType eventType;
    String entityType;
    T message;
}
