package com.acewill.call;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;

import okhttp3.Response;
import android.util.Log;

import com.zhy.http.okhttp.callback.Callback;

/**
 * Created by JimGong on 2016/6/23.
 */

public abstract class GenericsCallback<T>
        extends Callback<T>
{
    IGenericsSerializator mGenericsSerializator;

    public GenericsCallback(IGenericsSerializator serializator) {
        mGenericsSerializator = serializator;
    }

    @Override
    public T parseNetworkResponse(Response response, int id)
            throws IOException
    {
        String string = response.body()
                                .string();
        Log.e("responseData", "response>>" + string);
        System.out.println(string);
        Class<T> entityClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        if (entityClass == String.class) {
            return (T) string;
        }
        T bean = mGenericsSerializator.transform(string, entityClass);
        return bean;
    }

}
