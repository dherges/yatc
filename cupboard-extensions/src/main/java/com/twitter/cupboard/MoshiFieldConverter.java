package com.twitter.cupboard;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.squareup.moshi.Moshi;

import java.io.IOException;

import nl.qbusict.cupboard.convert.EntityConverter;
import nl.qbusict.cupboard.convert.FieldConverter;

public class MoshiFieldConverter<T> implements FieldConverter<T> {
    private static final String TAG = "MoshiFC";

    private final Class<T> mType;
    private final Moshi mMoshi;

    public MoshiFieldConverter(Moshi moshi, Class<T> type) {
        this.mMoshi = moshi;
        this.mType = type;
    }

    @Override
    public T fromCursorValue(Cursor cursor, int columnIndex) {
        try {
            return mMoshi.adapter(mType).fromJson(cursor.getString(columnIndex));
        } catch (IOException e) {
            Log.e(TAG, "Caught IOException while reading from JSON", e);
        }

        return null;
    }

    @Override
    public void toContentValue(T value, String key, ContentValues values) {
        values.put(key, mMoshi.adapter(mType).toJson(value));
    }

    @Override
    public EntityConverter.ColumnType getColumnType() {
        return EntityConverter.ColumnType.TEXT;
    }

}
