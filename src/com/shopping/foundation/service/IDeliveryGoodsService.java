package com.shopping.foundation.service;

import com.shopping.core.query.support.IPageList;
import com.shopping.core.query.support.IQueryObject;
import com.shopping.foundation.domain.DeliveryGoods;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface IDeliveryGoodsService
{
  boolean save(DeliveryGoods paramDeliveryGoods);

  DeliveryGoods getObjById(Long paramLong);

  boolean delete(Long paramLong);

  boolean batchDelete(List<Serializable> paramList);

  IPageList list(IQueryObject paramIQueryObject);

  boolean update(DeliveryGoods paramDeliveryGoods);

  List<DeliveryGoods> query(String paramString, Map paramMap, int paramInt1, int paramInt2);
}



 
 