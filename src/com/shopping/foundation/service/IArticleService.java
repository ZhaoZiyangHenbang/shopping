package com.shopping.foundation.service;

import com.shopping.core.query.support.IPageList;
import com.shopping.core.query.support.IQueryObject;
import com.shopping.foundation.domain.Article;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface IArticleService
{
  boolean save(Article paramArticle);

  Article getObjById(Long paramLong);

  boolean delete(Long paramLong);

  boolean batchDelete(List<Serializable> paramList);

  IPageList list(IQueryObject paramIQueryObject);

  boolean update(Article paramArticle);

  List<Article> query(String paramString, Map paramMap, int paramInt1, int paramInt2);

  Article getObjByProperty(String paramString, Object paramObject);
}



 
 