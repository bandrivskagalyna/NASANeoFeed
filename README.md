# NASA NEO Feed Example

Project contains test for the Neo feed endpoint which is retreiving a list of Asteroids based on their closest approach date to Earth. GET https://api.nasa.gov/neo/rest/v1/feed?start_date=START_DATE&end_date=END_DATE&api_key=API_KEY

## Setup requirements

Java JDK 11 , Maven and optional IDE

## Installation and Run

Using Maven
```bash
mvn clean install
```

## Note

- For demo purpose I wrote one test with RestAssured asserts and rest with TestNG asserts.

- Test NeoFeedTest.testStartDateAfterEndDate  will fail due to an actual bug (When start date and end date parameters are within 7 days limit but swiched places the API returns 200 result and in response those dates automatically swiched to be correct, but I thinj this is a bug because it alteres request and in case something heppens request cannot be traced). Test was ignored.


- Also Error response structure are not the same (probably because they are from different services, but still it would be better if they have similar structure. 

   Error response when API key is not valid
```bash
{
  "error": {
    "code": "API_KEY_INVALID",
    "message": "An invalid api_key was supplied. Get one at https://api.nasa.gov:443"
  }
}
```
   Error response when feed date limit exceeded:
```bash
{
    "code": 400,
    "http_error": "BAD_REQUEST",
    "error_message": "Date Format Exception - Expected format (yyyy-mm-dd) - The Feed date limit is only 7 Days",
    "request": "http://www.neowsapp.com/rest/v1/feed?start_date=2015-09-10&end_date=2015-09-20"
}
```

- Also I didn't add few test due to I didn't know expected behavior. But seems the results are not accurate:
1. Set start and end date parameters to future dates returns data results with future dates in it
Ex. https://api.nasa.gov/neo/rest/v1/feed?start_date=2090-08-20&end_date=2090-08-24&api_key=NW9PHugtaKeZ7O9QynbOpsz1zWspPClZ9v4ZQXcT

2. If start and end dates are not set than system automatically sets date from today plus 7 days:
Ex. request :
   https://api.nasa.gov/neo/rest/v1/feed?api_key=NW9PHugtaKeZ7O9QynbOpsz1zWspPClZ9v4ZQXcT
Auto generated request will be:
  http://www.neowsapp.com/rest/v1/feed?start_date=2022-08-07&end_date=2022-08-14&detailed=false&api_key=NW9PHugtaKeZ7O9QynbOpsz1zWspPClZ9v4ZQXcT
