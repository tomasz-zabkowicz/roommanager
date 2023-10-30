# Room Manager
Hotel room occupancy analyzer.

# Getting Started
The project was written using Java 20. It's possible it will run with older versions however at least Java 14 is required.

In order to run project from command line use:

```bash
$ mvn spring-boot:run
```

Once successfully started, application listens on http://localhost:9998/.

# Endpoints

- Room occupancy analyzer endpoint:
  ```
  POST http://localhost:9998/rooms/analyze/occupancy
  
  Expected JSON payload format:
  {
    "premiumRoomsAvailable" : 3,
    "economyRoomsAvailable" : 3,
    "guestPrices" : [23, 45, 155, 374, 22, 99.99, 100, 101, 115, 209]
  }
  
  Example of response:
  [
    {
        "roomType": "PREMIUM",
        "occupiedRoomsCount": 3,
        "income": 738
    },
    {
        "roomType": "ECONOMY",
        "occupiedRoomsCount": 3,
        "income": 167.99
    }
  ]
  ```

# Testing

Tests can be started from command line with:

```bash
$ mvn verify
```
