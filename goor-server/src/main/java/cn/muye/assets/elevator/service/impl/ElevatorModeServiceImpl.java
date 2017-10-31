package cn.muye.assets.elevator.service.impl;

import cn.mrobot.bean.assets.elevator.*;
import cn.mrobot.bean.assets.robot.Robot;
import cn.mrobot.utils.WhereRequest;
import cn.muye.assets.elevator.mapper.ElevatorModeMapper;
import cn.muye.assets.elevator.service.ElevatorModeService;
import cn.muye.base.service.imp.BaseServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
@Transactional
public class ElevatorModeServiceImpl extends BaseServiceImpl<ElevatorMode> implements ElevatorModeService {

    @Autowired
    private ElevatorModeMapper elevatorModeMapper;
    private static final Logger log = LoggerFactory.getLogger(ElevatorModeServiceImpl.class);

    @Override
    public List<ElevatorMode> listElevatorModesByElevatorId(Long elevatorId) throws Exception {
        List<ElevatorMode> modes = elevatorModeMapper.listElevatorModesByElevatorId(elevatorId);
        return modes;
    }
}
