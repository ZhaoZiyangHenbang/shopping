package com.shopping.foundation.service;

import com.shopping.core.query.support.IPageList;
import com.shopping.core.query.support.IQueryObject;
import com.shopping.foundation.domain.ArticleClass;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface IArticleClassService
{
  boolean save(ArticleClass paramArticleClass);

  ArticleClass getObjById(Long paramLong);

  boolean delete(Long paramLong);

  boolean batchDelete(List<Serializable> paramList);

  IPageList list(IQueryObject paramIQueryObject);

  boolean update(ArticleClass paramArticleClass);

  List<ArticleClass> query(String paramString, Map paramMap, int paramInt1, int paramInt2);

  ArticleClass getObjByPropertyName(String paramString, Object paramObject);
}



 
 