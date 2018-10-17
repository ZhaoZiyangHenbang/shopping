package com.shopping.foundation.service;

import com.shopping.core.query.support.IPageList;
import com.shopping.core.query.support.IQueryObject;
import com.shopping.foundation.domain.Advert;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface IAdvertService
{
  boolean save(Advert paramAdvert);

  Advert getObjById(Long paramLong);

  boolean delete(Long paramLong);

  boolean batchDelete(List<Serializable> paramList);

  IPageList list(IQueryObject paramIQueryObject);

  boolean update(Advert paramAdvert);

  List<Advert> query(String paramString, Map paramMap, int paramInt1, int paramInt2);
}



 
 