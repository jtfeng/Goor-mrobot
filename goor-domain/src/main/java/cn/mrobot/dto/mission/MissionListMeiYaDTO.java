package cn.mrobot.dto.mission;

import cn.mrobot.bean.mission.MissionList;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Project Name : goor-domain
 * User: Chay
 * Date: 2017/8/16
 * Time: 16:21
 * Describe: 美亚任务列表按地图名、充电任务、导航任务分类
 * Version:1.0
 */
public class MissionListMeiYaDTO implements Serializable{

	String mapName; //地图名
	private List<MissionList> patrols = new ArrayList<MissionList>();//巡逻任务
	private List<MissionList> charges = new ArrayList<MissionList>();//充电任务

	public String getMapName() {
		return mapName;
	}

	public void setMapName(String mapName) {
		this.mapName = mapName;
	}

	public List<MissionList> getPatrols() {
		return patrols;
	}

	public void setPatrols(List<MissionList> patrols) {
		this.patrols = patrols;
	}

	public List<MissionList> getCharges() {
		return charges;
	}

	public void setCharges(List<MissionList> charges) {
		this.charges = charges;
	}
}
