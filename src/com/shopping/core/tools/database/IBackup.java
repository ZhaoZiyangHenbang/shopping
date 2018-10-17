package com.shopping.core.tools.database;

import java.sql.ResultSet;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

public interface IBackup
{
  boolean createSqlScript(HttpServletRequest paramHttpServletRequest, String paramString1, String paramString2, String paramString3, String paramString4)
    throws Exception;

  boolean executSqlScript(String paramString)
    throws Exception;

  List<String> getTables()
    throws Exception;

  String queryDatabaseVersion();

  boolean execute(String paramString);

  boolean export(String paramString1, String paramString2);

  ResultSet query(String paramString);
}
