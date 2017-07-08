package cn.muye.base.service;

import cn.mrobot.bean.FileUpload;
import cn.mrobot.bean.area.map.MapZip;

/**
 * Created with IntelliJ IDEA.
 * Project Name : Goor-mrobot
 * User: Jelynn
 * Date: 2017/6/23
 * Time: 11:22
 * Describe:
 * Version:1.0
 */
public interface FileUploadService {

	FileUpload getByName(String name);

	long save(FileUpload fileUpload);

	void update(FileUpload fileUpload);
}
