package cn.muye.base.service.imp;

import cn.mrobot.bean.FileUpload;
import cn.mrobot.bean.area.map.MapZip;
import cn.muye.base.service.FileUploadService;
import cn.muye.base.service.mapper.FileUploadMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Condition;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Project Name : Goor-mrobot
 * User: Jelynn
 * Date: 2017/6/23
 * Time: 11:22
 * Describe:
 * Version:1.0
 */
@Service
public class FileUploadServiceImpl implements FileUploadService {

	@Autowired
	private FileUploadMapper fileUploadMapper;

	@Override
	public FileUpload getByName(String name) {
		Condition condition = new Condition(MapZip.class);
		condition.createCriteria().andCondition("NAME ='" + name + "'");
		List<FileUpload> mapZipList = fileUploadMapper.selectByExample(condition);
		return (mapZipList.size() > 0) ? mapZipList.get(0) : null;
	}

	@Override
	public long save(FileUpload fileUpload) {
		return fileUploadMapper.insert(fileUpload);
	}

	@Override
	public void update(FileUpload fileUpload) {
		fileUploadMapper.updateByPrimaryKey(fileUpload);
	}
}
