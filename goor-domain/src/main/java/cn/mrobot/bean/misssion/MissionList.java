package cn.mrobot.bean.misssion;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;
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
public class MissionList implements Serializable{

	private Long id;

	private String name;  //总任务名称

	private String description;

	private String deviceId;

	@JSONField(format = "yyyy-MM-dd HH:mm:ss")
	private Date createTime;  //创建时间

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

	private Integer priority;//优先级

	public static class Builder {

		private String name;  //总任务名称
		private String description;
		private String deviceId;
		private Date createTime;  //创建时间
		private Date updateTime;    //更新时间
		private Long intervalTime;
		private int repeatCount;
		private Long startTime;
		private Integer priority;//优先级
		private List<Mission> missionList;

		public Builder name(String name) {
			this.name = name;
			return this;
		}

		public Builder deviceId(String deviceId) {
			this.deviceId = deviceId;
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
		deviceId = builder.deviceId;
		createTime = builder.createTime;
		updateTime = builder.updateTime;
		startTime = builder.startTime;
		intervalTime = builder.intervalTime;
		repeatCount = builder.repeatCount;
		priority = builder.priority;
		missionList = builder.missionList;
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

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
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
}
