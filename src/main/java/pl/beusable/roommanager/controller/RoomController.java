package pl.beusable.roommanager.controller;

import static org.springframework.http.HttpStatus.OK;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.beusable.roommanager.controller.dto.RoomOccupancyRequestDTO;
import pl.beusable.roommanager.domain.dto.RoomTypeUsageRecord;
import pl.beusable.roommanager.service.RoomOccupancyAnalyzer;

@RequestMapping("rooms")
@RestController
public class RoomController {

  private final RoomOccupancyAnalyzer roomOccupancyAnalyzer;

  public RoomController(RoomOccupancyAnalyzer roomOccupancyAnalyzer) {
    this.roomOccupancyAnalyzer = roomOccupancyAnalyzer;
  }

  @PostMapping(path = "/analyze/occupancy")
  public ResponseEntity<List<RoomTypeUsageRecord>> analyzeOccupancy(@Valid @RequestBody RoomOccupancyRequestDTO roomOccupancyRequestDTO) {
    final Integer premiumRoomsAvailable = roomOccupancyRequestDTO.getPremiumRoomsAvailable();
    final Integer economyRoomsAvailable = roomOccupancyRequestDTO.getEconomyRoomsAvailable();
    final List<BigDecimal> guestPrices = roomOccupancyRequestDTO.getGuestPrices();
    return new ResponseEntity<>(roomOccupancyAnalyzer.analyzePerRoomType(premiumRoomsAvailable, economyRoomsAvailable, guestPrices), OK);
  }
}
