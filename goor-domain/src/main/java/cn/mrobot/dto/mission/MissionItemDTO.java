package cn.mrobot.dto.mission;

import cn.mrobot.bean.mission.FeatureItem;
import com.alibaba.fastjson.annotation.JSONField;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * Project Name : goor-domain
 * User: Chay
 * Date: 2017/7/3
 * Time: 16:41
 * Describe: 子任务
 * Version:1.0
 */
public class MissionItemDTO {

	private Long id;

	private String name;

	private String data;//任务详细/功能数据

	public MissionItemDTO() {
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

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}
}
