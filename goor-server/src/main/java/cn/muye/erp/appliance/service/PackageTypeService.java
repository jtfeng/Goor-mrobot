package cn.muye.erp.appliance.service;

import cn.mrobot.bean.erp.appliance.PackageType;

import java.util.List;

/**
 *
 * @author Jelynn
 * @date 2017/12/5
 */
public interface PackageTypeService{

    PackageType findByName(String name);

    PackageType findByCode(int code);

    List<PackageType> listAll();

    void save(PackageType packageType);
}
