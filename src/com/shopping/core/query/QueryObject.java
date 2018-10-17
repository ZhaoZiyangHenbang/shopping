package com.shopping.core.query;


import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.springframework.web.servlet.ModelAndView;

import com.shopping.core.domain.virtual.SysMap;
import com.shopping.core.query.support.IQueryObject;
import com.shopping.core.tools.CommUtil;

public class QueryObject
  implements IQueryObject
{
  protected Integer pageSize = Integer.valueOf(12);
  protected Integer pageSize3 = Integer.valueOf(3);
  protected Integer currentPage = Integer.valueOf(0);
  protected String orderBy;
  protected String groupBy;
  protected String orderType;
  protected Map params = new HashMap();

  protected String queryString = "1=1";

  public Integer getPageSize3() {
    return pageSize3;
  }

  public void setPageSize3(Integer pageSize3) {
    this.pageSize3 = pageSize3;
  }

  public void setCurrentPage(Integer currentPage) {
    this.currentPage = currentPage;
  }

  public void setOrderBy(String orderBy) {
    this.orderBy = orderBy;
  }

  public String getGroupBy() {
    return groupBy;
  }

  public void setGroupBy(String groupBy) {
    this.groupBy = groupBy;
  }

  public void setOrderType(String orderType) {
    this.orderType = orderType;
  }

  public void setPageSize(Integer pageSize) {
    this.pageSize = pageSize;
  }

  protected void setParams(Map params) {
    this.params = params;
  }

  public String getOrderType() {
    return this.orderType;
  }

  public Integer getCurrentPage() {
    if (this.currentPage == null) {
      this.currentPage = Integer.valueOf(-1);
    }
    return this.currentPage;
  }
  public String getOrder() {
    return this.orderType;
  }

  public String getOrderBy() {
    return this.orderBy;
  }

  public Integer getPageSize() {
    if (this.pageSize == null) {
      this.pageSize = Integer.valueOf(-1);
    }
    return this.pageSize;
  }

  public QueryObject()
  {
  }

  public QueryObject(String currentPage, ModelAndView mv, String orderBy, String orderType)
  {
    if ((currentPage != null) && (!currentPage.equals("")))
      setCurrentPage(Integer.valueOf(CommUtil.null2Int(currentPage)));
    setPageSize(this.pageSize);
    if ((orderBy == null) || (orderBy.equals(""))) {
      setOrderBy("addTime");
      mv.addObject("orderBy", "addTime");
    } else {
      setOrderBy(orderBy);
      mv.addObject("orderBy", orderBy);
    }
    if ((orderType == null) || (orderType.equals("desc"))) {
      setOrderType("desc");
      mv.addObject("orderType", "desc");
    } else {
      setOrderType(orderType);
      mv.addObject("orderType", orderType);
    }
  }
  
  public QueryObject(String currentPage, Map<String, Object> map, String orderBy, String orderType)
  {
	  if ((currentPage != null) && (!currentPage.equals("")))
	      setCurrentPage(Integer.valueOf(CommUtil.null2Int(currentPage)));
	  
	    setPageSize(this.pageSize);
	    
	    if ((orderBy == null) || (orderBy.equals(""))) {
	      setOrderBy("addTime");
	      map.put("orderBy", "addTime");
	    } else {
	      setOrderBy(orderBy);
	      map.put("orderBy", orderBy);
	    }
	    if ((orderType == null) || (orderType.equals("desc"))) {
	      setOrderType("desc");
	      map.put("orderType", "desc");
	    } else {
	      setOrderType(orderType);
	      map.put("orderType", orderType);
	    }
  }

  public PageObject getPageObj() {
    PageObject pageObj = new PageObject();
    pageObj.setCurrentPage(getCurrentPage());
    pageObj.setPageSize(getPageSize());
    if ((this.currentPage == null) || (this.currentPage.intValue() <= 0)) {
      pageObj.setCurrentPage(Integer.valueOf(1));
    }
    return pageObj;
  }

  public PageObject getPageObj3() {
    PageObject pageObj = new PageObject();
    pageObj.setCurrentPage(getCurrentPage());
    pageObj.setPageSize(getPageSize3());
    if ((this.currentPage == null) || (this.currentPage.intValue() <= 0)) {
      pageObj.setCurrentPage(Integer.valueOf(1));
    }
    return pageObj;
  }

  public String getQuery() {
    customizeQuery();
    return this.queryString + orderString();
  }
  public String getGroupQuery() {
    customizeQuery();
    return this.queryString + groupString();
  }

  protected String groupString() {
    String groupString = " ";
    if ((getGroupBy() != null) && (!"".equals(getGroupBy()))) {
      groupString = groupString + " group by obj." + getGroupBy();
    }
    if ((getOrderBy() != null) && (!"".equals(getOrderBy()))) {
      groupString = groupString + " order by obj." + getOrderBy();
    }
    if ((getOrderType() != null) && (!"".equals(getOrderType()))) {
      groupString = groupString + " " + getOrderType();
    }
    return groupString;
  }

  protected String orderString() {
    String orderString = " ";
    if ((getOrderBy() != null) && (!"".equals(getOrderBy()))) {
      orderString = orderString + " order by obj." + getOrderBy();
    }
    if ((getOrderType() != null) && (!"".equals(getOrderType()))) {
      orderString = orderString + " " + getOrderType();
    }
    return orderString;
  }

  public Map getParameters() {
    return this.params;
  }

  public IQueryObject addQuery(String field, SysMap para, String expression) {
    if ((field != null) && (para != null)) {
      this.queryString = (this.queryString + " and " + field + " " + handleExpression(expression) + ":" + para.getKey().toString());
      this.params.put(para.getKey(), para.getValue());
    }
    return this;
  }

  public IQueryObject addQueryIn(String field, SysMap para, String expression) {
    if ((field != null) && (para != null)) {
      this.queryString = (this.queryString + " and " + field + " " + handleExpression(expression) + "(:"+para.getKey().toString()+")");
      this.params.put(para.getKey(), para.getValue());
    }
    return this;
  }

  public IQueryObject addQuery(String field, SysMap para, String expression, String logic)
  {
    if ((field != null) && (para != null)) {
      this.queryString = 
        (this.queryString + " " + logic + " " + field + " " + 
        handleExpression(expression) + ":" + 
        para.getKey().toString());
      this.params.put(para.getKey(), para.getValue());
    }
    return this;
  }

  public IQueryObject addQuery(String scope, Map paras) {
    if (scope != null) {
      if ((scope.trim().indexOf("and") == 0) || 
        (scope.trim().indexOf("or") == 0))
        this.queryString = (this.queryString + " " + scope);
      else
        this.queryString = (this.queryString + " and " + scope);
      if ((paras != null) && (paras.size() > 0)) {
        for (Iterator localIterator = paras.keySet().iterator(); localIterator.hasNext(); ) { Object key = localIterator.next();
          this.params.put(key, paras.get(key));
        }
      }
    }
    return this;
  }

  public IQueryObject addQuery(String para, Object obj, String field, String expression)
  {
    if ((field != null) && (para != null)) {
      this.queryString = (this.queryString + " and :" + para + " " + expression + " " + field);
      this.params.put(para, obj);
    }
    return this;
  }

  public IQueryObject addQuery(String para, Object obj, String field, String expression, String logic)
  {
    if ((field != null) && (para != null)) {
      this.queryString = 
        (this.queryString + " " + logic + " :" + para + " " + expression + " " + 
        field);
      this.params.put(para, obj);
    }
    return this;
  }

  private String handleExpression(String expression) {
    if (expression == null) {
      return "=";
    }
    return expression;
  }

  public void customizeQuery()
  {
  }

  public  QueryObject(String currentPage,ModelAndView mv, String groupBy, String orderBy, String orderType){
    if ((currentPage != null) && (!currentPage.equals("")))
      setCurrentPage(Integer.valueOf(CommUtil.null2Int(currentPage)));
      setPageSize(this.pageSize);
    if ((groupBy != null) || (!groupBy.equals(""))) {
      setGroupBy(groupBy);
      mv.addObject("groupBy", groupBy);
    }
    if ((orderBy == null) || (orderBy.equals(""))) {
      setOrderBy("addTime");
      mv.addObject("orderBy", "addTime");
    } else {
      setOrderBy(orderBy);
      mv.addObject("orderBy", orderBy);
    }
    if ((orderType == null) || (orderType.equals("desc"))) {
      setOrderType("desc");
      mv.addObject("orderType", "desc");
    } else {
      setOrderType(orderType);
      mv.addObject("orderType", orderType);
    }
  }
}