package com.shopping.foundation.service;

import com.shopping.core.query.support.IPageList;
import com.shopping.core.query.support.IQueryObject;
import com.shopping.foundation.domain.Goods;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface IGoodsService
{
  boolean save(Goods paramGoods);

  Goods getObjById(Long paramLong);

  boolean delete(Long paramLong);

  boolean batchDelete(List<Serializable> paramList);

  IPageList list(IQueryObject paramIQueryObject);

  boolean update(Goods paramGoods);

  List<Goods> query(String paramString, Map paramMap, int paramInt1, int paramInt2);

  Goods getObjByProperty(String paramString, Object paramObject);
}



 
 