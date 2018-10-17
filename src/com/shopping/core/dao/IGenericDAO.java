package com.shopping.core.dao;


import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface IGenericDAO<T>
{
  T get(Serializable paramSerializable);

  void save(T paramT);

  void remove(Serializable paramSerializable);

  void update(T paramT);

  T getBy(String paramString, Object paramObject);

  List executeNamedQuery(String paramString, Object[] paramArrayOfObject, int paramInt1, int paramInt2);

  List<T> find(String paramString, Map paramMap, int paramInt1, int paramInt2);

  List query(String paramString, Map paramMap, int paramInt1, int paramInt2);

  int batchUpdate(String paramString, Object[] paramArrayOfObject);

  List executeNativeNamedQuery(String paramString);

  List executeNativeQuery(String paramString, Object[] paramArrayOfObject, int paramInt1, int paramInt2);

  int executeNativeSQL(String paramString);

  void flush();

  List group(String paramString, Map paramMap, int paramInt1, int paramInt2);

}