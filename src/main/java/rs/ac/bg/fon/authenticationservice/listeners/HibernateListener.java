package rs.ac.bg.fon.authenticationservice.listeners;

import rs.ac.bg.fon.authenticationservice.listeners.event.InsertEventListenerImpl;
import rs.ac.bg.fon.authenticationservice.listeners.event.UpdateStatusEventListenerImpl;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.internal.SessionFactoryImpl;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HibernateListener {

    private final EntityManagerFactory entityManagerFactory;
    private final InsertEventListenerImpl insertEventListener;
    private final UpdateStatusEventListenerImpl updateStatusEventListener;


    @PostConstruct
    private void init() {
        SessionFactoryImpl sessionFactory = entityManagerFactory.unwrap(SessionFactoryImpl.class);

        EventListenerRegistry registry = sessionFactory.getServiceRegistry().getService(EventListenerRegistry.class);

        registry.getEventListenerGroup(EventType.POST_INSERT).appendListener(insertEventListener);
        registry.getEventListenerGroup(EventType.POST_UPDATE).appendListener(updateStatusEventListener);
    }
}
