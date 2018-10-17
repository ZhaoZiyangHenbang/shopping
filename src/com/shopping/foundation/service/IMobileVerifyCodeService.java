package com.shopping.foundation.service;

import com.shopping.core.query.support.IPageList;
import com.shopping.core.query.support.IQueryObject;
import com.shopping.foundation.domain.MobileVerifyCode;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface IMobileVerifyCodeService
{
  boolean save(MobileVerifyCode paramMobileVerifyCode);

  MobileVerifyCode getObjById(Long paramLong);

  boolean delete(Long paramLong);

  boolean batchDelete(List<Serializable> paramList);

  IPageList list(IQueryObject paramIQueryObject);

  boolean update(MobileVerifyCode paramMobileVerifyCode);

  List<MobileVerifyCode> query(String paramString, Map paramMap, int paramInt1, int paramInt2);

  MobileVerifyCode getObjByProperty(String paramString, Object paramObject);
}



 
 