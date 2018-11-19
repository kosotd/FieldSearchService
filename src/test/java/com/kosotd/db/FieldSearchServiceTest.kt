package com.kosotd.db

import org.junit.Test
import javax.sql.DataSource

class FieldSearchServiceTest {

    private lateinit var dataSource: DataSource

    @Test
    fun test(){
        val fieldSearchService = FieldSearchService.create(dataSource)
        val (count, _) = fieldSearchService.search(SearchTableDTO(), {
            it.searchObjectsCount()
        }, {
            println(it)
        })

        val (ids, _) = fieldSearchService.search(SearchTableDTO()) {
            it.searchObjects()
        }
    }
}