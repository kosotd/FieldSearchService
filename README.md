# FieldSearchService

Class for executing (so far oracle) queries.

Usage: 
```
    val fieldSearchService = FieldSearchService.create(dataSource)
    val (count, _) = fieldSearchService.search(SearchTableDTO(), {
        it.searchObjectsCount()
    }, {
        println(it)
    })

    val (ids, _) = fieldSearchService.search(SearchTableDTO()) {
        it.searchObjects()
    }
```      
```
    HttpStatus expectedStatus = HttpStatus.ACCEPTED;
    RequestService.build(timeout).post("http://site.com").send(expectedStatus);
```
      
Maven:
```
    <dependency>
      <groupId>com.kosotd</groupId>
      <artifactId>field-search-service</artifactId>
      <version>1.0</version>
    </dependency>
    ...
    <repository>
        <id>field-search-service-mvn-repo</id>
        <url>https://raw.github.com/kosotd/FieldSearchService/mvn-repo/</url>
    </repository>
```
