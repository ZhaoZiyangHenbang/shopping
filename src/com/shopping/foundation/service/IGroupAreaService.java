package com.shopping.foundation.service;

import com.shopping.core.query.support.IPageList;
import com.shopping.core.query.support.IQueryObject;
import com.shopping.foundation.domain.GroupArea;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface IGroupAreaService
{
  boolean save(GroupArea paramGroupArea);

  GroupArea getObjById(Long paramLong);

  boolean delete(Long paramLong);

  boolean batchDelete(List<Serializable> paramList);

  IPageList list(IQueryObject paramIQueryObject);

  boolean update(GroupArea paramGroupArea);

  List<GroupArea> query(String paramString, Map paramMap, int paramInt1, int paramInt2);
}



 
 