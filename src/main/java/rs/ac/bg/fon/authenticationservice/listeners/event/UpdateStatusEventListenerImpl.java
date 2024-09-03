package rs.ac.bg.fon.authenticationservice.listeners.event;

import rs.ac.bg.fon.authenticationservice.config.RabbitMQConfig;
import rs.ac.bg.fon.authenticationservice.dto.request.RegistrationEmailRequestDto;
import rs.ac.bg.fon.authenticationservice.rabbitmq.RabbitMQMessageProducer;
import rs.ac.bg.fon.authenticationservice.repository.entity.AccountEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.event.spi.PostUpdateEvent;
import org.hibernate.event.spi.PostUpdateEventListener;
import org.hibernate.persister.entity.EntityPersister;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UpdateStatusEventListenerImpl implements PostUpdateEventListener {

    private final RabbitMQMessageProducer producer;
    private final RabbitMQConfig rabbitMQConfig;

    @Override
    public void onPostUpdate(PostUpdateEvent postUpdateEvent) {
        AccountEntity account = (AccountEntity) postUpdateEvent.getEntity();

        int validColumnIndex = postUpdateEvent.getPersister().getPropertyIndex("valid");

        if (validColumnIndex != -1) {
            Object currentState = postUpdateEvent.getState()[validColumnIndex];
            Object previousState = postUpdateEvent.getOldState()[validColumnIndex];

            if (!currentState.equals(previousState)) {
                producer.publish(
                        new RegistrationEmailRequestDto(account.getEmail()),
                        rabbitMQConfig.getExchange(),
                        rabbitMQConfig.getRegistrationRoutingKey());
                log.info("Registration email is sent");
            }
        }
    }

    @Override
    public boolean requiresPostCommitHandling(EntityPersister entityPersister) {
        return false;
    }
}
