package cn.mrobot.bean.misssion;

/**
 * Created by Chay on 2017/03/15.
 */
public class FeatureItemType {

	private Long id;

	private String name; //任务详细类型键名

	private String value; //任务详细类型键值

	private String description; //描述

	private String dataModel; //数据模板，方便前端用户输入

	private Long featureItemId;

	private FeatureItem featureItem;

	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public void setName(String key) {
		this.name = key;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDataModel() {
		return dataModel;
	}

	public void setDataModel(String dataModel) {
		this.dataModel = dataModel;
	}

	public Long getFeatureItemId() {
		return featureItemId;
	}

	public void setFeatureItemId(Long featureItemId) {
		this.featureItemId = featureItemId;
	}

	public FeatureItem getFeatureItem() {
		return featureItem;
	}

	public void setFeatureItem(FeatureItem featureItem) {
		this.featureItem = featureItem;
	}
}
