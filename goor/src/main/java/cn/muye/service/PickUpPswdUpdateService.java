package cn.muye.service;

import cn.mrobot.bean.AjaxResult;
import cn.mrobot.bean.message.data.PickUpPswdUpdateBean;

/**
 * Created by abel on 17-7-11.
 */
public interface PickUpPswdUpdateService {

    AjaxResult sendPickUpPswdUpdate(PickUpPswdUpdateBean bean);
}
