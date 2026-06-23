package ru.practicum.explore_with_me.event.mapper;

import ru.practicum.explore_with_me.event.dto.LocationDto;
import ru.practicum.explore_with_me.event.model.Location;

public class LocationMapper {
    public static Location toLocation(LocationDto locationDto) {
        return new Location(locationDto.lat(), locationDto.lon());
    }


    public static LocationDto toLocationDto(Location location) {
        return new LocationDto(location.getLat(), location.getLon());
    }
}
