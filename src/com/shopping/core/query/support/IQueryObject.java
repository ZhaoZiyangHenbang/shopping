package com.shopping.core.query.support;

import com.shopping.core.domain.virtual.SysMap;
import com.shopping.core.query.PageObject;
import java.util.Map;

public interface IQueryObject
{
  String getQuery();

  String getGroupQuery();

  Map getParameters();

  PageObject getPageObj();

  PageObject getPageObj3();

  IQueryObject addQuery(String paramString, Map paramMap);

  IQueryObject addQuery(String paramString1, SysMap paramSysMap, String paramString2);

  IQueryObject addQueryIn(String field, SysMap para, String expression);

  IQueryObject addQuery(String paramString1, Object paramObject, String paramString2, String paramString3);
}