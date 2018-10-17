package com.shopping.foundation.service;

import com.shopping.core.query.support.IPageList;
import com.shopping.core.query.support.IQueryObject;
import com.shopping.foundation.domain.SysLog;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface ISysLogService
{
  boolean save(SysLog paramSysLog);

  SysLog getObjById(Long paramLong);

  boolean delete(Long paramLong);

  boolean batchDelete(List<Serializable> paramList);

  IPageList list(IQueryObject paramIQueryObject);

  boolean update(SysLog paramSysLog);

  List<SysLog> query(String paramString, Map paramMap, int paramInt1, int paramInt2);
}



 
 