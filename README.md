
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
    "message": "something went wrong",
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
    "message": "something went wrong again",
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

Let's image we have to integrate with the above legacy service. How can we correct the above legacy service API design issues and provide a better contract with the new service (`foo-service`).

We are using [OpenFeign](https://github.com/OpenFeign/feign)

```java
@FeignClient("bar-service")
interface BarServiceClient {

    @GetMapping("/success")
    Bar success();

    @GetMapping("/failure-with-status-200")
    Bar failWithStatus200();

    @GetMapping("/failure-with-status-non-200")
    Bar failWithStatusNon200();

    @GetMapping("/bar/{id}")
    String decode404(@PathVariable String id);
}
``` 

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

Here we expect and error with indication:

```bash
http :8080/failure-with-status-200

HTTP/1.1 502
Content-Type: application/json
Date: Thu, 17 Oct 2019 12:18:49 GMT
Transfer-Encoding: chunked

{
    "error": "Bad Gateway",
    "message": "endpoint: http://localhost:8080/failure-with-status-200 could not produce result because: barValue cannot be empty",
    "path": "/failure-with-status-200",
    "status": 502,
    "timestamp": "2019-10-17T12:18:49.483+0000"
}
```

and also in the logs an ERROR entry

```bash
2019-10-17 14:18:49.476 ERROR 67973 --- [nio-8080-exec-3] com.example.fooservice.FooController     : endpoint: http://localhost:8080/failure-with-status-200 could not produce result because: barValue cannot be empty
```

By default the `ResponseEntityDecoder` is used from the `spring-cloud-openfeign` module

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

We would like to handle a response with 404 http status as a valid response, not as an error. With `OpenFeign` we can easily do this, setting the  

```yaml
feign.client.config:
  bar-service:
    decode404: true
```

Example request (in this case setting the default value for the `barValue` field)

```bash
http :8080/decode404/123

HTTP/1.1 200
Content-Type: application/json
Date: Thu, 17 Oct 2019 12:23:04 GMT
Transfer-Encoding: chunked

{
    "barValue": "default value",
    "id": "36d5cff7-ee8f-4467-bbbd-35c08b989167"
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

and in the logs

```bash
2019-10-17 14:11:41.641 DEBUG 67686 --- [nio-8080-exec-7] com.example.fooservice.BarServiceClient  : [BarServiceClient#failWithStatusNon200] ---> GET http://bar-service/failure-with-status-non-200 HTTP/1.1
2019-10-17 14:11:41.641 DEBUG 67686 --- [nio-8080-exec-7] com.example.fooservice.BarServiceClient  : [BarServiceClient#failWithStatusNon200] ---> END HTTP (0-byte body)
2019-10-17 14:11:41.647 DEBUG 67686 --- [nio-8080-exec-7] com.example.fooservice.BarServiceClient  : [BarServiceClient#failWithStatusNon200] <--- HTTP/1.1 500 (6ms)
2019-10-17 14:11:41.648 DEBUG 67686 --- [nio-8080-exec-7] com.example.fooservice.BarServiceClient  : [BarServiceClient#failWithStatusNon200] connection: close
2019-10-17 14:11:41.648 DEBUG 67686 --- [nio-8080-exec-7] com.example.fooservice.BarServiceClient  : [BarServiceClient#failWithStatusNon200] content-type: application/json
2019-10-17 14:11:41.648 DEBUG 67686 --- [nio-8080-exec-7] com.example.fooservice.BarServiceClient  : [BarServiceClient#failWithStatusNon200] date: Thu, 17 Oct 2019 12:11:41 GMT
2019-10-17 14:11:41.648 DEBUG 67686 --- [nio-8080-exec-7] com.example.fooservice.BarServiceClient  : [BarServiceClient#failWithStatusNon200] transfer-encoding: chunked
2019-10-17 14:11:41.648 DEBUG 67686 --- [nio-8080-exec-7] com.example.fooservice.BarServiceClient  : [BarServiceClient#failWithStatusNon200] 
2019-10-17 14:11:41.648 DEBUG 67686 --- [nio-8080-exec-7] com.example.fooservice.BarServiceClient  : [BarServiceClient#failWithStatusNon200] {"message":"something went wrong again","status":500}
2019-10-17 14:11:41.649 DEBUG 67686 --- [nio-8080-exec-7] com.example.fooservice.BarServiceClient  : [BarServiceClient#failWithStatusNon200] <--- END HTTP (53-byte body)
```


Resources


1. https://spring.io/projects/spring-cloud-openfeign
