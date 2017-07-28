package cn.muye.service.consumer.topic;

/**
 * Created by abel on 17-7-27.
 */
public interface X86MissionCommonRequestService {

    void sendX86MissionStateCommonRequest(
            String robotCode);

    void sendX86MissionQueueCommonRequest(
            String robotCode);

    void sendX86MissionStateCommonRequest();

    void sendX86MissionQueueCommonRequest();
}
