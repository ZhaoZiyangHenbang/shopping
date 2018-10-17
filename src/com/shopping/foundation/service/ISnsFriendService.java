package com.shopping.foundation.service;

import com.shopping.core.query.support.IPageList;
import com.shopping.core.query.support.IQueryObject;
import com.shopping.foundation.domain.SnsFriend;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface ISnsFriendService
{
  boolean save(SnsFriend paramSnsFriend);

  SnsFriend getObjById(Long paramLong);

  boolean delete(Long paramLong);

  boolean batchDelete(List<Serializable> paramList);

  IPageList list(IQueryObject paramIQueryObject);

  boolean update(SnsFriend paramSnsFriend);

  List<SnsFriend> query(String paramString, Map paramMap, int paramInt1, int paramInt2);
}



 
 