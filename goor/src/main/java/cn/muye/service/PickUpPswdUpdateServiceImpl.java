package cn.muye.service;

import cn.mrobot.bean.AjaxResult;
import cn.mrobot.bean.assets.robot.RobotPassword;
import cn.mrobot.bean.message.data.PickUpPswdUpdateBean;
import cn.mrobot.utils.FileUtils;
import cn.mrobot.utils.JsonUtils;
import cn.mrobot.utils.StringUtil;
import com.google.gson.reflect.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by abel on 17-8-7.
 */
@Service
public class PickUpPswdUpdateServiceImpl implements PickUpPswdUpdateService {

    @Autowired
    File fileCachePath;

    @Override
    public AjaxResult sendPickUpPswdUpdate(PickUpPswdUpdateBean bean) {
        if (bean == null ||
                bean.getList() == null ||
                bean.getList().isEmpty()){
            return AjaxResult.success();
        }
        if (fileCachePath != null){
            String content = FileUtils.readFileAsString(
                    fileCachePath.getAbsolutePath() + "/pickuppswd.robot"
            );
            if (!StringUtil.isEmpty(content)){
                //反序列化对象，比较
                List<RobotPassword> savedlist =
                        (List<RobotPassword>) JsonUtils.fromJson(content,
                                new TypeToken<List<RobotPassword>>(){}.getType());
                List<RobotPassword> addObjs = new ArrayList<>();
                if (savedlist != null &&
                        !savedlist.isEmpty()){
                    for (RobotPassword rp :
                            savedlist) {
                        if (rp != null) {
                            boolean isReplace = false;
                            for (RobotPassword temp :
                                    bean.getList()) {
                                if (temp != null &&
                                        Objects.equals(temp.getRobotId(), rp.getRobotId()) &&
                                        Objects.equals(temp.getBoxNum(), rp.getBoxNum())){
                                    //对象相同，替换
                                    isReplace = true;
                                    break;
                                }
                            }
                            if (!isReplace){
                                addObjs.add(rp);
                            }
                        }
                    }
                }
                //增加
                for (RobotPassword rp :
                        addObjs) {
                    if (rp != null) {
                        bean.getList().add(rp);
                    }
                }
            }

            //保存
            FileUtils.writeToFile(fileCachePath.getAbsolutePath() + "/pickuppswd.robot",
                    JsonUtils.toJson(bean.getList(),
                            new TypeToken<List<RobotPassword>>(){}.getType()));
        }
        return AjaxResult.success();
    }
}
