package com.shopping.foundation.service;

import com.shopping.core.query.support.IPageList;
import com.shopping.core.query.support.IQueryObject;
import com.shopping.foundation.domain.GoodsBrandCategory;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface IGoodsBrandCategoryService
{
  boolean save(GoodsBrandCategory paramGoodsBrandCategory);

  GoodsBrandCategory getObjById(Long paramLong);

  boolean delete(Long paramLong);

  boolean batchDelete(List<Serializable> paramList);

  IPageList list(IQueryObject paramIQueryObject);

  boolean update(GoodsBrandCategory paramGoodsBrandCategory);

  List<GoodsBrandCategory> query(String paramString, Map paramMap, int paramInt1, int paramInt2);

  GoodsBrandCategory getObjByProperty(String paramString, Object paramObject);
}



 
 