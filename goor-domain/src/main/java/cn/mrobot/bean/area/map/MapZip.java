package cn.mrobot.bean.area.map;

import com.alibaba.fastjson.annotation.JSONField;

import javax.persistence.*;
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
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	/**
	 * 地图名
	 */
	@Column(name = "MAP_NAME")
	private String mapName;

	/**
	 * 场景名
	 */
	@Column(name = "SCENE_NAME")
	private String sceneName;

	/**
	 * 文件存放路径
	 */
	@Column(name = "FILE_PATH")
	private String filePath;

	/**
	 * 文件名称
	 */
	@Column(name = "FILE_NAME")
	private String fileName;

	@Column(name = "VERSION")
	private String version;

	/**
	 * 文件机器人上存放路径
	 */
	@Column(name = "ROBOT_PATH")
	private String robotPath;

	@Column(name = "CREATE_DATE")
	private Date createDate;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
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

	public String getRobotPath() {
		return robotPath;
	}

	public void setRobotPath(String robotPath) {
		this.robotPath = robotPath;
	}
}
