package com.shopping.foundation.service;

import com.shopping.core.query.support.IPageList;
import com.shopping.core.query.support.IQueryObject;
import com.shopping.foundation.domain.WaterMark;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface IWaterMarkService
{
  boolean save(WaterMark paramWaterMark);

  WaterMark getObjById(Long paramLong);

  boolean delete(Long paramLong);

  boolean batchDelete(List<Serializable> paramList);

  IPageList list(IQueryObject paramIQueryObject);

  boolean update(WaterMark paramWaterMark);

  List<WaterMark> query(String paramString, Map paramMap, int paramInt1, int paramInt2);

  WaterMark getObjByProperty(String paramString, Object paramObject);
}



 
 