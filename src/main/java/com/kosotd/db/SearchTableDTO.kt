package com.kosotd.db

import com.fasterxml.jackson.annotation.JsonProperty

open class SearchTableDTO (
        @JsonProperty("name")
        open var name: String? = null,
        @JsonProperty("order_column")
        open var orderColumn: String? = null,
        @JsonProperty("select_columns")
        open var selectColumns: MutableList<SelectColumnDTO>? = null,
        @JsonProperty("fields")
        open var fields: MutableList<SearchFieldDTO>? = null,
        @JsonProperty("join_tables")
        open var joinTables: MutableList<JoinTableDTO>? = null
) {

    var alias: String = ""
    var parentAlias: String = ""

    fun assignAlias(alias: String) {
        this.alias = alias
        fields?.forEach {
            it.alias = alias
        }
        selectColumns?.forEach {
            it.alias = alias
        }
    }

    fun getAllSelectColumns(): MutableList<SelectColumnDTO> {
        val result = mutableListOf<SelectColumnDTO>()
        result.addAll(selectColumns!!)
        joinTables!!.forEach {
            result.addAll(it.getAllSelectColumns())
        }
        return result
    }

    fun getAllFields(): MutableList<SearchFieldDTO> {
        val result = mutableListOf<SearchFieldDTO>()
        result.addAll(fields!!)
        joinTables!!.forEach {
            result.addAll(it.getAllFields())
        }
        return result
    }

    fun getAllJoinTables(): MutableList<JoinTableDTO> {
        val result = mutableListOf<JoinTableDTO>()
        result.addAll(joinTables!!)
        joinTables!!.forEach {
            result.addAll(it.getAllJoinTables())
        }
        return result
    }
}
