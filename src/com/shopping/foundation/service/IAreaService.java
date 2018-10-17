package com.shopping.foundation.service;

import com.shopping.core.query.support.IPageList;
import com.shopping.core.query.support.IQueryObject;
import com.shopping.foundation.domain.Area;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface IAreaService
{
  boolean save(Area paramArea);

  Area getObjById(Long paramLong);

  boolean delete(Long paramLong);

  boolean batchDelete(List<Serializable> paramList);

  IPageList list(IQueryObject paramIQueryObject);

  boolean update(Area paramArea);

  List<Area> query(String paramString, Map paramMap, int paramInt1, int paramInt2);
}



 
 