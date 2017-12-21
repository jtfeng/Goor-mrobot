package cn.muye.erp.operation.mapper;

import cn.mrobot.bean.erp.operation.OperationDefaultApplianceXREF;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 *
 * @author Jelynn
 * @date 2017/12/19
 */
public interface OperationDefaultApplianceXREFMapper {

    List<OperationDefaultApplianceXREF> findByOperationId(@Param("operationId") Long operationId);

    void save(OperationDefaultApplianceXREF operationDefaultApplianceXREF);

    void deleteByOperationId(@Param("operationTypeId") Long operationTypeId);
}
