# sh-be-code-challenge

## Instructions

Clone this repository and run `docker-compose up -d` to bring up both the `db` and the `api-server` services, the latter of which is the functional solution to this test.  Any viable REST client can be used to test the solution against the requirements.

NOTE that the app will try for ten seconds to connect to a DB at start-up, and he DB is necessary for running the tests.

### Tests

Run `make run-tests` in the root directory.

If you want to run tests inside of Intellij, make sure to run the DB using `docker-compose start db`

## API Doc

### Get rates

#### Request

`GET /rates`

    http://localhost:5000/rates

#### Response

    HTTP/1.1 200 OK
    Content-Type: application/json

    {
       "rates":[
          {
             "days":"mon,tues,thurs",
             "times":"0900-2100",
             "tz":"America/Chicago",
             "price":1500
          },
          {
             "days":"sun,tues",
             "times":"0100-0700",
             "tz":"America/Chicago",
             "price":925
          },
          {
             "days":"mon,wed,sat",
             "times":"0100-0500",
             "tz":"America/Chicago",
             "price":1000
          },
          {
             "days":"fri,sat,sun",
             "times":"0900-2100",
             "tz":"America/Chicago",
             "price":2000
          },
          {
             "days":"wed",
             "times":"0600-1800",
             "tz":"America/Chicago",
             "price":1750
          }
       ]
    }

### Put rates

#### Request

`PUT /rates`

    http://localhost:5000/rates

    {
       "rates":[
          {
             "days":"mon,tues,thurs",
             "times":"0900-2100",
             "tz":"America/Chicago",
             "price":1500
          },
          {
             "days":"sun,tues",
             "times":"0100-0700",
             "tz":"America/Chicago",
             "price":925
          },
          {
             "days":"mon,wed,sat",
             "times":"0100-0500",
             "tz":"America/Chicago",
             "price":1000
          },
       ]
    }
    

#### Response

    HTTP/1.1 200 OK
    Content-Type: application/json

    {}

### Get price

`GET /price`

    http://localhost:5000/price?start=2022-09-10T10:00:00-05:00&end=2022-09-10T12:43:00-05:00

#### Response

    HTTP/1.1 200 OK
    Content-Type: application/json

    {
        "price": 2000
    }

## Notes

This could certainly be improved.  I'm an early learner of Kotlin so I'm not sure how "correct" or idiomatic this code is.
