package cn.muye.assets.rfidbracelet.service.impl;

import cn.mrobot.bean.assets.rfidbracelet.RfidBracelet;
import cn.mrobot.bean.assets.robot.Robot;
import cn.mrobot.bean.assets.shelf.Shelf;
import cn.mrobot.utils.WhereRequest;
import cn.muye.assets.rfidbracelet.controller.RfidBraceletController;
import cn.muye.assets.rfidbracelet.mapper.RfidBraceletMapper;
import cn.muye.assets.rfidbracelet.service.RfidBraceletService;
import cn.muye.base.service.imp.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;

/**
 * Created by admin on 2017/7/3.
 */
@Service
@Transactional
public class RfidBraceletServiceImpl extends BaseServiceImpl<RfidBracelet> implements RfidBraceletService {
    @Autowired
    private RfidBraceletMapper rfidBraceletMapper;

    @Override
    public List<RfidBracelet> list() {
        return rfidBraceletMapper.selectAll();
    }

    @Override
    public int save(RfidBracelet rfidBracelet) {
        int result = rfidBraceletMapper.insert(rfidBracelet);
        synchronized (RfidBraceletController.USER_DATA_SOURCES) {
            RfidBraceletController.USER_DATA_SOURCES.put(rfidBracelet.getBracblbtUsername(), 1);
        }
        return result;
    }

    @Override
    public RfidBracelet getById(String id) {
        return rfidBraceletMapper.selectByPrimaryKey(id);
    }

    @Override
    public int update(RfidBracelet rfidBracelet) {
        RfidBracelet bracelet =  findById(rfidBracelet.getId());
        int result = rfidBraceletMapper.updateByPrimaryKey(rfidBracelet) ;
        if (!bracelet.getBracblbtUsername().equals(rfidBracelet.getBracblbtUsername())){
            synchronized (RfidBraceletController.USER_DATA_SOURCES) {
                RfidBraceletController.USER_DATA_SOURCES.put(bracelet.getBracblbtUsername(), 0);
                RfidBraceletController.USER_DATA_SOURCES.put(rfidBracelet.getBracblbtUsername(), 1);
            }
        }
        return result;
    }

    @Override
    public int deleteById(Long id) {
        RfidBracelet rfidBracelet = findById(id);
        int result = rfidBraceletMapper.deleteByPrimaryKey(id) ;
        synchronized (RfidBraceletController.USER_DATA_SOURCES) {
            RfidBraceletController.USER_DATA_SOURCES.put(rfidBracelet.getBracblbtUsername(), 0);
        }
        return result;
    }

    @Override
    public List<RfidBracelet> listRfidBracelet(WhereRequest whereRequest) {
        List<RfidBracelet> list = listPageByStoreIdAndOrder(whereRequest.getPage(),
                whereRequest.getPageSize(),RfidBracelet.class,"ID DESC");
        return list;
    }
}