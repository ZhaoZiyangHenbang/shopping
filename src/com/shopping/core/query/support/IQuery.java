package com.shopping.core.query.support;


import java.util.List;
import java.util.Map;

public interface IQuery
{
  int getRows(String paramString);

  List getResult(String paramString);

  void setFirstResult(int paramInt);

  void setMaxResults(int paramInt);

  void setParaValues(Map paramMap);

  List getResult(String paramString, int paramInt1, int paramInt2);
}