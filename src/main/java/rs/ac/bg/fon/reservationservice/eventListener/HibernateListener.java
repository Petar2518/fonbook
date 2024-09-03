package rs.ac.bg.fon.reservationservice.eventListener;

import rs.ac.bg.fon.reservationservice.dto.message.ReservationEmailMessage;
import rs.ac.bg.fon.reservationservice.eventListener.eventHandlers.MessageQueueHandler;
import rs.ac.bg.fon.reservationservice.mapper.ReservationMapper;
import rs.ac.bg.fon.reservationservice.model.ProfileInfo;
import rs.ac.bg.fon.reservationservice.model.Reservation;
import rs.ac.bg.fon.reservationservice.util.JwtUtil;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostRemove;
import jakarta.persistence.PostUpdate;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@ConfigurationProperties
@Slf4j
public class HibernateListener {

    @Autowired
    MessageQueueHandler messageQueueHandler;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ReservationMapper reservationMapper;

    @PostPersist
    private void afterInsert(Reservation reservation) {
        if (request == null) {
            return;
        }
        ReservationEmailMessage reservationEmailMessage = getReservationEmailMessage(reservation);

        messageQueueHandler.sendMessage(reservation, "INSERT");
        messageQueueHandler.sendMessageToEmailService(reservationEmailMessage);
    }

    @PostUpdate
    private void afterUpdate(Reservation reservation) {
        messageQueueHandler.sendMessage(reservation, "UPDATE");
    }

    @PostRemove
    private void afterDelete(Reservation reservation) {
        messageQueueHandler.sendMessage(reservation, "DELETE");
    }

    private ReservationEmailMessage getReservationEmailMessage(Reservation reservation) {
        ProfileInfo profileInfo = jwtUtil.getFromJwt(request.getHeader(HttpHeaders.AUTHORIZATION));
        return reservationMapper.fromEntityToReservationEmailMessage(reservation, profileInfo.getSub());
    }
}
