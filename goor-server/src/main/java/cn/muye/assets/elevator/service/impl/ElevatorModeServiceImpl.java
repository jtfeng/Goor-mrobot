package cn.muye.assets.elevator.service.impl;

import cn.mrobot.bean.assets.elevator.*;
import cn.muye.assets.elevator.service.ElevatorModeService;
import cn.muye.base.service.imp.BaseServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ElevatorModeServiceImpl extends BaseServiceImpl<ElevatorMode> implements ElevatorModeService {

    private static final Logger log = LoggerFactory.getLogger(ElevatorModeServiceImpl.class);

}
