package com.kosotd.db

import com.fasterxml.jackson.annotation.JsonProperty

class JoinTableDTO(
        @JsonProperty("name")
        override var name: String? = null,
        @JsonProperty("order_column")
        override var orderColumn: String? = null,
        @JsonProperty("select_columns")
        override var selectColumns: MutableList<SelectColumnDTO>? = null,
        @JsonProperty("fields")
        override var fields: MutableList<SearchFieldDTO>? = null,
        @JsonProperty("join_tables")
        override var joinTables: MutableList<JoinTableDTO>? = null,
        @JsonProperty("join_column")
        var joinColumn: String? = null,
        @JsonProperty("parent_join_column")
        var parentJoinColumn: String? = null
): SearchTableDTO(name, orderColumn, selectColumns, fields, joinTables)