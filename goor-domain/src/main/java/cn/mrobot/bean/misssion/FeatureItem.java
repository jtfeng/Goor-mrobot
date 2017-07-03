package cn.mrobot.bean.misssion;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created with IntelliJ IDEA.
 * Project Name : djinn
 * User: Jelynn
 * Date: 2017/3/17
 * Time: 9:08
 * Describe:  功能类
 * Version:1.0
 */
@Table(name = "")
public class FeatureItem {

	@Id
	@Column(name = "ID")
	private Long id;
	/**
	 * //功能键名
	 */
	@Column(name = "NAME")
	private String name;
	/**
	 * //功能键值
	 */
	@Column(name = "VALUE")
	private String value;

	@Column(name = "DESCRIPTION")
	private String description;

	private String dataModel; //数据模板，方便前端用户输入

	public FeatureItem() {
	}

	public FeatureItem(String name, String value) {
		this.name = name;
		this.value = value;
	}

	public FeatureItem(String name, String value, String description) {
		this.name = name;
		this.value = value;
		this.description = description;
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

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
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

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof FeatureItem))
			return false;
		FeatureItem that = (FeatureItem) o;
		return !(name != null ? !name.equals(that.name) : that.name != null);
	}

	@Override
	public int hashCode() {
		return name != null ? name.hashCode() : 0;
	}
}
