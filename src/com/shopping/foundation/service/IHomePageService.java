package com.shopping.foundation.service;

import com.shopping.core.query.support.IPageList;
import com.shopping.core.query.support.IQueryObject;
import com.shopping.foundation.domain.HomePage;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface IHomePageService
{
  boolean save(HomePage paramHomePage);

  HomePage getObjById(Long paramLong);

  boolean delete(Long paramLong);

  boolean batchDelete(List<Serializable> paramList);

  IPageList list(IQueryObject paramIQueryObject);

  boolean update(HomePage paramHomePage);

  List<HomePage> query(String paramString, Map paramMap, int paramInt1, int paramInt2);
}



 
 