package com.acewill.call;

public interface IGenericsSerializator {
    <T> T transform(String response, Class<T> classOfT);
}
