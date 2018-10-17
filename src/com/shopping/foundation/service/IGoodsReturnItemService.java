package com.shopping.foundation.service;

import com.shopping.core.query.support.IPageList;
import com.shopping.core.query.support.IQueryObject;
import com.shopping.foundation.domain.GoodsReturnItem;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface IGoodsReturnItemService
{
  boolean save(GoodsReturnItem paramGoodsReturnItem);

  GoodsReturnItem getObjById(Long paramLong);

  boolean delete(Long paramLong);

  boolean batchDelete(List<Serializable> paramList);

  IPageList list(IQueryObject paramIQueryObject);

  boolean update(GoodsReturnItem paramGoodsReturnItem);

  List<GoodsReturnItem> query(String paramString, Map paramMap, int paramInt1, int paramInt2);
}



 
 