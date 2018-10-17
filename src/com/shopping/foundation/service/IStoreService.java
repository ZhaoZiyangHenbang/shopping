package com.shopping.foundation.service;

import com.shopping.core.query.support.IPageList;
import com.shopping.core.query.support.IQueryObject;
import com.shopping.foundation.domain.Store;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface IStoreService
{
  boolean save(Store paramStore);

  Store getObjById(Long paramLong);

  boolean delete(Long paramLong);

  boolean batchDelete(List<Serializable> paramList);

  IPageList list(IQueryObject paramIQueryObject);

  boolean update(Store paramStore);

  List<Store> query(String paramString, Map paramMap, int paramInt1, int paramInt2);

  Store getObjByProperty(String paramString, Object paramObject);
}



 
 