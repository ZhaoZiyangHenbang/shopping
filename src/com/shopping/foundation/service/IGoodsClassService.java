package com.shopping.foundation.service;

import com.shopping.core.query.support.IPageList;
import com.shopping.core.query.support.IQueryObject;
import com.shopping.foundation.domain.GoodsClass;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface IGoodsClassService
{
  boolean save(GoodsClass paramGoodsClass);

  GoodsClass getObjById(Long paramLong);

  boolean delete(Long paramLong);

  boolean batchDelete(List<Serializable> paramList);

  IPageList list(IQueryObject paramIQueryObject);

  boolean update(GoodsClass paramGoodsClass);

  List<GoodsClass> query(String paramString, Map paramMap, int paramInt1, int paramInt2);

  GoodsClass getObjByProperty(String paramString, Object paramObject);
}



 
 