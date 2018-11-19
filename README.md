# RequestService

Class for building and sending GET and POST requests.

Usage: 
```
    RequestService.Response response = RequestService.build(1000).get("http://site.com", new HashMap<String, String>(){{
        put("Authorization", "Bearer 30d8d1a8-e6d8-4798-b907-deeacebf61ea");
    }}, new HashMap<String, String>(){{
        put("param", "value");
    }}).send();
    String data = response.getData();
    Header[] headers = response.getHeaders();
```      
```
    HttpStatus expectedStatus = HttpStatus.ACCEPTED;
    RequestService.build(timeout).post("http://site.com").send(expectedStatus);
```
```
    RequestService.Response response = RequestService.build().get(b -> {
        b.setUrl("http://site.com").addHeader("Authorization", "Bearer 30d8d1a8-e6d8-4798-b907-deeacebf61ea");
    }).send();
```
      
Maven:
```
    <dependency>
      <groupId>com.kosotd</groupId>
      <artifactId>request-service</artifactId>
      <version>1.2</version>
    </dependency>
    ...
    <repository>
        <id>request-service-mvn-repo</id>
        <url>https://raw.github.com/kosotd/RequestService/mvn-repo/</url>
    </repository>
```
