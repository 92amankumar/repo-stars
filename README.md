## Repository APIs

### Popular Repositories API

#### Local Setup

* To run application
`make run
`

* To run test cases
`make test
`

#### Testing

* For documentation or usage of API, refer to, swagger URL
``http://localhost:8080``

or cURL

``curl -X 'GET' \
  'http://localhost:8080/api/repositories/popular?numberOfResults=10&fromDate=2022-12-22&programmingLanguage=javascript&sortBy=STARS&orderAs=DESC' \
  -H 'accept: application/json'``

Note: We are using unauthenticated API from github which will
be [rate limited](https://docs.github.com/en/rest/search?apiVersion=2022-11-28#rate-limit) to 30 requests per minute.
More parameters can be added as per steps for drilling-down search mentioned
at [link](https://docs.github.com/en/rest/search?apiVersion=2022-11-28#constructing-a-search-query)

API is configured according to documentation
mentioned [here](https://docs.github.com/en/rest/search?apiVersion=2022-11-28#search-repositories)
