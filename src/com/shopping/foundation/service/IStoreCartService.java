package com.shopping.foundation.service;

import com.shopping.core.query.support.IPageList;
import com.shopping.core.query.support.IQueryObject;
import com.shopping.foundation.domain.StoreCart;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface IStoreCartService
{
  boolean save(StoreCart paramStoreCart);

  StoreCart getObjById(Long paramLong);

  boolean delete(Long paramLong);

  boolean batchDelete(List<Serializable> paramList);

  IPageList list(IQueryObject paramIQueryObject);

  boolean update(StoreCart paramStoreCart);

  List<StoreCart> query(String paramString, Map paramMap, int paramInt1, int paramInt2);
}



 
 