package com.shopping.brands.service;

import com.shopping.brands.bean.Brandsbean;
import com.shopping.core.dao.IGenericDAO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * Created by Administrator on 2017/3/25.
 */
@Service
@Transactional
public class BrandsServiceImpl implements BrandsService {

    @Resource(name="brandsDao")
    private IGenericDAO<Brandsbean> brandsDao;



    public boolean save(Brandsbean brandsbean) {
        try {
            this.brandsDao.save(brandsbean);
            return true; }
        catch (Exception e) {

        }
        return false;
    }
}
