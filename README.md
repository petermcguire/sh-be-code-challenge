# sh-be-code-challenge

## Instructions

Clone this repository and run `docker-compose up -d` to bring up both the `db` and the `api-server` services, the latter of which is the functional solution to this test.  Any viable REST client can be used to test the solution against the requirements.

NOTE that the app will try for ten seconds to connect to a DB at start-up, and he DB is necessary for running the tests.

### Tests

Run `make run-tests` in the root directory.

If you want to run tests inside of Intellij, make sure to run the DB using `docker-compose start db`

## API Doc

### Get list of Things

#### Request

`GET /thing/`

    curl -i -H 'Accept: application/json' http://localhost:7000/thing/

#### Response

    HTTP/1.1 200 OK
    Date: Thu, 24 Feb 2011 12:36:30 GMT
    Status: 200 OK
    Connection: close
    Content-Type: application/json
    Content-Length: 2

    []

## Notes

This could certainly be improved.  I'm an early learner of Kotlin so I'm not sure how "correct" or idiomatic this code is.
