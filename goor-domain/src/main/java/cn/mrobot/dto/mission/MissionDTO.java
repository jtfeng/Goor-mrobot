package cn.mrobot.dto.mission;

import cn.mrobot.bean.mission.MissionItem;

import java.io.Serializable;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * Project Name : goor-domain
 * User: Chay
 * Date: 2017/7/3
 * Time: 16:40
 * Describe:  任务 (子任务组成)
 * Version:1.0
 */
public class MissionDTO implements Serializable {

	private Long id;

	private int repeatCount;

	private Long intervalTime;

	private Set<MissionItemDTO> missionItemDTOSet;

	public MissionDTO() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public int getRepeatCount() {
		return repeatCount;
	}

	public void setRepeatCount(int repeatCount) {
		this.repeatCount = repeatCount;
	}

	public Long getIntervalTime() {
		return intervalTime;
	}

	public void setIntervalTime(Long intervalTime) {
		this.intervalTime = intervalTime;
	}

	public Set<MissionItemDTO> getMissionItemDTOSet() {
		return missionItemDTOSet;
	}

	public void setMissionItemDTOSet(Set<MissionItemDTO> missionItemDTOSet) {
		this.missionItemDTOSet = missionItemDTOSet;
	}
}
