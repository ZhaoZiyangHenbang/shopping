package com.shopping.foundation.service;

import com.shopping.core.query.support.IPageList;
import com.shopping.core.query.support.IQueryObject;
import com.shopping.foundation.domain.Album;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface IAlbumService
{
  boolean save(Album paramAlbum);

  Album getObjById(Long paramLong);

  boolean delete(Long paramLong);

  boolean batchDelete(List<Serializable> paramList);

  IPageList list(IQueryObject paramIQueryObject);

  boolean update(Album paramAlbum);

  List<Album> query(String paramString, Map paramMap, int paramInt1, int paramInt2);

  Album getDefaultAlbum(Long paramLong);
}



 
 