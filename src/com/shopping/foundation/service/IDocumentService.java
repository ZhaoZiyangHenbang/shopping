package com.shopping.foundation.service;

import com.shopping.core.query.support.IPageList;
import com.shopping.core.query.support.IQueryObject;
import com.shopping.foundation.domain.Document;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface IDocumentService
{
  boolean save(Document paramDocument);

  Document getObjById(Long paramLong);

  boolean delete(Long paramLong);

  boolean batchDelete(List<Serializable> paramList);

  IPageList list(IQueryObject paramIQueryObject);

  boolean update(Document paramDocument);

  List<Document> query(String paramString, Map paramMap, int paramInt1, int paramInt2);

  Document getObjByProperty(String paramString, Object paramObject);
}



 
 