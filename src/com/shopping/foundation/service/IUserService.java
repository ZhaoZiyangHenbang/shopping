package com.shopping.foundation.service;

import com.shopping.core.query.support.IPageList;
import com.shopping.core.query.support.IQueryObject;
import com.shopping.foundation.domain.User;
import java.util.List;
import java.util.Map;

public interface IUserService
{
  boolean save(User paramUser);

  boolean delete(Long paramLong);

  boolean update(User paramUser);

  IPageList list(IQueryObject paramIQueryObject);

  User getObjById(Long paramLong);

  User getObjByProperty(String paramString1, String paramString2);

  List<User> query(String paramString, Map paramMap, int paramInt1, int paramInt2);

}



 
 