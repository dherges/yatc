package com.twitter.cupboard;

import com.squareup.moshi.Moshi;

import java.lang.reflect.Type;

import nl.qbusict.cupboard.Cupboard;
import nl.qbusict.cupboard.convert.FieldConverter;
import nl.qbusict.cupboard.convert.FieldConverterFactory;

public class MoshiFieldConverterFactory implements FieldConverterFactory {

    private final Moshi mMoshi;
    private final Class<?> mType;

    public MoshiFieldConverterFactory(Moshi moshi, Class<?> fieldType) {
        this.mMoshi = moshi;
        this.mType = fieldType;
    }

    @Override
    public FieldConverter<?> create(Cupboard cupboard, Type type) {
        if (mType.isAssignableFrom((Class) type)) {
            return new MoshiFieldConverter<>(mMoshi, mType);
        }

        return null;
    }

}
