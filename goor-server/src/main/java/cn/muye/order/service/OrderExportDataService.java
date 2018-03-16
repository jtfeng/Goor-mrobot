package cn.muye.order.service;

import cn.muye.order.bean.export.OrderExportData;

import java.util.Date;

/**
 * Created by Selim on 2018/3/12.
 */
public interface OrderExportDataService {

    //获取3张表的全部数据
    OrderExportData getOrderExportData(Long stationId, Date startDate, Date endDate);

}
