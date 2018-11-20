package com.kosotd.db

import com.fasterxml.jackson.annotation.JsonProperty

class SearchTableDTO (
        @JsonProperty("name")
        var name: String? = null,
        @JsonProperty("order_column")
        var orderColumn: String? = null,
        @JsonProperty("select_columns")
        var selectColumns: MutableList<SelectColumnDTO>? = null,
        @JsonProperty("fields")
        var fields: MutableList<SearchFieldDTO>? = null,
        @JsonProperty("join_tables")
        var joinTables: MutableList<SearchTableDTO>? = null,
        @JsonProperty("join_column")
        var joinColumn: String? = null,
        @JsonProperty("parent_join_column")
        var parentJoinColumn: String? = null
) {

    internal var alias: String = ""
    internal var parentAlias: String = ""

    internal fun assignAlias(alias: String) {
        this.alias = alias
        fields?.forEach {
            it.alias = alias
        }
        selectColumns?.forEach {
            it.alias = alias
        }
    }

    internal fun getAllSelectColumns(): MutableList<SelectColumnDTO> {
        val result = mutableListOf<SelectColumnDTO>()
        result.addAll(selectColumns!!)
        joinTables!!.forEach {
            result.addAll(it.getAllSelectColumns())
        }
        return result
    }

    internal fun getAllFields(): MutableList<SearchFieldDTO> {
        val result = mutableListOf<SearchFieldDTO>()
        result.addAll(fields!!)
        joinTables!!.forEach {
            result.addAll(it.getAllFields())
        }
        return result
    }

    internal fun getAllJoinTables(): MutableList<SearchTableDTO> {
        val result = mutableListOf<SearchTableDTO>()
        result.addAll(joinTables!!)
        joinTables!!.forEach {
            result.addAll(it.getAllJoinTables())
        }
        return result
    }
}
