package com.kosotd.db

import com.kosotd.logic.WhereStatementBuilder
import java.util.*
import java.util.logging.Logger
import javax.sql.DataSource

/**
 * сервис для поиска объектов по полям
 */
class FieldSearchService private constructor() {

    companion object {
        private val logger = Logger.getLogger(FieldSearchService::class.java.name)
        private lateinit var dataSource: DataSource

        fun create(dataSource: DataSource): FieldSearchService {
            this.dataSource = dataSource
            return FieldSearchService()
        }
    }

    data class SearchData<out T>(val result: T?, val message: String)
    fun <T> search(searchTableDTO: SearchTableDTO, success: (Search) -> T): SearchData<T> {
        return search(searchTableDTO, success, {})
    }

    fun <T> search(searchTableDTO: SearchTableDTO, success: (Search) -> T, failed: (String) -> Unit): SearchData<T> {
        val (isValid, msg) = checkAndFixSearchTable(searchTableDTO)
        if (isValid) return SearchData(success(Search(searchTableDTO)), "") else failed(msg)
        return SearchData(null, msg)
    }

    private fun checkAndFixSearchTable(searchTableDTO: SearchTableDTO, depth: Int = 0): CheckDTO {
        if (searchTableDTO.name.isNullOrBlank()) return CheckDTO(false, "search_table.name must not be empty")

        if (searchTableDTO.orderColumn.isNullOrBlank() && depth < 1) searchTableDTO.orderColumn = "ID"

        if (searchTableDTO.selectColumns == null) searchTableDTO.selectColumns = mutableListOf()
        if (searchTableDTO.selectColumns!!.isEmpty() && depth < 1) searchTableDTO.selectColumns!!.add(SelectColumnDTO("ID", "NUMBER"))
        searchTableDTO.selectColumns!!.forEach {
            val (isValid, message) = checkSelectColumn(it)
            if (!isValid) return CheckDTO(isValid, message)
        }

        if (searchTableDTO.fields == null) searchTableDTO.fields = mutableListOf()
        searchTableDTO.fields!!.forEach {
            val (isValid, message) = checkSearchField(it)
            if (!isValid) return CheckDTO(isValid, message)
        }

        searchTableDTO.assignAlias("j$depth")
        searchTableDTO.parentAlias = "j${depth - 1}"

        if (searchTableDTO.joinTables == null) searchTableDTO.joinTables = mutableListOf()
        searchTableDTO.joinTables!!.forEach {
            val (isValid, message) = checkAndFixSearchTable(it, depth + 1)
            if (!isValid) return CheckDTO(isValid, message)
        }

        if (depth > 0){
            if ((searchTableDTO as JoinTableDTO).joinColumn.isNullOrBlank())
                return CheckDTO(false, "search_table.join_column must not be empty")
            if (searchTableDTO.parentJoinColumn.isNullOrBlank())
                return CheckDTO(false, "search_table.parent_join_column must not be empty")
        }

        return CheckDTO(true, "")
    }

    private fun checkSelectColumn(selectColumnDTO: SelectColumnDTO): CheckDTO {
        val fieldTypes = listOf("TEXT", "NUMBER", "DATE")

        if (selectColumnDTO.name.isNullOrBlank()) return CheckDTO(false, "search_column.name must not be empty")

        if (selectColumnDTO.fieldType == null) selectColumnDTO.fieldType = "TEXT"
        if (strNotIn(selectColumnDTO.fieldType, fieldTypes))
            return CheckDTO(false, "search_column.field_type must be one of [TEXT, NUMBER, DATE]")

        return CheckDTO(true, "")
    }

