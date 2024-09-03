package rs.ac.bg.fon.reservationservice.exceptions;

public class MessageQueueException extends RuntimeException {

    public MessageQueueException(Exception e) {
        super("Exception sending message: " + e.getMessage());
    }
}
