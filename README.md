
Let's imagine we have the following legacy service (`bar-service`) with the following endpoints. 

1. Success

```bash
http :8080/success

HTTP/1.1 200
Content-Type: application/json
Date: Thu, 17 Oct 2019 11:23:20 GMT
Transfer-Encoding: chunked

{
    "value": "hello"
}
```

2. Failure With Non 200 HTTP status code

```bash
http :8080/failure-with-status-non-200

HTTP/1.1 500
Connection: close
Content-Type: application/json
Date: Thu, 17 Oct 2019 11:25:12 GMT
Transfer-Encoding: chunked

{
    "message": "something went wrong again",
    "status": 500
}
```

3. Failure With 200 HTTP status code --> Bad API design, but a legacy service might do this

```bash
http :8081/failure-with-status-200

HTTP/1.1 200
Content-Type: application/json
Date: Thu, 17 Oct 2019 11:26:04 GMT
Transfer-Encoding: chunked

{
    "message": "something went wrong",
    "status": 500
}
```

4. Endpoint returning 404

```bash
http :8081/bar/123

HTTP/1.1 404
Content-Length: 0
Date: Thu, 17 Oct 2019 11:26:35 GMT
```

Let's image we have to integrate with the above legacy service. How can we correct the above legacy service API design issues and provide a better contract with the new service (`foo-service`)

1. Success

```bash
http :8080/success

HTTP/1.1 200
Content-Type: application/json
Date: Thu, 17 Oct 2019 11:28:07 GMT
Transfer-Encoding: chunked

{
    "barValue": "hello",
    "id": "5cc741fb-9a46-4559-a7b5-b847e1247133"
}
```

2. Failure With Non 200 HTTP status code

```bash
http :8080/failure-with-status-non-200

HTTP/1.1 502
Content-Type: application/json
Date: Thu, 17 Oct 2019 11:29:10 GMT
Transfer-Encoding: chunked

{
    "error": "Bad Gateway",
    "message": "endpoint: http://localhost:8080/failure-with-status-non-200 could not produce result because: status 500 reading BarServiceClient#failWithStatusNon200()",
    "path": "/failure-with-status-non-200",
    "status": 502,
    "timestamp": "2019-10-17T11:29:10.905+0000"
}
```

In the logs we can see:

```bash
...
2019-10-17 13:29:27.004 ERROR 64166 --- [nio-8080-exec-9] com.example.fooservice.FooController     : endpoint: http://localhost:8080/failure-with-status-non-200 could not produce result because: status 500 reading BarServiceClient#failWithStatusNon200()
...
```


3. Failure With 200 HTTP status code 

Currently is doing this, but we expect and error.

```bash
http :8080/failure-with-status-200

HTTP/1.1 200
Content-Type: application/json
Date: Thu, 17 Oct 2019 11:30:38 GMT
Transfer-Encoding: chunked

{
    "barValue": null,
    "id": "68917b5e-49de-45f6-a020-3ce955323f53"
}
```

By default the `ResponseEntityDecoder` is using see:

```java
@Configuration
public class FeignClientsConfiguration {
    ...
	@Bean
	@ConditionalOnMissingBean
	public Decoder feignDecoder() {
		return new OptionalDecoder(
				new ResponseEntityDecoder(new SpringDecoder(this.messageConverters)));
	}
	...
}
```


4. Endpoint returning 404

We would like to threat the 404 http status as a valid response not an error. With OpenFeign we can set 

```yaml
feign.client.config:
  bar-service:
    decode404: true
```

Example request:

```bash
http :8080/decode404/123

HTTP/1.1 200
Content-Type: application/json
Date: Thu, 17 Oct 2019 11:31:33 GMT
Transfer-Encoding: chunked

{
    "barValue": null,
    "id": "440ea4e6-4ea3-4983-a480-81ff118762b9"
}
```

Instead of:

```bash
http :8080/decode404/123

HTTP/1.1 502
Content-Type: application/json
Date: Thu, 17 Oct 2019 11:33:46 GMT
Transfer-Encoding: chunked

{
    "error": "Bad Gateway",
    "message": "endpoint: http://localhost:8080/decode404/123 could not produce result because: status 404 reading BarServiceClient#decode404(String)",
    "path": "/decode404/123",
    "status": 502,
    "timestamp": "2019-10-17T11:33:45.970+0000"
}
```

### Logging

After setting the 

```yaml
logging:
  level:
    com.example.fooservice.BarServiceClient: debug
```

we can configure the `loggerLevel` to `none`, `basic`, `headers` or `full` 

```yaml
feign.client.config:
  bar-service:
    loggerLevel: full
```



Resources



1. https://github.com/OpenFeign/feign/issues/776
