package com.kosotd.db

import com.fasterxml.jackson.annotation.JsonProperty

class SelectColumnDTO(
    @JsonProperty("name")
    var name: String? = null,
    @JsonProperty("field_type")
    var fieldType: String? = null
){
    var alias: String = ""
}