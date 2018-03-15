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
public class PubBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private String pub_name;

    private String publisher;
}
