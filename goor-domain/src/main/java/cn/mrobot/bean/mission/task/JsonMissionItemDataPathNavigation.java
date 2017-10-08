package cn.mrobot.bean.mission.task;

import java.io.Serializable;

/**
 * Created by chay on 17-8-23.
 */
public class JsonMissionItemDataPathNavigation implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;//路径ID
    private String scene_name;//场景名称
    private Integer tolerance_type;//工控路径类型（此处暂定为 0 表示终点保持原样工控路径 1 代表终点无朝向要求工控路径）

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getScene_name() {
        return scene_name;
    }

    public void setScene_name(String scene_name) {
        this.scene_name = scene_name;
    }

    public Integer getTolerance_type() {
        return tolerance_type;
    }

    public void setTolerance_type(Integer tolerance_type) {
        this.tolerance_type = tolerance_type;
    }
}
