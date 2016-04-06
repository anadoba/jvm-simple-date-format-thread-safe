# SimpleDateFormat made thread-safe

I want to show that SimpleDateFormat may likely fail when multiple threads use it simultaneously.
This can be easily fixed by - for example - ThreadLocal mechanism.

Project contains 2 unit cases:
- first one uses SimpleDateFormat and fails when it comes to concurrency...
- second one is thread-safe (it's passes the test under the same circumstances).

## running the tests
`mvn compile test`

Standard SimpleDateFormat fails by returning wrong date value or by throwing strange FormatExceptions.