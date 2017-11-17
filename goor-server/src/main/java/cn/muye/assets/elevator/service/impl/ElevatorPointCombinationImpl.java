package cn.muye.assets.elevator.service.impl;

import cn.mrobot.bean.area.point.MapPoint;
import cn.mrobot.bean.assets.elevator.Elevator;
import cn.mrobot.bean.assets.elevator.ElevatorPointCombination;
import cn.mrobot.bean.assets.elevator.ElevatorShaft;
import cn.mrobot.utils.StringUtil;
import cn.mrobot.utils.WhereRequest;
import cn.muye.area.station.service.impl.StationMapPointXREFServiceImpl;
import cn.muye.assets.elevator.mapper.ElevatorMapper;
import cn.muye.assets.elevator.mapper.ElevatorPointCombinationMapper;
import cn.muye.assets.elevator.mapper.ElevatorShaftMapper;
import cn.muye.assets.elevator.mapper.MapPointMapper;
import cn.muye.assets.elevator.service.ElevatorPointCombinationService;
import cn.muye.assets.elevator.service.ElevatorService;
import cn.muye.base.bean.SearchConstants;
import cn.muye.base.service.imp.BaseServiceImpl;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sun.rmi.runtime.Log;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

@Service
@Transactional
public class ElevatorPointCombinationImpl extends BaseServiceImpl<ElevatorPointCombination> implements ElevatorPointCombinationService {

    private static final Logger log = LoggerFactory.getLogger(ElevatorPointCombinationImpl.class);
    @Autowired
    private MapPointMapper mapPointMapper;
    @Autowired
    private ElevatorPointCombinationMapper elevatorPointCombinationMapper;
    @Autowired
    private ElevatorService elevatorService;

    @Override
    public boolean checkCreateCondition(List<Long> mappointIds) throws Exception {
//        checkArgument(mappointIds.size() == Sets.newHashSet(mappointIds).size(),
//                "四个点不能是相同的点，请重新选择!");
        Set<String> strings = Sets.newHashSet();
        for (Long id:mappointIds){
            MapPoint point = mapPointMapper.selectByPrimaryKey(id);
            strings.add(point.getStoreId() + ":" + point.getMapName());
        }
        checkArgument(strings.size() == 1, "四个点不属于同一张地图，请重新选择!");
        return true;
    }

    @Override
    public List<ElevatorPointCombination> listElevatorPointCombinations(WhereRequest whereRequest) throws Exception {
//        List<ElevatorPointCombination> combinations = this.listPageByStoreIdAndOrder(whereRequest.getPage(),
//                whereRequest.getPageSize(), ElevatorPointCombination.class,"ID DESC");
        PageHelper.startPage(whereRequest.getPage(), whereRequest.getPageSize());
        Example example = new Example(ElevatorPointCombination.class);
        example.createCriteria()
                .andCondition("STORE_ID =", SearchConstants.FAKE_MERCHANT_STORE_ID)
                .andCondition("SCENE_ID=", whereRequest.getQueryObj());
        example.setOrderByClause("ID DESC");
        List<ElevatorPointCombination> combinations = myMapper.selectByExample(example);
        packageDataBindMappoint(combinations);
        packageDataBindElevator(combinations);
        return combinations;
    }

    @Override
    public List<ElevatorPointCombination> findByElevatorId(Long elevatorId) throws Exception {
        List<ElevatorPointCombination> combinations = elevatorPointCombinationMapper.findByElevatorId(elevatorId);
        packageDataBindMappoint(combinations);
        return combinations;
    }

    @Override
    public List<ElevatorPointCombination> listPageByStoreIdAndOrder(int page, int pageSize, Class<ElevatorPointCombination> clazz, String order) {


        PageHelper.startPage(page, pageSize);
        Example example = new Example(clazz);
        example.createCriteria().andCondition("STORE_ID =", SearchConstants.FAKE_MERCHANT_STORE_ID);
        if(!StringUtil.isNullOrEmpty(order)){
            example.setOrderByClause(order);
        }
        List<ElevatorPointCombination> combinations = myMapper.selectByExample(example);


        packageDataBindMappoint(combinations);
        packageDataBindElevator(combinations);
        return combinations;
    }

    private void packageDataBindMappoint(List<ElevatorPointCombination> combinations){
        for (ElevatorPointCombination combination:combinations){
            log.info(" - - - - - ");
            Long mapPointId = 0L;MapPoint mapPoint = null;
            try {
                checkNotNull((mapPointId = combination.getWaitPoint()));
                System.out.println(this.mapPointMapper.selectByPrimaryKey(mapPointId));
                log.info("等待点：" + (mapPoint = this.mapPointMapper.selectByPrimaryKey(mapPointId)));
                combination.setwPoint(mapPoint);
            }catch (Exception e){ combination.setwPoint(null); }
            try {
                checkNotNull((mapPointId = combination.getGoPoint()));
                log.info("进入点：" + (mapPoint = this.mapPointMapper.selectByPrimaryKey(mapPointId)));
                combination.setgPoint(mapPoint);
            }catch (Exception e){ combination.setwPoint(null); }
            try {
                checkNotNull((mapPointId = combination.getOutPoint()));
                log.info("出去点：" + (mapPoint = this.mapPointMapper.selectByPrimaryKey(mapPointId)));
                combination.setoPoint(mapPoint);
            }catch (Exception e){ combination.setwPoint(null); }
            try {
                checkNotNull((mapPointId = combination.getInnerPoint()));
                log.info("内部点：" + (mapPoint = this.mapPointMapper.selectByPrimaryKey(mapPointId)));
                combination.setiPoint(mapPoint);
            }catch (Exception e){ combination.setwPoint(null); }
            log.info(" - - - - - ");
        }
    }

    private void packageDataBindElevator(List<ElevatorPointCombination> combinations){
        for (ElevatorPointCombination combination:combinations){
            combination.setElevators(elevatorService.findByElevatorPointCombinationId(combination.getId()));
        }
    }

    @Override
    public ElevatorPointCombination findById(Long id) {
        ElevatorPointCombination combination = super.findById(id);
        packageDataBindMappoint(Lists.newArrayList(combination));
        packageDataBindElevator(Lists.newArrayList(combination));
        return combination;
    }
}