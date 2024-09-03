package rs.ac.bg.fon.searchservice.rabbitmq.eventHandlers;

import rs.ac.bg.fon.searchservice.domain.AccommodationDomain;
import rs.ac.bg.fon.searchservice.dto.message.AccommodationMessageDto;
import rs.ac.bg.fon.searchservice.dto.message.IdMessageDto;

public interface DataChangedMessageEventHandler<T> {

    default void handleInsertAndUpdate(Object message) {
        handleGenericInsertAndUpdate(getEntityType().cast(message));
    }

    void handleGenericInsertAndUpdate(T messageDto);

//    default void handleUpdate(Object message) {
//        handleGenericUpdate(getEntityType().cast(message));
//    }

//    void handleGenericUpdate(T messageDto);
    void handleDelete(IdMessageDto id);

    Class<T> getEntityType();
}
