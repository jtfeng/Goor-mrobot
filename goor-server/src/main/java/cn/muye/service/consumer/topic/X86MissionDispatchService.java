package cn.muye.service.consumer.topic;

/**
 * Created by abel on 17-7-11.
 */
public interface X86MissionDispatchService {

    void sendX86MissionDispatch(
            String robotCode,
            String missionListData);
}
