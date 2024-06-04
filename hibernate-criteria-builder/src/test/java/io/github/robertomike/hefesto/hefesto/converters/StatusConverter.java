package io.github.robertomike.hefesto.hefesto.converters;


import io.github.robertomike.hefesto.enums.Status;
import jakarta.persistence.AttributeConverter;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StatusConverter implements AttributeConverter<Set<Status>, String> {

    @Override
    public String convertToDatabaseColumn(Set<Status> enums) {
        return enums.stream().map(Enum::name).collect(Collectors.joining(","));
    }

    @Override
    public Set<Status> convertToEntityAttribute(String value) {
        return Stream.of(value.split(",")).map(Status::valueOf).collect(Collectors.toSet());
    }
}
