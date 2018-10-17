package com.shopping.foundation.service;

import com.shopping.core.query.support.IPageList;
import com.shopping.core.query.support.IQueryObject;
import com.shopping.foundation.domain.Visit;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface IVisitService
{
  boolean save(Visit paramVisit);

  Visit getObjById(Long paramLong);

  boolean delete(Long paramLong);

  boolean batchDelete(List<Serializable> paramList);

  IPageList list(IQueryObject paramIQueryObject);

  boolean update(Visit paramVisit);

  List<Visit> query(String paramString, Map paramMap, int paramInt1, int paramInt2);
}



 
 