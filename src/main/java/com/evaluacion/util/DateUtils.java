package com.evaluacion.util;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public class DateUtils {
    private final static ZoneOffset DEFAULT_OFFSET = ZoneOffset.of("-03:00");



    public static OffsetDateTime toOffsetDateTime (LocalDateTime fecha){
        return fecha != null ? OffsetDateTime.of(fecha,DEFAULT_OFFSET): null;
    }
}
