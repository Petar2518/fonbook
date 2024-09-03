package rs.ac.bg.fon.accommodationservice.exception.specific;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MessageQueueException extends RuntimeException{

    public MessageQueueException(Exception e) {
        super("Whoops! An error occurred on server side.",e);
    }
}