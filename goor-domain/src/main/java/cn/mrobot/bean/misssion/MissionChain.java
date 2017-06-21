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
 * Time: 16:40
 * Describe:  总任务 (任务串)
 * Version:1.0
 */
public class MissionChain implements Serializable {

	private Long id;

	private String name; //任务名

	private String description; //描述

	@JSONField(format = "yyyy-MM-dd HH:mm:ss")
	private Date createTime;

	private Date updateTime;

	private int repeatCount;

	private Long intervalTime;

	private List<MissionNode> missionNodeList;

	private Long missionMainId;

	private Integer priority;//优先级

	public MissionChain() {
	}

	public static class Builder {

		private String name;
		private String description;
		private Date createTime;
		private Date updateTime;
		private Integer priority;
		private int repeatCount;
		private Long intervalTime;
		private List<MissionNode> missionNodeList;
		private Long missionMainId;

		public Builder name(String name) {
			this.name = name;
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


		public Builder priority(Integer priority) {
			this.priority = priority;
			return this;
		}

		public Builder repeatCount(int repeatCount) {
			this.repeatCount = repeatCount;
			return this;
		}

		public Builder missionNodeList(List<MissionNode> missionNodeList) {
			this.missionNodeList = missionNodeList;
			return this;
		}

		public Builder intervalTime(Long intervalTime) {
			this.intervalTime = intervalTime;
			return this;
		}


		public Builder missionMainId(Long missionMainId) {
			this.missionMainId = missionMainId;
			return this;
		}

		public MissionChain build() {
			return new MissionChain(this);
		}
	}

	private MissionChain(Builder builder) {
		name = builder.name;
		description = builder.description;
		createTime = builder.createTime;
		priority = builder.priority;
		repeatCount = builder.repeatCount;
		intervalTime = builder.intervalTime;
		missionMainId = builder.missionMainId;
		missionNodeList = builder.missionNodeList;
		updateTime = builder.updateTime;
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

	public Long getMissionMainId() {
		return missionMainId;
	}

	public void setMissionMainId(Long missionMainId) {
		this.missionMainId = missionMainId;
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

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	public List<MissionNode> getMissionNodeList() {
		return missionNodeList;
	}

	public void setMissionNodeList(List<MissionNode> missionNodeList) {
		this.missionNodeList = missionNodeList;
	}
}
