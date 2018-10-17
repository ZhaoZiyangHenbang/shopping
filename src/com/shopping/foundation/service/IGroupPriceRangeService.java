package com.shopping.foundation.service;

import com.shopping.core.query.support.IPageList;
import com.shopping.core.query.support.IQueryObject;
import com.shopping.foundation.domain.GroupPriceRange;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface IGroupPriceRangeService
{
  boolean save(GroupPriceRange paramGroupPriceRange);

  GroupPriceRange getObjById(Long paramLong);

  boolean delete(Long paramLong);

  boolean batchDelete(List<Serializable> paramList);

  IPageList list(IQueryObject paramIQueryObject);

  boolean update(GroupPriceRange paramGroupPriceRange);

  List<GroupPriceRange> query(String paramString, Map paramMap, int paramInt1, int paramInt2);
}



 
 