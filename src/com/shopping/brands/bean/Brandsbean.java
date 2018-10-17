package com.shopping.brands.bean;

import com.shopping.core.domain.IdEntity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;



import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by Administrator on 2017/3/24.
 */
@Cache(usage= CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name="shopping_brands")
public class Brandsbean extends IdEntity {
    //品牌名字
    private String name;
    //品牌电话
    private String tel;
    //品牌描述
    private String descripe;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getDescripe() {
        return descripe;
    }

    public void setDescripe(String descripe) {
        this.descripe = descripe;
    }
}
