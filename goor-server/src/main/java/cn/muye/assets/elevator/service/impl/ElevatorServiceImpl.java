package cn.muye.assets.elevator.service.impl;

import cn.mrobot.bean.assets.elevator.Elevator;
import cn.mrobot.bean.assets.elevator.ElevatorShaft;
import cn.mrobot.bean.assets.scene.Scene;
import cn.mrobot.utils.WhereRequest;
import cn.muye.assets.elevator.mapper.ElevatorMapper;
import cn.muye.assets.elevator.mapper.ElevatorShaftMapper;
import cn.muye.assets.elevator.service.ElevatorService;
import cn.muye.base.service.imp.BaseServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import static com.google.common.base.Preconditions.*;

import java.util.List;

@Service
@Transactional
public class ElevatorServiceImpl extends BaseServiceImpl<Elevator> implements ElevatorService {

    private static final Logger log = LoggerFactory.getLogger(ElevatorServiceImpl.class);
    @Autowired
    private ElevatorMapper elevatorMapper;
    @Autowired
    private ElevatorShaftMapper elevatorShaftMapper;

    @Override
    public List<Elevator> listElevators(WhereRequest whereRequest) throws Exception {
        List<Elevator> elevators = this.listPageByStoreIdAndOrder(whereRequest.getPage(),
                whereRequest.getPageSize(), Elevator.class,"ID DESC");
        return elevators;
    }

    @Override
    public List<Elevator> listPageByStoreIdAndOrder(int page, int pageSize, Class<Elevator> clazz, String order) {
        List<Elevator> elevators = super.listPageByStoreIdAndOrder(page, pageSize, clazz, order);
        for (Elevator elevator:elevators){
            try {
                checkNotNull(elevator.getElevatorshaftId());
                log.info(" - - - - - ");
                log.info(String.format("所属电梯井的编号为：%d", elevator.getElevatorshaftId()));
                ElevatorShaft elevatorShaft = this.elevatorShaftMapper.selectByPrimaryKey(elevator.getElevatorshaftId());
                log.info(String.format("所属电梯井的信息为：%s",elevatorShaft.toString()));
                elevator.setElevatorShaft(elevatorShaft);
                log.info(" - - - - - ");
            }catch (Exception e){
                elevator.setElevatorShaft(null);
            }
        }
        return elevators;
    }
}
