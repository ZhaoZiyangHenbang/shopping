package com.shopping.foundation.service;

import com.shopping.core.query.support.IPageList;
import com.shopping.core.query.support.IQueryObject;
import com.shopping.foundation.domain.RefundLog;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface IRefundLogService
{
  boolean save(RefundLog paramRefundLog);

  RefundLog getObjById(Long paramLong);

  boolean delete(Long paramLong);

  boolean batchDelete(List<Serializable> paramList);

  IPageList list(IQueryObject paramIQueryObject);

  boolean update(RefundLog paramRefundLog);

  List<RefundLog> query(String paramString, Map paramMap, int paramInt1, int paramInt2);
}



 
 