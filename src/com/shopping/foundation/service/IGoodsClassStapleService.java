package com.shopping.foundation.service;

import com.shopping.core.query.support.IPageList;
import com.shopping.core.query.support.IQueryObject;
import com.shopping.foundation.domain.GoodsClassStaple;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface IGoodsClassStapleService
{
  boolean save(GoodsClassStaple paramGoodsClassStaple);

  GoodsClassStaple getObjById(Long paramLong);

  boolean delete(Long paramLong);

  boolean batchDelete(List<Serializable> paramList);

  IPageList list(IQueryObject paramIQueryObject);

  boolean update(GoodsClassStaple paramGoodsClassStaple);

  List<GoodsClassStaple> query(String paramString, Map paramMap, int paramInt1, int paramInt2);
}



 
 