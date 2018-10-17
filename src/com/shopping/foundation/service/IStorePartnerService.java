package com.shopping.foundation.service;

import com.shopping.core.query.support.IPageList;
import com.shopping.core.query.support.IQueryObject;
import com.shopping.foundation.domain.StorePartner;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface IStorePartnerService
{
  boolean save(StorePartner paramStorePartner);

  StorePartner getObjById(Long paramLong);

  boolean delete(Long paramLong);

  boolean batchDelete(List<Serializable> paramList);

  IPageList list(IQueryObject paramIQueryObject);

  boolean update(StorePartner paramStorePartner);

  List<StorePartner> query(String paramString, Map paramMap, int paramInt1, int paramInt2);
}



 
 