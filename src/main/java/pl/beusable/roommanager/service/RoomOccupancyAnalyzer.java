package pl.beusable.roommanager.service;

import static pl.beusable.roommanager.domain.RoomType.ECONOMY;
import static pl.beusable.roommanager.domain.RoomType.PREMIUM;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pl.beusable.roommanager.domain.RoomType;
import pl.beusable.roommanager.domain.dto.RoomTypeUsageRecord;

@Service
public class RoomOccupancyAnalyzer {

  @Value("${room-manager.occupancy.premium-guest.min-price:#{100.0}}")
  private BigDecimal premiumGuestMinPrice;

  public List<RoomTypeUsageRecord> analyzePerRoomType(int premiumRoomsAvailable, int economyRoomsAvailable, List<BigDecimal> guestPrices) {
    final Map<RoomType, List<BigDecimal>> guestPricesPerRoomType = buildGuestPricesPerRoomTypeMap(guestPrices, premiumRoomsAvailable,
        economyRoomsAvailable);

    final List<BigDecimal> premiumGuestPrices = guestPricesPerRoomType.get(PREMIUM);
    final List<BigDecimal> economyGuestPrices = guestPricesPerRoomType.get(ECONOMY);
    final RoomTypeUsageRecord premiumRoomsUsage = calculateRoomTypeUsage(PREMIUM, premiumRoomsAvailable, premiumGuestPrices);
    final RoomTypeUsageRecord economyRoomsUsage = calculateRoomTypeUsage(ECONOMY, economyRoomsAvailable, economyGuestPrices);
    return Arrays.asList(premiumRoomsUsage, economyRoomsUsage);
  }

  /**
   * Builds a map of room types assigning to each one guests based on price they are willing to pay. In case there is no enough economy
   * rooms to accommodate all economy guests, those of them willing to pay the highest price are upgraded to premium room according to
   * premium rooms availability.
   *
   * @param guestPrices           guest prices
   * @param premiumRoomsAvailable number of premium rooms available
   * @param economyRoomsAvailable number of economy rooms available
   * @return a map of room types with declared guest prices assigned to each
   */
  private Map<RoomType, List<BigDecimal>> buildGuestPricesPerRoomTypeMap(List<BigDecimal> guestPrices, int premiumRoomsAvailable,
      int economyRoomsAvailable) {
    final List<BigDecimal> premiumGuestPrices = extractGuestPricesForRoomTypeSorted(guestPrices, PREMIUM);
    final List<BigDecimal> economyGuestPrices = extractGuestPricesForRoomTypeSorted(guestPrices, ECONOMY);

    final int economyGuestsExcess = Math.max(0, economyGuestPrices.size() - economyRoomsAvailable);
    final int remainingPremiumRooms = Math.max(0, premiumRoomsAvailable - premiumGuestPrices.size());
    final int premiumRoomsForEconomyGuests = Math.min(economyGuestsExcess, remainingPremiumRooms);
    final List<BigDecimal> economyGuestPricesQualifyingForPremium = economyGuestPrices.subList(0, premiumRoomsForEconomyGuests);
    premiumGuestPrices.addAll(economyGuestPricesQualifyingForPremium);
    economyGuestPrices.removeAll(economyGuestPricesQualifyingForPremium);

    return Map.of(PREMIUM, premiumGuestPrices, ECONOMY, economyGuestPrices);
  }

  private RoomTypeUsageRecord calculateRoomTypeUsage(RoomType roomType, int roomsAvailable, List<BigDecimal> guestPrices) {
    final List<BigDecimal> guestsWithRooms = guestPrices.stream().limit(roomsAvailable).toList();
    final int roomsOccupied = guestsWithRooms.size();
    final BigDecimal totalIncome = guestsWithRooms.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
    return new RoomTypeUsageRecord(roomType, roomsOccupied, totalIncome);
  }

  private List<BigDecimal> extractGuestPricesForRoomTypeSorted(List<BigDecimal> guestPrices, RoomType roomType) {
    if (PREMIUM.equals(roomType)) {
      return guestPrices.stream().filter(guestPrice -> guestPrice.compareTo(premiumGuestMinPrice) >= 0).sorted(Comparator.reverseOrder())
          .collect(Collectors.toList());
    }
    return guestPrices.stream().filter(guestPrice -> guestPrice.compareTo(premiumGuestMinPrice) < 0).sorted(Comparator.reverseOrder())
        .collect(Collectors.toList());
  }
}
