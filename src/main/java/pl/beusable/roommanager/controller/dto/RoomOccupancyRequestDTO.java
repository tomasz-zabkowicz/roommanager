package pl.beusable.roommanager.controller.dto;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoomOccupancyRequestDTO {

  @NotNull(message = "Number of premium rooms available is required.")
  private Integer premiumRoomsAvailable;

  @NotNull(message = "Number of economy rooms available is required.")
  private Integer economyRoomsAvailable;

  @NotNull(message = "List of prices guests are willing to pay is required.")
  private List<BigDecimal> guestPrices;
}
