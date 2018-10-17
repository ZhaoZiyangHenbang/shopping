package com.shopping.core.query.support;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface IPageList extends Serializable {
  List getResult();

  void setQuery(IQuery paramIQuery);

  int getPages();

  int getRowCount();

  int getCurrentPage();

  int getPageSize();

  void doList(int paramInt1, int paramInt2, String paramString1, String paramString2);

  void doList(int paramInt1, int paramInt2, String paramString1, String paramString2, Map paramMap);
}