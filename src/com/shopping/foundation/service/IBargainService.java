package com.shopping.foundation.service;

import com.shopping.core.query.support.IPageList;
import com.shopping.core.query.support.IQueryObject;
import com.shopping.foundation.domain.Bargain;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface IBargainService
{
  boolean save(Bargain paramBargain);

  Bargain getObjById(Long paramLong);

  boolean delete(Long paramLong);

  boolean batchDelete(List<Serializable> paramList);

  IPageList list(IQueryObject paramIQueryObject);

  boolean update(Bargain paramBargain);

  List<Bargain> query(String paramString, Map paramMap, int paramInt1, int paramInt2);
}



 
 