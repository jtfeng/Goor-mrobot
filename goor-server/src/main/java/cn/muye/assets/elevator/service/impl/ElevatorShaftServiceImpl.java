package cn.muye.assets.elevator.service.impl;

import cn.mrobot.bean.assets.elevator.Elevator;
import cn.mrobot.bean.assets.elevator.ElevatorShaft;
import cn.mrobot.utils.WhereRequest;
import cn.muye.assets.elevator.mapper.ElevatorMapper;
import cn.muye.assets.elevator.mapper.ElevatorShaftMapper;
import cn.muye.assets.elevator.service.ElevatorService;
import cn.muye.assets.elevator.service.ElevatorShaftService;
import cn.muye.base.service.imp.BaseServiceImpl;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import static com.google.common.base.Preconditions.*;

import java.util.List;

@Service
@Transactional
public class ElevatorShaftServiceImpl extends BaseServiceImpl<ElevatorShaft> implements ElevatorShaftService {

    private static final Logger log = LoggerFactory.getLogger(ElevatorShaftServiceImpl.class);
    @Autowired
    private ElevatorMapper elevatorMapper;
    @Autowired
    private ElevatorShaftMapper elevatorShaftMapper;

    @Override
    public List<ElevatorShaft> listElevatorShafts(WhereRequest whereRequest) throws Exception {
        List<ElevatorShaft> elevatorShafts = this.listPageByStoreIdAndOrder(whereRequest.getPage(),
                whereRequest.getPageSize(), ElevatorShaft.class,"ID DESC");
        return elevatorShafts;
    }

    @Override
    public List<ElevatorShaft> listPageByStoreIdAndOrder(int page, int pageSize, Class<ElevatorShaft> clazz, String order) {
        List<ElevatorShaft> elevatorShafts = super.listPageByStoreIdAndOrder(page, pageSize, clazz, order);
        for (ElevatorShaft elevatorShaft:elevatorShafts){
            try {
                checkNotNull(elevatorShaft.getId());
                log.info(" - - - - - ");
                log.info(String.format("电梯井的 ID 编号为：%s", elevatorShaft.getId()));
                List<Elevator> elevators = this.elevatorMapper.select(new Elevator(){{
                    setElevatorshaftId(elevatorShaft.getId());
                }});
                log.info(String.format("该电梯井下属的电梯有：%s", elevators.toString()));
                log.info(" - - - - - ");
                elevatorShaft.setElevators(elevators);
            }catch (Exception e){
                elevatorShaft.setElevators(Lists.newArrayList());
            }
        }
        return elevatorShafts;
    }
}
