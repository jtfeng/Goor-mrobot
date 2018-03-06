package cn.muye.erp.operation.mapper;

import cn.mrobot.bean.erp.operation.OperationDefaultApplianceXREF;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Jelynn
 * @date 2017/12/19
 */
public interface OperationDefaultApplianceXREFMapper {

    List<OperationDefaultApplianceXREF> findByOperationId(@Param("operationId") Long operationId);

    void save(OperationDefaultApplianceXREF operationDefaultApplianceXREF);

    void deleteByOperationId(@Param("operationTypeId") Long operationTypeId);

    void deleteByApplianceId(@Param("applianceId") Long applianceId);

    /**
     * 根据operationTypeId 和 applianceId 查询数据，更气对应的数量
     *
     * @param operationDefaultApplianceXREF
     */
    void updateNumber(OperationDefaultApplianceXREF operationDefaultApplianceXREF);
}
