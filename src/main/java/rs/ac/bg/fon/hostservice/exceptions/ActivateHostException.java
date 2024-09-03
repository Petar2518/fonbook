package rs.ac.bg.fon.hostservice.exceptions;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@EqualsAndHashCode(callSuper = true)
@ResponseStatus(HttpStatus.NOT_FOUND)
@Getter
@Setter
public class ActivateHostException extends HttpStatusException{
    private String resourceName;


    public ActivateHostException(Long id) {
        super(String.format("Host with id %S could not be activated'", id), HttpStatus.NOT_FOUND);
        this.resourceName = resourceName;

    }
}
