package rs.ac.bg.fon.accommodationservice.eventListener;

import jakarta.persistence.PostPersist;
import jakarta.persistence.PostRemove;
import jakarta.persistence.PostUpdate;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import rs.ac.bg.fon.accommodationservice.eventListener.eventHandlers.MessageQueueHandler;

@Component
@Getter
@Setter
@ConfigurationProperties
@Slf4j
public class HibernateListener {

    @Autowired
    MessageQueueHandler messageQueueHandler;


    @PostPersist
    private void afterInsert(Object o) {
        String operation = "INSERT";
        messageQueueHandler.sendMessage(o, operation);
    }

    @PostUpdate
    private void afterUpdate(Object o) {
        String operation = "UPDATE";
        messageQueueHandler.sendMessage(o, operation);
    }

    @PostRemove
    private void afterDelete(Object o) {
        String operation = "DELETE";
        messageQueueHandler.sendMessage(o, operation);
    }


}
