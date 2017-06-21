package cn.mrobot.bean.area.map;

import com.alibaba.fastjson.annotation.JSONField;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * Project Name : goor-common
 * User: Jelynn
 * Date: 2017/6/19
 * Time: 18:58
 * Describe:
 * Version:1.0
 */
@Table(name = "A_MAP_ZIP")
public class MapZip {

	@Id
	private int id;

	@Column(name = "MAP_NAME")
	private String mapName;

	@Column(name = "SCENE_NAME")
	@JSONField(name = "scene_name")
	private String sceneName;

	@Column(name = "FILE_PATH")
	private String filePath;

	@Column(name = "VERSION")
	private String version;

	@Column(name = "FILE_NAME")
	private String fileName;

	@Column(name = "CREATE_DATE")
	private Date createDate;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getMapName() {
		return mapName;
	}

	public void setMapName(String mapName) {
		this.mapName = mapName;
	}

	public String getSceneName() {
		return sceneName;
	}

	public void setSceneName(String sceneName) {
		this.sceneName = sceneName;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
}
