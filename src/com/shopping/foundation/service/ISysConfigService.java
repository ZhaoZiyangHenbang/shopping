package com.shopping.foundation.service;

import com.shopping.foundation.domain.SysConfig;

public interface ISysConfigService
{
  boolean save(SysConfig paramSysConfig);

  boolean delete(SysConfig paramSysConfig);

  boolean update(SysConfig paramSysConfig);

  SysConfig getSysConfig();
}



 
 