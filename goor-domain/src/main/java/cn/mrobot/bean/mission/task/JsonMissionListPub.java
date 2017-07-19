package cn.mrobot.bean.mission.task;

import cn.mrobot.dto.mission.MissionListDTO;

import java.io.Serializable;
import java.util.List;

/**
 * Created by abel on 17-7-18.
 */
public class JsonMissionListPub implements Serializable {
    private static final long serialVersionUID = -9064286601055679851L;

    private List<MissionListDTO> missionLists;
    private Long sendTime;
    private String uuid;

    public List<MissionListDTO> getMissionLists() {
        return missionLists;
    }

    public void setMissionLists(List<MissionListDTO> missionLists) {
        this.missionLists = missionLists;
    }

    public Long getSendTime() {
        return sendTime;
    }

    public void setSendTime(Long sendTime) {
        this.sendTime = sendTime;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
