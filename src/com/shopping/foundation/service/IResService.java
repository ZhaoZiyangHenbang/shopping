package com.shopping.foundation.service;

import com.shopping.core.query.support.IPageList;
import com.shopping.core.query.support.IQueryObject;
import com.shopping.foundation.domain.Res;
import java.util.List;
import java.util.Map;

public interface IResService
{
  boolean save(Res paramRes);

  boolean delete(Long paramLong);

  boolean update(Res paramRes);

  Res getObjById(Long paramLong);

  List<Res> query(String paramString, Map paramMap, int paramInt1, int paramInt2);

  IPageList list(IQueryObject paramIQueryObject);
}



 
 