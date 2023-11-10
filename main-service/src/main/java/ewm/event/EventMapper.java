package ewm.event;

import ewm.category.Category;
import ewm.category.CategoryMapper;
import ewm.event.dto.EventInputDto;
import ewm.event.dto.EventOutputDto;
import ewm.event.dto.Location;
import ewm.user.User;
import ewm.user.UserMapper;

public class EventMapper {

    public static EventOutputDto toDto(Event event) {
        return EventOutputDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(CategoryMapper.toDto(event.getCategory()))
                .initiator(UserMapper.toShortDto(event.getInitiator()))
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .location(Location.builder()
                        .lat(event.getLatitude())
                        .lon(event.getLongitude())
                        .build())
                .paid(event.isPaid())
                .participantLimit(event.getParticipantLimit())
                .requestModeration(event.isRequestModeration())
                .title(event.getTitle())
                .state(event.getState())
                .createdOn(event.getCreatedOn())
                .publishedOn(event.getPublishedTime())
                .build();
    }

    public static EventOutputDto toDto(Event event, Long confirmedCount, Long hits) {
        return EventOutputDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(CategoryMapper.toDto(event.getCategory()))
                .initiator(UserMapper.toShortDto(event.getInitiator()))
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .location(Location.builder()
                        .lat(event.getLatitude())
                        .lon(event.getLongitude())
                        .build())
                .paid(event.isPaid())
                .participantLimit(event.getParticipantLimit())
                .requestModeration(event.isRequestModeration())
                .title(event.getTitle())
                .state(event.getState())
                .createdOn(event.getCreatedOn())
                .publishedOn(event.getPublishedTime())
                .confirmedRequests(confirmedCount == null ? 0 : confirmedCount)
                .views(hits == null ? 0 : hits)
                .build();
    }

    public static Event toEntity(EventInputDto eventInputDto, Category category, User initiator, EventState state) {
        return Event.builder()
                .annotation(eventInputDto.getAnnotation())
                .category(category)
                .initiator(initiator)
                .description(eventInputDto.getDescription())
                .eventDate(eventInputDto.getEventDate())
                .latitude(eventInputDto.getLocation().getLat())
                .longitude(eventInputDto.getLocation().getLon())
                .paid(eventInputDto.getPaid())
                .participantLimit(eventInputDto.getParticipantLimit())
                .requestModeration(eventInputDto.getRequestModeration())
                .title(eventInputDto.getTitle())
                .state(state)
                .build();
    }
}
