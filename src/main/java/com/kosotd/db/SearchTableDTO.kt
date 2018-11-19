package com.kosotd.db

import com.fasterxml.jackson.annotation.JsonProperty

open class SearchTableDTO (
        @JsonProperty("name")
        open var name: String? = null,
        @JsonProperty("fields")
        open var fields: MutableList<SearchFieldDTO>? = null,
        @JsonProperty("join_tables")
        open var joinTables: MutableList<JoinTableDTO>? = null
) {

    fun setAlias(alias: String) {
        fields?.forEach {
            it.alias = alias
        }
    }
}
