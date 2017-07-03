package cn.mrobot.bean.mission;

import cn.mrobot.bean.base.BaseBean;
import cn.mrobot.dto.mission.MissionDTO;
import cn.mrobot.dto.mission.MissionListDTO;
import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Project Name : djinn
 * User: Jelynn
 * Date: 2017/3/21
 * Time: 16:21
 * Describe: 总任务 (任务串)
 * Version:1.0
 */
public class MissionList extends BaseBean{

	private Long id;

	private String name;  //总任务名称

	private String description;

	private String missionListType;

	@JSONField(format = "yyyy-MM-dd HH:mm:ss")
	private Date updateTime;    //更新时间

	private List<Mission> missionList;

	/**
	 * //间隔时间
	 */
	private Long intervalTime;
	/**
	 * //循环次数  -1为无限循环
	 */
	private int repeatCount;

	private Long startTime;

	private Long stopTime;

	private Integer priority;//优先级

	public static class Builder {

		private String name;  //总任务名称
		private String description;
		private Date created;  //创建时间
		private Date updateTime;    //更新时间
		private Long intervalTime;
		private int repeatCount;
		private Long startTime;
		private Long stopTime;
		private Integer priority;//优先级
		private String missionListType;
		private List<Mission> missionList;

		public Builder name(String name) {
			this.name = name;
			return this;
		}

		public Builder missionListType(String missionListType) {
			this.missionListType = missionListType;
			return this;
		}

		public Builder description(String description) {
			this.description = description;
			return this;
		}

		public Builder created(Date created) {
			this.created = created;
			return this;
		}

		public Builder updateTime(Date updateTime) {
			this.updateTime = updateTime;
			return this;
		}

		public Builder startTime(Long startTime) {
			this.startTime = startTime;
			return this;
		}

		public Builder stopTime(Long stopTime) {
			this.stopTime = stopTime;
			return this;
		}

		public Builder intervalTime(Long intervalTime) {
			this.intervalTime = intervalTime;
			return this;
		}

		public Builder repeatCount(int repeatCount) {
			this.repeatCount = repeatCount;
			return this;
		}

		public Builder priority(Integer priority) {
			this.priority = priority;
			return this;
		}

		public Builder missionChainList(List<Mission> missionList) {
			this.missionList = missionList;
			return this;
		}

		public MissionList build() {
			return new MissionList(this);
		}
	}

	public MissionList() {
	}

	private MissionList(Builder builder) {
		name = builder.name;
		description = builder.description;
		created = builder.created;
		updateTime = builder.updateTime;
		startTime = builder.startTime;
		stopTime = builder.stopTime;
		intervalTime = builder.intervalTime;
		repeatCount = builder.repeatCount;
		priority = builder.priority;
		missionList = builder.missionList;
		missionListType = builder.missionListType;
	}

	public MissionListDTO toDTO() {
		MissionListDTO missionListDTO = new MissionListDTO();
		missionListDTO.setId(this.getId());
		missionListDTO.setIntervalTime(this.getIntervalTime());
		missionListDTO.setMissionListType(this.getMissionListType());
		missionListDTO.setPriority(this.getPriority());
		missionListDTO.setRepeatCount(this.getRepeatCount());
		missionListDTO.setStartTime(this.getStartTime());
		missionListDTO.setStopTime(this.getStopTime());
		List<Mission> missions = this.getMissionList();
		if(missions != null) {
			List<MissionDTO> missionDTOS = new ArrayList<MissionDTO>();
			for(Mission mission : missions) {
				missionDTOS.add(mission.toDTO());
			}
			missionListDTO.setMissionDTOList(missionDTOS);
		}
		return missionListDTO;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
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

	public List<Mission> getMissionList() {
		return missionList;
	}

	public void setMissionList(List<Mission> missionList) {
		this.missionList = missionList;
	}

	public String getMissionListType() {
		return missionListType;
	}

	public void setMissionListType(String missionListType) {
		this.missionListType = missionListType;
	}

}
