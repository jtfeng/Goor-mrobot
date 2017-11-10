package cn.mrobot.bean.mission.task;

import java.io.Serializable;

/**
 * Created by abel on 17-7-19.
 */
public class JsonMissionItemDataDoor implements Serializable {
    private static final long serialVersionUID = 1;

    private Long waitTime;
    private JsonMissionItemDataLaserNavigation point;
    private Path path;

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public Long getWaitTime() {
        return waitTime;
    }

    public void setWaitTime(Long waitTime) {
        this.waitTime = waitTime;
    }

    public JsonMissionItemDataLaserNavigation getPoint() {
        return point;
    }

    public void setPoint(JsonMissionItemDataLaserNavigation point) {
        this.point = point;
    }

    public static class Path implements Serializable{
        private static final long serialVersionUID = 6913188562839295836L;
        private String scene_name;
        private Long id;
        private Integer tolerance_type;//工控路径类型（此处暂定为 0 表示终点保持原样工控路径 1 代表终点无朝向要求工控路径）
        /**
         *  地图名称
         */
        private String map_name;

        public String getScene_name() {
            return scene_name;
        }

        public void setScene_name(String scene_name) {
            this.scene_name = scene_name;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public Integer getTolerance_type() {
            return tolerance_type;
        }

        public void setTolerance_type(Integer tolerance_type) {
            this.tolerance_type = tolerance_type;
        }

        public String getMap_name() {
            return map_name;
        }

        public void setMap_name(String map_name) {
            this.map_name = map_name;
        }
    }
}
