package com.kosotd.db

import org.junit.Test
import org.postgresql.ds.PGSimpleDataSource
import org.postgresql.util.PSQLException

class FieldSearchServiceTest {

    private val dataSource = PGSimpleDataSource()
    private val searchService = FieldSearchService.create(dataSource)

    @Test(expected = PSQLException::class)
    fun test1(){
        searchService.search(SearchTableDTO(name = "TABLE")) { it.searchObjects() }
    }

    @Test(expected = PSQLException::class)
    fun test2(){
        searchService.search(SearchTableDTO(
                name = "TABLE",
                selectColumns = mutableListOf(SelectColumnDTO("COL1"), SelectColumnDTO("COL2")),
                orderColumn = "COL1")) { it.searchObjects() }
    }

    @Test(expected = PSQLException::class)
    fun test3(){
        searchService.search(SearchTableDTO(
                name = "TABLE",
                selectColumns = mutableListOf(SelectColumnDTO("COL1"), SelectColumnDTO("COL2")),
                joinTables = mutableListOf(JoinTableDTO(
                        name = "TABLE1",
                        selectColumns = mutableListOf(SelectColumnDTO("COL3")),
                        joinColumn = "COL3",
                        parentJoinColumn = "COL1"
                )),
                orderColumn = "COL1")) { it.searchObjects() }
    }

    @Test(expected = PSQLException::class)
    fun test4(){
        searchService.search(SearchTableDTO(
                name = "TABLE",
                selectColumns = mutableListOf(SelectColumnDTO("COL1"), SelectColumnDTO("COL2")),
                joinTables = mutableListOf(JoinTableDTO(
                        name = "TABLE1",
                        selectColumns = mutableListOf(SelectColumnDTO("COL3")),
                        joinColumn = "COL3",
                        parentJoinColumn = "COL1",
                        joinTables = mutableListOf(JoinTableDTO(
                                name = "TABLE2",
                                selectColumns = mutableListOf(SelectColumnDTO("COL4")),
                                joinColumn = "COL4",
                                parentJoinColumn = "COL3"
                        ))
                )),
                orderColumn = "COL1")) { it.searchObjects() }
    }

    @Test(expected = PSQLException::class)
    fun test5(){
        searchService.search(SearchTableDTO(
                name = "TABLE",
                selectColumns = mutableListOf(SelectColumnDTO("COL1"), SelectColumnDTO("COL2")),
                fields = mutableListOf(SearchFieldDTO(
                        "FIELD1",
                        "qwe"

                )),
                joinTables = mutableListOf(JoinTableDTO(
                        name = "TABLE1",
                        selectColumns = mutableListOf(SelectColumnDTO("COL3")),
                        joinColumn = "COL3",
                        parentJoinColumn = "COL1",
                        fields = mutableListOf(SearchFieldDTO(
                                "FIELD2",
                                "123",
                                "NUMBER",
                                "=",
                                "OR"

                        )),
                        joinTables = mutableListOf(JoinTableDTO(
                                name = "TABLE2",
                                selectColumns = mutableListOf(SelectColumnDTO("COL4")),
                                joinColumn = "COL4",
                                parentJoinColumn = "COL3",
                                fields = mutableListOf(SearchFieldDTO(
                                        "FIELD3",
                                        "zxc"

                                ))
                        ))
                )),
                orderColumn = "COL1")) { it.searchObjects() }
    }
}