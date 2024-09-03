package rs.ac.bg.fon.authenticationservice.listeners.event;

import rs.ac.bg.fon.authenticationservice.config.RabbitMQConfig;
import rs.ac.bg.fon.authenticationservice.dto.request.VerificationEmailRequestDto;
import rs.ac.bg.fon.authenticationservice.dto.response.ForgotPasswordResponseDto;
import rs.ac.bg.fon.authenticationservice.rabbitmq.RabbitMQMessageProducer;
import rs.ac.bg.fon.authenticationservice.repository.entity.AccountEntity;
import rs.ac.bg.fon.authenticationservice.repository.entity.PasswordResetTokenEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.event.spi.PostInsertEvent;
import org.hibernate.event.spi.PostInsertEventListener;
import org.hibernate.persister.entity.EntityPersister;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class InsertEventListenerImpl implements PostInsertEventListener {

    private final RabbitMQMessageProducer producer;
    private final RabbitMQConfig rabbitMQConfig;

    @Override
    public void onPostInsert(PostInsertEvent postInsertEvent) {
        if (postInsertEvent.getEntity() instanceof PasswordResetTokenEntity passwordResetToken) {
            producer.publish(
                    new ForgotPasswordResponseDto(passwordResetToken.getAccount().getEmail(), passwordResetToken.getId()),
                    rabbitMQConfig.getExchange(),
                    rabbitMQConfig.getForgotPasswordRoutingKey()
            );
            log.info("Forgot password email is sent");
        }
    }

    @Override
    public boolean requiresPostCommitHandling(EntityPersister entityPersister) {
        return true;
    }
}
