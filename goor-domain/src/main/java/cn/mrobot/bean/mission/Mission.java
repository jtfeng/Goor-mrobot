package cn.mrobot.bean.mission;

import cn.mrobot.bean.base.BaseBean;
import cn.mrobot.bean.mission.task.MissionItemTask;
import cn.mrobot.bean.mission.task.MissionTask;
import cn.mrobot.dto.mission.MissionDTO;
import cn.mrobot.dto.mission.MissionItemDTO;
import com.alibaba.fastjson.annotation.JSONField;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * Project Name : djinn
 * User: Jelynn
 * Date: 2017/3/21
 * Time: 16:40
 * Describe:  总任务 (任务串)
 * Version:1.0
 */
public class Mission extends BaseBean {

	private Long id;

	private Long sceneId;//所属场景ID

	private String name; //任务名

	private String description; //描述

	@JSONField(format = "yyyy-MM-dd HH:mm:ss")
	private Date updateTime;

	private int repeatCount;

	private Long intervalTime;

	private Set<MissionItem> missionItemSet;

	private String sceneName; //场景名

	private Integer typeId; //任务类型ID

//	@Transient
//	private String typeName; //任务类型名称 todo 前端直接用登陆后常量代替了

//	private Long missionMainId;

//	private Integer priority;//优先级

	public Mission() {
	}

	public static class Builder {

		private String name;
		private String description;
		private Date createTime;
		private Date updateTime;
//		private Integer priority;
		private int repeatCount;
		private Long intervalTime;
		private Set<MissionItem> missionItemSet;
//		private Long missionMainId;
		private Long sceneId;

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


//		public Builder priority(Integer priority) {
//			this.priority = priority;
//			return this;
//		}

		public Builder repeatCount(int repeatCount) {
			this.repeatCount = repeatCount;
			return this;
		}

		public Builder missionNodeSet(Set<MissionItem> missionItemSet) {
			this.missionItemSet = missionItemSet;
			return this;
		}

		public Builder intervalTime(Long intervalTime) {
			this.intervalTime = intervalTime;
			return this;
		}


//		public Builder missionMainId(Long missionMainId) {
//			this.missionMainId = missionMainId;
//			return this;
//		}

		public Builder sceneId(Long sceneId) {
			this.sceneId = sceneId;
			return this;
		}

		public Mission build() {
			return new Mission(this);
		}
	}

	private Mission(Builder builder) {
		name = builder.name;
		description = builder.description;
		createTime = builder.createTime;
//		priority = builder.priority;
		repeatCount = builder.repeatCount;
		intervalTime = builder.intervalTime;
//		missionMainId = builder.missionMainId;
		missionItemSet = builder.missionItemSet;
		updateTime = builder.updateTime;
		sceneId = builder.sceneId;
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

//	public Long getMissionListId() {
//		return missionMainId;
//	}
//
//	public void setMissionListId(Long missionMainId) {
//		this.missionMainId = missionMainId;
//	}

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

//	public Integer getPriority() {
//		return priority;
//	}
//
//	public void setPriority(Integer priority) {
//		this.priority = priority;
//	}

	public Set<MissionItem> getMissionItemSet() {
		return missionItemSet;
	}

	public void setMissionItemSet(Set<MissionItem> missionItemSet) {
		this.missionItemSet = missionItemSet;
	}

	public String getSceneName() {
		return sceneName;
	}

	public void setSceneName(String sceneName) {
		this.sceneName = sceneName;
	}

	public Integer getTypeId() {
		return typeId;
	}

	public void setTypeId(Integer typeId) {
		this.typeId = typeId;
	}

	public Long getSceneId() {
		return sceneId;
	}

	public void setSceneId(Long sceneId) {
		this.sceneId = sceneId;
	}
}
