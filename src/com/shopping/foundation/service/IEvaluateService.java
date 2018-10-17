package com.shopping.foundation.service;

import com.shopping.core.query.support.IPageList;
import com.shopping.core.query.support.IQueryObject;
import com.shopping.foundation.domain.Evaluate;
import com.shopping.foundation.domain.Goods;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface IEvaluateService
{
  boolean save(Evaluate paramEvaluate);

  Evaluate getObjById(Long paramLong);

  boolean delete(Long paramLong);

  boolean batchDelete(List<Serializable> paramList);

  IPageList list(IQueryObject paramIQueryObject);

  boolean update(Evaluate paramEvaluate);

  List<Evaluate> query(String paramString, Map paramMap, int paramInt1, int paramInt2);

  List<Goods> query_goods(String paramString, Map paramMap, int paramInt1, int paramInt2);
}



 
 