package com.shopping.foundation.service;

import com.shopping.core.query.support.IPageList;
import com.shopping.core.query.support.IQueryObject;
import com.shopping.foundation.domain.RoleGroup;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface IRoleGroupService
{
  boolean save(RoleGroup paramRoleGroup);

  RoleGroup getObjById(Long paramLong);

  boolean delete(Long paramLong);

  boolean batchDelete(List<Serializable> paramList);

  IPageList list(IQueryObject paramIQueryObject);

  boolean update(RoleGroup paramRoleGroup);

  List<RoleGroup> query(String paramString, Map paramMap, int paramInt1, int paramInt2);

  RoleGroup getObjByProperty(String paramString, Object paramObject);
}



 
 