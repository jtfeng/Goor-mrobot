package cn.mrobot.bean.base;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Created by enva on 2017/6/30.
 */
@AllArgsConstructor
@Data
@NoArgsConstructor
public class PubX86HeartBeat implements Serializable {

    private static final long serialVersionUID = 1L;

    private String uuid;

    private String direction = "ping";

}
