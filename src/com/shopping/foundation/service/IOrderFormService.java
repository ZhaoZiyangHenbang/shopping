package com.shopping.foundation.service;

import com.shopping.core.query.support.IPageList;
import com.shopping.core.query.support.IQueryObject;
import com.shopping.foundation.domain.OrderForm;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface IOrderFormService
{
  boolean save(OrderForm paramOrderForm);

  OrderForm getObjById(Long paramLong);

  boolean delete(Long paramLong);

  boolean batchDelete(List<Serializable> paramList);

  IPageList list(IQueryObject paramIQueryObject);

  IPageList allList(IQueryObject paramIQueryObject);

  boolean update(OrderForm paramOrderForm);

  List<OrderForm> query(String paramString, Map paramMap, int paramInt1, int paramInt2);

  IPageList groupByOrderId(IQueryObject properties);
}



 
 