    private fun checkSearchField(searchFieldDTO: SearchFieldDTO): CheckDTO {
        val fieldTypes = listOf("TEXT", "NUMBER", "DATE", "NULL")
        val operatorsNull = listOf("IS", "IS NOT")
        val operators = listOf("!=", "=", ">", "<", ">=", "<=", "LIKE")
        val logicalStatements = listOf("AND", "OR")

        if (searchFieldDTO.name.isNullOrBlank()) return CheckDTO(false, "search_field.name must not be empty")

        if (searchFieldDTO.value.isNullOrBlank()) return CheckDTO(false, "search_field.value must not be empty")

        if (searchFieldDTO.fieldType == null) searchFieldDTO.fieldType = "TEXT"
        if (strNotIn(searchFieldDTO.fieldType, fieldTypes))
            return CheckDTO(false, "search_field.field_type must be one of [TEXT, NUMBER, DATE, NULL]")

        if ("NULL".equals(searchFieldDTO.fieldType?.trim(), true)) {
            if (searchFieldDTO.operator == null) searchFieldDTO.operator = "IS"
            if (strNotIn(searchFieldDTO.operator, operatorsNull))
                return CheckDTO(false, "search_field.operator must be one of [IS, IS NOT] for NULL search_field.field_type")
        } else {
            if (searchFieldDTO.operator == null) searchFieldDTO.operator = "="
            if (strNotIn(searchFieldDTO.operator, operators))
                return CheckDTO(false, "search_field.operator must be one of [!=, =, >, <, >=, <=, LIKE]")
        }

        if (searchFieldDTO.logicalStatement == null) searchFieldDTO.logicalStatement = "AND"
        if (strNotIn(searchFieldDTO.logicalStatement, logicalStatements))
            return CheckDTO(false, "search_field.logical_statement must be one of [AND, OR]")

        if (searchFieldDTO.priority == null) searchFieldDTO.priority = 0

        return CheckDTO(true, "")
    }

    private fun strNotIn(value: String?, values: List<String>): Boolean {
        return !values.any { it.equals(value?.trim(), true) }
    }

    class Search internal constructor(private val searchTableDTO: SearchTableDTO) {

        /**
         * найти все id объектов, соответствующие критериям поиска
         * @return список id найденных объектов
         */
        fun searchObjects(): MutableList<MutableMap<String, Any>> {
            val builder = StringBuilder()

            builder.append("SELECT ${buildSelectPart(searchTableDTO)} FROM ${searchTableDTO.name} ${searchTableDTO.alias}")
            buildJoinPart(searchTableDTO).ifPresent {
                builder.append(it)
            }
            buildWherePart(searchTableDTO).ifPresent {
                builder.append(it)
            }
            builder.append(" ORDER BY ${searchTableDTO.alias}.${searchTableDTO.orderColumn} ASC")

            logger.info(builder.toString())
            return executeQuery(builder.toString(), searchTableDTO)
        }

        /**
         * найти все id объектов, соответствующие критериям поиска
         * @return список id найденных объектов
         */
        fun searchObjectsPagination(l: Int, r: Int): MutableList<MutableMap<String, Any>> {
            val builder = StringBuilder()

            builder.append("SELECT ${buildSelectPart(searchTableDTO)}, " +
                    "ROW_NUMBER() OVER (ORDER BY ${searchTableDTO.alias}.${searchTableDTO.orderColumn} ASC) AS ROWNUM_COLUMN " +
                    "FROM ${searchTableDTO.name} ${searchTableDTO.alias}")
            buildJoinPart(searchTableDTO).ifPresent {
                builder.append(it)
            }
            buildWherePart(searchTableDTO).ifPresent {
                builder.append(it)
            }
            val query = "SELECT * FROM ($builder) WHERE ROWNUM_COLUMN >= ${l + 1} AND ROWNUM_COLUMN < ${r + 1}"

            logger.info(query)
            return executeQuery(query, searchTableDTO)
        }

        /**
         * найти количество объектов, соответствующих критериям поиска
         * @return количество объектов
         */
        fun searchObjectsCount(): Long {
            val builder = StringBuilder()
            builder.append("SELECT COUNT(${searchTableDTO.alias}.${searchTableDTO.orderColumn}) AS COUNT_COLUMN FROM ${searchTableDTO.name} ${searchTableDTO.alias}")

            buildJoinPart(searchTableDTO).ifPresent {
                builder.append(it)
            }
            buildWherePart(searchTableDTO).ifPresent {
                builder.append(it)
            }

            logger.info(builder.toString())
            return executeQueryCount(builder.toString())
        }

