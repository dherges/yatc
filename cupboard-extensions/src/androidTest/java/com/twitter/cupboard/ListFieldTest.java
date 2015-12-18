package com.twitter.cupboard;

import android.content.ContentValues;
import android.database.MatrixCursor;
import android.test.AndroidTestCase;

import com.squareup.moshi.Moshi;

import java.util.Arrays;
import java.util.List;

import nl.qbusict.cupboard.Cupboard;
import nl.qbusict.cupboard.CupboardBuilder;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ListFieldTest extends AndroidTestCase {

    protected MoshiListFieldConverterFactory factory;
    protected Cupboard cupboard;

    public static class MyTestEntity {
        public Long _id;
        public List<String> strings;
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        factory = new MoshiListFieldConverterFactory(new Moshi.Builder().build());;

        cupboard = new CupboardBuilder().registerFieldConverterFactory(factory).build();
        cupboard.register(MyTestEntity.class);
    }

    public void testListFieldConverter() {
        MyTestEntity entity = new MyTestEntity();
        entity.strings = Arrays.asList("test1", "test2");

        ContentValues values = cupboard.withEntity(MyTestEntity.class).toContentValues(entity);
        assertEquals("[\"test1\",\"test2\"]", values.getAsString("strings"));

        MatrixCursor cursor = new MatrixCursor(new String[] {"strings"});
        cursor.addRow(Arrays.asList("[\"test3\",\"test4\"]"));

        entity = cupboard.withCursor(cursor).get(MyTestEntity.class);
        assertNotNull(entity.strings);
        assertEquals(2, entity.strings.size());
        assertEquals("test3", entity.strings.get(0));
        assertEquals("test4", entity.strings.get(1));

        // TODO test assertions ...

    }

}
