package com.kosotd.db;

import kotlin.Unit;
import org.junit.Test;

import javax.sql.DataSource;
import java.util.List;

public class FieldSearchServiceTestJava {

    private DataSource dataSource;

    @Test
    public void test(){
        FieldSearchService fieldSearchService = FieldSearchService.Companion.create(dataSource);
        FieldSearchService.SearchData<List<Long>> searchData = fieldSearchService.search(new SearchTableDTO(),
                FieldSearchService.Search::searchObjects,
                s1 -> Unit.INSTANCE);
        List<Long> ids = searchData.getResult();
    }
}
