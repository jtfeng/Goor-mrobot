package cn.mrobot.bean.mission.task;

import java.io.Serializable;
import java.util.List;

/**
 * Created by abel on 17-9-18.
 */
public class JsonMissionItemDataTwoElevator implements Serializable {

    private static final long serialVersionUID = 3605314903832197383L;
    /**
     * path2_3 : {"id":1,"scene_name":"test"}
     * path3_2 : {"id":1,"scene_name":"test"}
     * elevators : [{"ip_elevator_id":1,"arrival_floor":1,"default_elevator":1,"enter_point":{"th":0,"map_name":"地图名","x":0,"y":0,"map":"地图名","scene_name":"场景名"},"set_pose_point":{"th":0,"map_name":"地图名","x":0,"y":0,"map":"地图名","scene_name":"场景名"},"back_point":{"th":0,"map_name":"地图名","x":0,"y":0,"map":"地图名","scene_name":"场景名"},"current_floor":4},{"ip_elevator_id":1,"arrival_floor":1,"default_elevator":0,"enter_point":{"th":0,"map_name":"地图名","x":0,"y":0,"map":"地图名","scene_name":"场景名"},"set_pose_point":{"th":0,"map_name":"地图名","x":0,"y":0,"map":"地图名","scene_name":"场景名"},"back_point":{"th":0,"map_name":"地图名","x":0,"y":0,"map":"地图名","scene_name":"场景名"},"current_floor":4}]
     * path0_1 : {"id":1,"scene_name":"test"}
     * path1_0 : {"id":1,"scene_name":"test"}
     */
    private PathEntity path2_3;
    private PathEntity path3_2;
    private List<ElevatorsEntity> elevators;
    private PathEntity path0_1;
    private PathEntity path1_0;

    private List<String> employee_num_list;

    public void setPath2_3(PathEntity path2_3) {
        this.path2_3 = path2_3;
    }

    public void setPath3_2(PathEntity path3_2) {
        this.path3_2 = path3_2;
    }

    public void setElevators(List<ElevatorsEntity> elevators) {
        this.elevators = elevators;
    }

    public void setPath0_1(PathEntity path0_1) {
        this.path0_1 = path0_1;
    }

    public void setPath1_0(PathEntity path1_0) {
        this.path1_0 = path1_0;
    }

    public PathEntity getPath2_3() {
        return path2_3;
    }

    public PathEntity getPath3_2() {
        return path3_2;
    }

    public List<ElevatorsEntity> getElevators() {
        return elevators;
    }

    public PathEntity getPath0_1() {
        return path0_1;
    }

    public PathEntity getPath1_0() {
        return path1_0;
    }

    public List<String> getEmployee_num_list() {
        return employee_num_list;
    }

    public void setEmployee_num_list(List<String> employee_num_list) {
        this.employee_num_list = employee_num_list;
    }

    public static class ElevatorsEntity implements Serializable {
        private static final long serialVersionUID = 3866715187994095143L;
        /**
         * ip_elevator_id : 1
         * arrival_floor : 1
         * default_elevator : 1
         * enter_point : {"th":0,"map_name":"地图名","x":0,"y":0,"map":"地图名","scene_name":"场景名"}
         * set_pose_point : {"th":0,"map_name":"地图名","x":0,"y":0,"map":"地图名","scene_name":"场景名"}
         * back_point : {"th":0,"map_name":"地图名","x":0,"y":0,"map":"地图名","scene_name":"场景名"}
         * current_floor : 4
         */
        private int ip_elevator_id;
        private int arrival_floor;
        private int default_elevator;
        private int auto_mode;
        private JsonMissionItemDataElevator.Point enter_point;
        private JsonMissionItemDataElevator.Point set_pose_point;
        private JsonMissionItemDataElevator.Point back_point;
        private int current_floor;
        private Long waitPointId;
        private Long waitPointIdNext;
        private Long elevatorId;
        private String sceneName;
        private String mapName;
        private String sceneNameNext;
        private String mapNameNext;

        public int getAuto_mode() {
            return auto_mode;
        }

        public void setAuto_mode(int auto_mode) {
            this.auto_mode = auto_mode;
        }

        public String getSceneNameNext() {
            return sceneNameNext;
        }

        public void setSceneNameNext(String sceneNameNext) {
            this.sceneNameNext = sceneNameNext;
        }

        public String getMapNameNext() {
            return mapNameNext;
        }

        public void setMapNameNext(String mapNameNext) {
            this.mapNameNext = mapNameNext;
        }

        public String getSceneName() {
            return sceneName;
        }

        public void setSceneName(String sceneName) {
            this.sceneName = sceneName;
        }

        public String getMapName() {
            return mapName;
        }

        public void setMapName(String mapName) {
            this.mapName = mapName;
        }

        public Long getElevatorId() {
            return elevatorId;
        }

        public void setElevatorId(Long elevatorId) {
            this.elevatorId = elevatorId;
        }

        public Long getWaitPointIdNext() {
            return waitPointIdNext;
        }

        public void setWaitPointIdNext(Long waitPointIdNext) {
            this.waitPointIdNext = waitPointIdNext;
        }

        public Long getWaitPointId() {
            return waitPointId;
        }

        public void setWaitPointId(Long waitPointId) {
            this.waitPointId = waitPointId;
        }

        public void setIp_elevator_id(int ip_elevator_id) {
            this.ip_elevator_id = ip_elevator_id;
        }

        public void setArrival_floor(int arrival_floor) {
            this.arrival_floor = arrival_floor;
        }

        public void setDefault_elevator(int default_elevator) {
            this.default_elevator = default_elevator;
        }

        public void setEnter_point(JsonMissionItemDataElevator.Point enter_point) {
            this.enter_point = enter_point;
        }

        public void setSet_pose_point(JsonMissionItemDataElevator.Point set_pose_point) {
            this.set_pose_point = set_pose_point;
        }

        public void setBack_point(JsonMissionItemDataElevator.Point back_point) {
            this.back_point = back_point;
        }

        public void setCurrent_floor(int current_floor) {
            this.current_floor = current_floor;
        }

        public int getIp_elevator_id() {
            return ip_elevator_id;
        }

        public int getArrival_floor() {
            return arrival_floor;
        }

        public int getDefault_elevator() {
            return default_elevator;
        }

        public JsonMissionItemDataElevator.Point getEnter_point() {
            return enter_point;
        }

        public JsonMissionItemDataElevator.Point getSet_pose_point() {
            return set_pose_point;
        }

        public JsonMissionItemDataElevator.Point getBack_point() {
            return back_point;
        }

        public int getCurrent_floor() {
            return current_floor;
        }
    }

    public static class PathEntity implements Serializable {
        private static final long serialVersionUID = -7086571034842549349L;
        /**
         * id : 1
         * scene_name : test
         */
        private int id;
        private String scene_name;

        public void setId(int id) {
            this.id = id;
        }

        public void setScene_name(String scene_name) {
            this.scene_name = scene_name;
        }

        public int getId() {
            return id;
        }

        public String getScene_name() {
            return scene_name;
        }
    }

}
