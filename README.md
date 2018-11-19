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
    SearchTableDTO json example:
    {
        "name":"TABLE",
        "fields":[
            {"name":"FIELD1","operator":"=","value":1,"fieldType":"NUMBER","logicalStatement":"AND", "priority":1},  
            {"name":"FIELD2","operator":"=","value":"qwe", "priority":1}
        ],
        "join_tables":[
            {
                "fields":[           
                    {"name":"FIELD3","operator":"=","value":78,"fieldType":"NUMBER", "logicalStatement":"OR", "priority":0}
                ],
                "join_column":"JOIN_TABLE_ID",
                "parent_join_column":"ID",
                "name":"JOIN_TABLE"
            }
        ]
    }
    => query:
        SELECT t.ID 
        FROM TABLE 
        JOIN JOIN_TABLE j0 ON j0.JOIN_TABLE_ID = t.ID 
        WHERE (t.FIELD1 = 1 AND (t.FIELD2 = 'qwe' OR j0.FIELD3 = 78))
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
