package cn.muye.base.websoket;

import cn.mrobot.bean.websocket.WSMessage;
import com.alibaba.fastjson.JSON;
import tk.mybatis.mapper.MapperException;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

/**
 * Created by Jelynn on 2017/8/17.
 */
public class ServerEncoder implements Encoder.Text<WSMessage>{

    @Override
    public void destroy() {
        // TODO Auto-generated method stub

    }

    @Override
    public void init(EndpointConfig arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public String encode(WSMessage message) throws EncodeException {
        try {
            return JSON.toJSONString(message);
        } catch (MapperException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }
}
