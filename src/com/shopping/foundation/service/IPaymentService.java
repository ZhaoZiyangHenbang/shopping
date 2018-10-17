package com.shopping.foundation.service;

import com.shopping.core.query.support.IPageList;
import com.shopping.core.query.support.IQueryObject;
import com.shopping.foundation.domain.Payment;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface IPaymentService
{
  boolean save(Payment paramPayment);

  Payment getObjById(Long paramLong);

  boolean delete(Long paramLong);

  boolean batchDelete(List<Serializable> paramList);

  IPageList list(IQueryObject paramIQueryObject);

  boolean update(Payment paramPayment);

  List<Payment> query(String paramString, Map paramMap, int paramInt1, int paramInt2);

  Payment getObjByProperty(String paramString1, String paramString2);
}



 
 