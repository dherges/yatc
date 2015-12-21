package com.twitter.yatc.cupboard;

import android.content.ContentValues;
import android.database.MatrixCursor;
import android.test.AndroidTestCase;

import com.squareup.moshi.Moshi;

import java.util.Arrays;

import nl.qbusict.cupboard.Cupboard;
import nl.qbusict.cupboard.CupboardBuilder;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class FieldTest extends AndroidTestCase {

    protected MoshiFieldConverterFactory factory;
    protected Cupboard cupboard;

    public static class MyTestEntity {
        public Long _id;
        public AnotherEntity field;
    }

    public static class AnotherEntity {
        public String text;
        public float numeric;
        public boolean yesNo;
        public long aLong;
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        factory = new MoshiFieldConverterFactory(new Moshi.Builder().build(), AnotherEntity.class);

        cupboard = new CupboardBuilder()
                .registerFieldConverterFactory(factory)
/*                .registerFieldConverter(
                        AnotherEntity.class,
                        new MoshiFieldConverter<>(new Moshi.Builder().build(), AnotherEntity.class))*/
                .build();
        cupboard.register(MyTestEntity.class);
    }

    public void testListFieldConverter() {
        MyTestEntity entity = new MyTestEntity();
        entity._id = 23l;
        entity.field = new AnotherEntity();
        entity.field.aLong = 23;
        entity.field.numeric = 0.23f;
        entity.field.yesNo = false;
        entity.field.text = "some text goes here";

        ContentValues valuesFromEntity = cupboard.withEntity(MyTestEntity.class).toContentValues(entity);

        MatrixCursor cursor = new MatrixCursor(new String[] {"_id", "field"});
        cursor.addRow(Arrays.asList(23l, "{\"aLong\":23,\"numeric\":0.23,\"text\":\"some text goes here\",\"yesNo\":false}"));

        MyTestEntity entityFromCursor = cupboard.withCursor(cursor).get(MyTestEntity.class);


        // TODO test assertions ...
    }

}
