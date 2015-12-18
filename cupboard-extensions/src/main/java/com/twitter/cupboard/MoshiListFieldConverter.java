package com.twitter.cupboard;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.squareup.moshi.Moshi;

import java.io.IOException;
import java.util.List;

import nl.qbusict.cupboard.convert.EntityConverter;
import nl.qbusict.cupboard.convert.FieldConverter;


public class MoshiListFieldConverter implements FieldConverter<List<?>> {
    private static final String TAG = "MoshiLFC";

    private final Moshi mMoshi;

    public MoshiListFieldConverter(Moshi moshi) {
        this.mMoshi = moshi;
    }

    @Override
    public List<?> fromCursorValue(Cursor cursor, int columnIndex) {
        String json = cursor.getString(columnIndex);
        try {
            return mMoshi.adapter(List.class).fromJson(json);
        } catch (IOException e) {
            Log.e(TAG, "Caught IOException while reading from JSON", e);
        }

        return null;
    }

    @Override
    public void toContentValue(List<?> value, String key, ContentValues values) {
        values.put(key, mMoshi.adapter(List.class).toJson(value));
    }

    @Override
    public EntityConverter.ColumnType getColumnType() {
        return EntityConverter.ColumnType.TEXT;
    }

}
