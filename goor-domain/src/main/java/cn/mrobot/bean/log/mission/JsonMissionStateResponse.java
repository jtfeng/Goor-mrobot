package cn.mrobot.bean.log.mission;

import java.io.Serializable;
import java.util.List;

/**
 * Created by abel on 17-7-18.
 */
public class JsonMissionStateResponse implements Serializable {
    private static final long serialVersionUID = 5017600521478414461L;

    public static final String state_waiting = "waiting";
    public static final String state_finished = "finished";
    public static final String state_executing = "executing";
    public static final String state_paused = "paused";
    public static final String state_canceled = "canceled";

    /**
     * mission_list_id : 1
     * state : executing
     * mission_list : [{"mission_id":1,"state":"finished","repeat_times":1,"mission_item_set":[{"mission_item_id":1,"state":"finished"},{"mission_item_id ":2,"state":"finished"}]},{"mission_id ":2,"state":"executing","missionItemSet":[{"mission_item_id ":1,"state":"finished"},{"mission_item_id ":2,"state":"executing"}],"repeat_times":1}]
     * repeat_times : 1
     */
    private Long mission_list_id;
    private String state;
    private List<Mission_listEntity> mission_list;
    private Integer repeat_times;

    public void setMission_list_id(Long mission_list_id) {
        this.mission_list_id = mission_list_id;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setMission_list(List<Mission_listEntity> mission_list) {
        this.mission_list = mission_list;
    }

    public void setRepeat_times(Integer repeat_times) {
        this.repeat_times = repeat_times;
    }

    public Long getMission_list_id() {
        return mission_list_id;
    }

    public String getState() {
        return state;
    }

    public List<Mission_listEntity> getMission_list() {
        return mission_list;
    }

    public Integer getRepeat_times() {
        return repeat_times;
    }

    public class Mission_listEntity implements Serializable {
        private static final long serialVersionUID = -185881937656521797L;
        /**
         * mission_id : 1
         * state : finished
         * repeat_times : 1
         * mission_item_set : [{"mission_item_id":1,"state":"finished"},{"mission_item_id ":2,"state":"finished"}]
         */
        private Long mission_id;
        private String state;
        private Integer repeat_times;
        private List<Mission_item_setEntity> mission_item_set;

        public void setMission_id(Long mission_id) {
            this.mission_id = mission_id;
        }

        public void setState(String state) {
            this.state = state;
        }

        public void setRepeat_times(Integer repeat_times) {
            this.repeat_times = repeat_times;
        }

        public void setMission_item_set(List<Mission_item_setEntity> mission_item_set) {
            this.mission_item_set = mission_item_set;
        }

        public Long getMission_id() {
            return mission_id;
        }

        public String getState() {
            return state;
        }

        public Integer getRepeat_times() {
            return repeat_times;
        }

        public List<Mission_item_setEntity> getMission_item_set() {
            return mission_item_set;
        }

        public class Mission_item_setEntity implements Serializable {
            private static final long serialVersionUID = -6095321509073051594L;
            /**
             * mission_item_id : 1
             * state : finished
             */
            private Long mission_item_id;
            private String state;

            public void setMission_item_id(Long mission_item_id) {
                this.mission_item_id = mission_item_id;
            }

            public void setState(String state) {
                this.state = state;
            }

            public Long getMission_item_id() {
                return mission_item_id;
            }

            public String getState() {
                return state;
            }
        }
    }
}
