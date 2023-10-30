package pl.beusable.roommanager.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.util.AssertionErrors.assertTrue;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import pl.beusable.roommanager.controller.dto.RoomOccupancyRequestDTO;

public class RoomControllerIT extends BaseRestIT {

  @SuppressWarnings("unchecked")
  @Test
  public void testExpectedOccupancyInfoReturned() throws JsonProcessingException {
    final List<BigDecimal> guestPrices = List.of(new BigDecimal(23), new BigDecimal(45), new BigDecimal(155), new BigDecimal(374),
        new BigDecimal(22), new BigDecimal("99.99"), new BigDecimal(100), new BigDecimal(101), new BigDecimal(115), new BigDecimal(209));

    RoomOccupancyRequestDTO roomOccupancyRequestDTO = new RoomOccupancyRequestDTO(3, 3, guestPrices);
    List<Map<String, Object>> response = (List<Map<String, Object>>) post("rooms/analyze/occupancy", toJSONString(roomOccupancyRequestDTO));
    validateOccupancyResponse(response, 3, 738d, 3, 167.99d);

    roomOccupancyRequestDTO = new RoomOccupancyRequestDTO(7, 5, guestPrices);
    response = (List<Map<String, Object>>) post("rooms/analyze/occupancy", toJSONString(roomOccupancyRequestDTO));
    validateOccupancyResponse(response, 6, 1054d, 4, 189.99d);

    roomOccupancyRequestDTO = new RoomOccupancyRequestDTO(2, 7, guestPrices);
    response = (List<Map<String, Object>>) post("rooms/analyze/occupancy", toJSONString(roomOccupancyRequestDTO));
    validateOccupancyResponse(response, 2, 583d, 4, 189.99d);

    roomOccupancyRequestDTO = new RoomOccupancyRequestDTO(7, 1, guestPrices);
    response = (List<Map<String, Object>>) post("rooms/analyze/occupancy", toJSONString(roomOccupancyRequestDTO));
    validateOccupancyResponse(response, 7, 1153.99d, 1, 45d);
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testValidationErrorsReturnedByOccupancyEndpointWhenPayloadIncorrect() throws JsonProcessingException {
    final RoomOccupancyRequestDTO roomOccupancyRequestDTO = new RoomOccupancyRequestDTO();
    final Map<String, List<String>> response = (Map<String, List<String>>) post("rooms/analyze/occupancy",
        toJSONString(roomOccupancyRequestDTO), HttpStatus.BAD_REQUEST);
    final List<String> errors = response.get("errors");
    assertTrue("'premiumRoomsAvailable' field validation missing!", errors.contains("Number of premium rooms available is required."));
    assertTrue("'economyRoomsAvailable' field validation missing!", errors.contains("Number of economy rooms available is required."));
    assertTrue("'guestPrices' field validation missing!", errors.contains("List of prices guests are willing to pay is required."));
  }

  private void validateOccupancyResponse(List<Map<String, Object>> response, Integer expectedPremiumOccupancy, Double expectedPremiumIncome,
      Integer expectedEconomyOccupancy, Double expectedEconomyIncome) {
    final Map<String, Object> premiumInfo = response.get(0);
    assertEquals(expectedPremiumOccupancy, premiumInfo.get("occupiedRoomsCount"), "Premium occupancy different than expected!");
    assertEquals(0, expectedPremiumIncome.compareTo(Double.parseDouble(premiumInfo.get("income").toString())),
        "Premium income different than expected!");

    final Map<String, Object> economyInfo = response.get(1);
    assertEquals(expectedEconomyOccupancy, economyInfo.get("occupiedRoomsCount"), "Economy occupancy different than expected!");
    assertEquals(0, expectedEconomyIncome.compareTo(Double.parseDouble(economyInfo.get("income").toString())),
        "Economy income different than expected!");
  }
}
