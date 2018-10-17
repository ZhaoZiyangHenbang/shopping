 package com.shopping.foundation.domain;
 
 import java.util.Date;
 import javax.persistence.Entity;
 import javax.persistence.FetchType;
 import javax.persistence.ManyToOne;
 import javax.persistence.OneToOne;
 import javax.persistence.Table;
 import org.hibernate.annotations.Cache;
 import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.shopping.core.domain.IdEntity;
 
 @Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
 @Entity
 @Table(name="shopping_visit")
 public class Visit extends IdEntity
 {
 private String vistName;
 private String v_type;

     public String getV_type() {
         return v_type;
     }

     public void setV_type(String v_type) {
         this.v_type = v_type;
     }

     private String vistDescript;

     public String getVistDescript() {
         return vistDescript;
     }

     public void setVistDescript(String vistDescript) {
         this.vistDescript = vistDescript;
     }

     public String getVistTel() {
         return vistTel;
     }

     public void setVistTel(String vistTel) {
         this.vistTel = vistTel;
     }

     public String getVistName() {
         return vistName;
     }

     public void setVistName(String vistName) {
         this.vistName = vistName;
     }

     private String vistTel;

   //主页
   @ManyToOne(fetch=FetchType.LAZY)
   private HomePage homepage;
 
   //用户
   @OneToOne(fetch=FetchType.LAZY)
   private User user;
   //浏览时间
   private Date visitTime;
 
   public HomePage getHomepage()
   {
     return this.homepage;
   }
 
   public void setHomepage(HomePage homepage) {
     this.homepage = homepage;
   }
 
   public User getUser() {
     return this.user;
   }
 
   public void setUser(User user) {
     this.user = user;
   }
 
   public Date getVisitTime() {
     return this.visitTime;
   }
 
   public void setVisitTime(Date visitTime) {
     this.visitTime = visitTime;
   }
 }



 
 