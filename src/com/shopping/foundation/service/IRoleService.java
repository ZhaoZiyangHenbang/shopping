package com.shopping.foundation.service;

import com.shopping.core.query.support.IPageList;
import com.shopping.core.query.support.IQueryObject;
import com.shopping.foundation.domain.Role;
import java.util.List;
import java.util.Map;

public interface IRoleService
{
  boolean save(Role paramRole);

  boolean delete(Long paramLong);

  boolean update(Role paramRole);

  Role getObjById(Long paramLong);

  List<Role> query(String paramString, Map paramMap, int paramInt1, int paramInt2);

  IPageList list(IQueryObject paramIQueryObject);

  Role getObjByProperty(String paramString, Object paramObject);
}



 
 