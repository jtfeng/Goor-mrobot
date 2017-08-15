package cn.mrobot.bean.mission;

import cn.mrobot.bean.base.BaseBean;
import cn.mrobot.bean.mission.task.MissionListTask;
import cn.mrobot.bean.mission.task.MissionTask;
import cn.mrobot.dto.mission.MissionDTO;
import cn.mrobot.dto.mission.MissionListDTO;
import com.alibaba.fastjson.annotation.JSONField;

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

	private Long sceneId;//关联场景ID

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
	private Integer repeatCount;

	private Long startTime;

	private Long stopTime;

	private Integer priority;//优先级

	private String mapName;//任务所属地图名

	public static class Builder {

		private String name;  //总任务名称
		private String description;
		private Date createTime;  //创建时间
		private Date updateTime;    //更新时间
		private Long intervalTime;
		private Integer repeatCount;
		private Long startTime;
		private Long stopTime;
		private Long sceneId;
		private Integer priority;//优先级
		private String missionListType;
		private List<Mission> missionList;
		private String mapName;//任务所属地图名

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

		public Builder createTime(Date createTime) {
			this.createTime = createTime;
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

		public Builder repeatCount(Integer repeatCount) {
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

		public Builder sceneId(Long sceneId) {
			this.sceneId = sceneId;
			return this;
		}

		public Builder mapName(String mapName) {
			this.mapName = mapName;
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
		createTime = builder.createTime;
		updateTime = builder.updateTime;
		startTime = builder.startTime;
		stopTime = builder.stopTime;
		intervalTime = builder.intervalTime;
		repeatCount = builder.repeatCount;
		priority = builder.priority;
		missionList = builder.missionList;
		missionListType = builder.missionListType;
		sceneId = builder.sceneId;
		mapName = builder.mapName;
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

	public Integer getRepeatCount() {
		return repeatCount;
	}

	public void setRepeatCount(Integer repeatCount) {
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

	public Long getSceneId() {
		return sceneId;
	}

	public void setSceneId(Long sceneId) {
		this.sceneId = sceneId;
	}

	public String getMapName() {
		return mapName;
	}

	public void setMapName(String mapName) {
		this.mapName = mapName;
	}
}
