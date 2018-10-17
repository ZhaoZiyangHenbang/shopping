package com.shopping.foundation.service;

import com.shopping.core.query.support.IPageList;
import com.shopping.core.query.support.IQueryObject;
import com.shopping.foundation.domain.Accessory;
import java.util.List;
import java.util.Map;

public interface IAccessoryService
{
  boolean save(Accessory paramAccessory);

  boolean delete(Long paramLong);

  boolean update(Accessory paramAccessory);

  IPageList list(IQueryObject paramIQueryObject);

  Accessory getObjById(Long paramLong);

  Accessory getObjByProperty(String paramString1, String paramString2);

  List<Accessory> query(String paramString, Map paramMap, int paramInt1, int paramInt2);
}



 
 