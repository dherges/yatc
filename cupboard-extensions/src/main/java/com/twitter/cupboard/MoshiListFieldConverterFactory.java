package com.twitter.cupboard;

import com.squareup.moshi.Moshi;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import nl.qbusict.cupboard.Cupboard;
import nl.qbusict.cupboard.convert.FieldConverter;
import nl.qbusict.cupboard.convert.FieldConverterFactory;

public class MoshiListFieldConverterFactory implements FieldConverterFactory {

    private final Moshi mMoshi;

    public MoshiListFieldConverterFactory(Moshi moshi) {
        this.mMoshi = moshi;
    }

    @Override
    public FieldConverter<?> create(Cupboard cupboard, final Type type) {
        if (type == List.class || (type instanceof ParameterizedType
                && ((Class<?>) (((ParameterizedType) type).getRawType())).isAssignableFrom(List.class))) {

            return new MoshiListFieldConverter(mMoshi);
        }

        return null;
    }

}