        private fun buildSelectPart(searchTableDTO: SearchTableDTO): String {
            val selectPart = StringBuilder()
            val allColumns = searchTableDTO.getAllSelectColumns()
            var delim = ""
            allColumns.forEach {
                selectPart.append("$delim${it.alias}.${it.name}")
                delim = ", "
            }
            return selectPart.toString()
        }

        private fun buildWherePart(searchTableDTO: SearchTableDTO): Optional<String> {
            val allFields = searchTableDTO.getAllFields()
            if (allFields.size > 0) {
                val whereStatement = WhereStatementBuilder.startBuilding()
                allFields.forEach {
                    whereStatement.addStatement(it.logicalStatement, buildCondition(it), it.priority)
                }
                return Optional.of(" WHERE " + whereStatement.endBuilding())
            }
            return Optional.empty()
        }

        private fun buildJoinPart(searchTableDTO: SearchTableDTO): Optional<String> {
            val allTables = searchTableDTO.getAllJoinTables()
            if (allTables.size > 0) {
                val joinPart = StringBuilder()
                allTables.forEach {
                    joinPart.append(" JOIN ${it.name} ${it.alias} ON ${it.parentAlias}.${it.parentJoinColumn} = ${it.alias}.${it.joinColumn}")
                }
                return Optional.of(joinPart.toString())
            }
            return Optional.empty()
        }

        /**
         * построение условия для поиска по полю
         * @param searchFieldDTO поле для поиска
         * @return условия для поиска по полю
         */
        private fun buildCondition(searchFieldDTO: SearchFieldDTO): String {
            val builder = StringBuilder()
            when {
                "TEXT".equals(searchFieldDTO.fieldType!!.trim(), true) -> {
                    builder.append("${searchFieldDTO.alias}.").append(searchFieldDTO.name).append(" ")
                            .append(searchFieldDTO.operator).append(" ")
                            .append("'${searchFieldDTO.value}'")
                }
                "NUMBER".equals(searchFieldDTO.fieldType!!.trim(), true) -> {
                    builder.append("${searchFieldDTO.alias}.").append(searchFieldDTO.name).append(" ")
                            .append(searchFieldDTO.operator).append(" ")
                            .append(searchFieldDTO.value)
                }
                "DATE".equals(searchFieldDTO.fieldType!!.trim(), true) -> {
                    builder.append("TRUNC(${searchFieldDTO.alias}.${searchFieldDTO.name})").append(" ")
                            .append(searchFieldDTO.operator).append(" ")
                            .append("TO_DATE('${searchFieldDTO.value}', 'dd.MM.yyyy')")
                }
                "NULL".equals(searchFieldDTO.fieldType!!.trim(), true) -> {
                    builder.append("${searchFieldDTO.alias}.").append(searchFieldDTO.name).append(" ")
                            .append(searchFieldDTO.operator).append(" NULL")
                }
            }
            return builder.toString()
        }

        private fun executeQuery(query: String, searchTableDTO: SearchTableDTO): MutableList<MutableMap<String, Any>> {
            val objects = mutableListOf<MutableMap<String, Any>>()
            val allSelectColumns = searchTableDTO.getAllSelectColumns()
            dataSource.connection.use {
                it.createStatement().use {
                    it.executeQuery(query).use { rs ->
                        while (rs.next()) {
                            val map = mutableMapOf<String, Any>()
                            allSelectColumns.forEach {
                                map[it.name!!] = rs.getObject(it.name)
                            }
                            objects.add(map)
                        }
                    }
                }
            }
            return objects
        }

        private fun executeQueryCount(query: String): Long {
            val ids = mutableListOf<Long>()
            dataSource.connection.use {
                it.createStatement().use {
                    it.executeQuery(query).use {
                        while (it.next()) {
                            ids.add(it.getLong("COUNT_COLUMN"))
                        }
                    }
                }
            }
            return ids[0]
        }
    }
}