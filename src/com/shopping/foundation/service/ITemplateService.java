package com.shopping.foundation.service;

import com.shopping.core.query.support.IPageList;
import com.shopping.core.query.support.IQueryObject;
import com.shopping.foundation.domain.Template;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface ITemplateService
{
  boolean save(Template paramTemplate);

  Template getObjById(Long paramLong);

  boolean delete(Long paramLong);

  boolean batchDelete(List<Serializable> paramList);

  IPageList list(IQueryObject paramIQueryObject);

  boolean update(Template paramTemplate);

  List<Template> query(String paramString, Map paramMap, int paramInt1, int paramInt2);

  Template getObjByProperty(String paramString, Object paramObject);
}



 
 