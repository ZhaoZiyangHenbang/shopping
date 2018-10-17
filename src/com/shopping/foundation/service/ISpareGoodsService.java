package com.shopping.foundation.service;

import com.shopping.core.query.support.IPageList;
import com.shopping.core.query.support.IQueryObject;
import com.shopping.foundation.domain.SpareGoods;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface ISpareGoodsService
{
  boolean save(SpareGoods paramSpareGoods);

  SpareGoods getObjById(Long paramLong);

  boolean delete(Long paramLong);

  boolean batchDelete(List<Serializable> paramList);

  IPageList list(IQueryObject paramIQueryObject);

  boolean update(SpareGoods paramSpareGoods);

  List<SpareGoods> query(String paramString, Map paramMap, int paramInt1, int paramInt2);
}



 
 