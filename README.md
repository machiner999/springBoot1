# springBoot1

Spring Boot 3.5 REST API that returns the current gold price in JPY.

## Requirements

- Java 21+
- Gradle (or generate a Gradle wrapper)

## Data Sources

- Gold price: gold-api.com (USD per troy ounce)
- FX rate: exchangerate.host (USD -> JPY)

## Run

If you have Gradle installed:

```bash
gradle bootRun
```

If you want a wrapper first:

```bash
gradle wrapper
./gradlew bootRun
```

## Endpoint

`GET /api/gold/price`

Example response:

```json
{
  "metal": "XAU",
  "currency": "JPY",
  "priceJpyPerOunce": 310000.25,
  "priceJpyPerGram": 9967.58,
  "timestamp": "2026-02-06T03:12:45Z",
  "source": "gold-api.com + exchangerate.host"
}
```
