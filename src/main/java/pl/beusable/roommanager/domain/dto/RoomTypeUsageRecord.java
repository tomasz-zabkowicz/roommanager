package pl.beusable.roommanager.domain.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import java.math.BigDecimal;
import pl.beusable.roommanager.domain.RoomType;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record RoomTypeUsageRecord(RoomType roomType, Integer occupiedRoomsCount, BigDecimal income) {

}
