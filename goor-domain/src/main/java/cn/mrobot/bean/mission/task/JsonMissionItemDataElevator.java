package cn.mrobot.bean.mission.task;

import java.io.Serializable;
import java.util.List;

/**
 * Created by abel on 17-7-19.
 */
public class JsonMissionItemDataElevator implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer current_floor;
    private Integer arrival_floor;
    Point enter_point;
    Point set_pose_point;
    Point back_point;

    private List<String> employee_num_list;

    public Integer getCurrent_floor() {
        return current_floor;
    }

    public void setCurrent_floor(Integer current_floor) {
        this.current_floor = current_floor;
    }

    public Integer getArrival_floor() {
        return arrival_floor;
    }

    public void setArrival_floor(Integer arrival_floor) {
        this.arrival_floor = arrival_floor;
    }

    public Point getEnter_point() {
        return enter_point;
    }

    public void setEnter_point(Point enter_point) {
        this.enter_point = enter_point;
    }

    public Point getSet_pose_point() {
        return set_pose_point;
    }

    public void setSet_pose_point(Point set_pose_point) {
        this.set_pose_point = set_pose_point;
    }

    public Point getBack_point() {
        return back_point;
    }

    public void setBack_point(Point back_point) {
        this.back_point = back_point;
    }

    public List<String> getEmployee_num_list() {
        return employee_num_list;
    }

    public void setEmployee_num_list(List<String> employee_num_list) {
        this.employee_num_list = employee_num_list;
    }

    public static class Point implements Serializable{
        private static final long serialVersionUID = -6641063722088390423L;
        String scene_name;
        String map_name;
        String point_name;
        double x;
        double y;
        double th;

        public String getScene_name() {
            return scene_name;
        }

        public void setScene_name(String scene_name) {
            this.scene_name = scene_name;
        }

        public String getMap_name() {
            return map_name;
        }

        public void setMap_name(String map_name) {
            this.map_name = map_name;
        }

        public String getPoint_name() {
            return point_name;
        }

        public void setPoint_name(String point_name) {
            this.point_name = point_name;
        }

        public double getX() {
            return x;
        }

        public void setX(double x) {
            this.x = x;
        }

        public double getY() {
            return y;
        }

        public void setY(double y) {
            this.y = y;
        }

        public double getTh() {
            return th;
        }

        public void setTh(double th) {
            this.th = th;
        }
    }
}
