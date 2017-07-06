package cn.mrobot.dto.mission;

import cn.mrobot.bean.mission.Mission;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Project Name : goor-domain
 * User: Chay
 * Date: 2017/7/3
 * Time: 16:21
 * Describe: 任务列表
 * Version:1.0
 */
public class MissionListDTO implements Serializable{

	private Long id;

	private List<MissionDTO> missionDTOList;

	private String missionListType;
	/**
	 * //间隔时间
	 */
	private Long intervalTime;
	/**
	 * //循环次数  -1为无限循环
	 */
	private int repeatCount;

	/**
	 * //任务开始时间
	 */
	private Long startTime;

	/**
	 * //任务结束时间
	 */
	private Long stopTime;

	private Integer priority;//优先级

	public MissionListDTO() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public List<MissionDTO> getMissionDTOList() {
		return missionDTOList;
	}

	public void setMissionDTOList(List<MissionDTO> missionDTOList) {
		this.missionDTOList = missionDTOList;
	}

	public String getMissionListType() {
		return missionListType;
	}

	public void setMissionListType(String missionListType) {
		this.missionListType = missionListType;
	}

	public Long getIntervalTime() {
		return intervalTime;
	}

	public void setIntervalTime(Long intervalTime) {
		this.intervalTime = intervalTime;
	}

	public int getRepeatCount() {
		return repeatCount;
	}

	public void setRepeatCount(int repeatCount) {
		this.repeatCount = repeatCount;
	}

	public Long getStartTime() {
		return startTime;
	}

	public void setStartTime(Long startTime) {
		this.startTime = startTime;
	}

	public Long getStopTime() {
		return stopTime;
	}

	public void setStopTime(Long stopTime) {
		this.stopTime = stopTime;
	}

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}
}
