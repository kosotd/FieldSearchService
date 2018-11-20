package com.kosotd.db

import com.fasterxml.jackson.annotation.JsonProperty

class SearchFieldDTO(
    @JsonProperty("name")
    var name: String? = null,
    @JsonProperty("value")
    var value: String? = null,
    /*
        TEXT, NUMBER, DATE, NULL
    */
    @JsonProperty("field_type")
    var fieldType: String? = null,
    /*
        !=, =, >, <, >=, <=, LIKE,
        IS, IS NOT - only for NULL fieldType
    */
    @JsonProperty("operator")
    var operator: String? = null,
    /*
        AND, OR
    */
    @JsonProperty("logical_statement")
    var logicalStatement: String? = null,
    @JsonProperty("priority")
    var priority: Int? = null
){
    internal var alias: String = ""
}