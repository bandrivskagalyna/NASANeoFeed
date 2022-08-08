# NASA NEO Feed Example

Project contains tests for Neo feed endpoint which retrieves a list of Asteroids based on their closest approach date to Earth.

`GET https://api.nasa.gov/neo/rest/v1/feed?start_date=START_DATE&end_date=END_DATE&api_key=API_KEY`

## Setup requirements

Java JDK 11 , Maven and optional IDE

## Installation and Run

Using Maven
```bash
mvn clean install
```

## Notes

- For demo purpose I wrote one test with RestAssured asserts and all others with TestNG asserts.

- Test NeoFeedTest.testStartDateAfterEndDate will fail due to actual bug (When start date and end date parameters are within 7 days limit but end date is before the start date - the API returns 200 result and in response those dates automatically switched to the right order, but I believe this is a bug because it silently alters the request and such error could not be traced). Test was ignored.

- Test NeoFeedTest.testAbsentStartDateParameter will fail due to bug (on my opinion , should be either valid response with start date= end date-7 or more specific error like 'Start date parameter is missing'. Need more clarification on this). Test was ignored

- Error response JSON structure is not the same for different errors (probably because they are from different services, but still it would be better if they follow similar structure). 

   Error response when API key is not valid
```bash
{
  "error": {
    "code": "API_KEY_INVALID",
    "message": "An invalid api_key was supplied. Get one at https://api.nasa.gov:443"
  }
}
```
   Error response when feed date limit is exceeded:
```bash
{
    "code": 400,
    "http_error": "BAD_REQUEST",
    "error_message": "Date Format Exception - Expected format (yyyy-mm-dd) - The Feed date limit is only 7 Days",
    "request": "http://www.neowsapp.com/rest/v1/feed?start_date=2015-09-10&end_date=2015-09-20"
}
```