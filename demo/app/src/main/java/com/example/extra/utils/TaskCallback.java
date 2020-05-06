package com.example.extra.utils;

import java.lang.reflect.ParameterizedType;

public abstract class TaskCallback<E> {

    Class<E> clazz;

    public TaskCallback() {
        try {
            this.clazz = (Class <E>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        } catch (Exception e) {
            throw e;
        }
    }

    public void onPreExecute() {

    }

    public abstract void onPostExecuteSuccess(E e);

    public abstract void onPostExecuteFail();

}