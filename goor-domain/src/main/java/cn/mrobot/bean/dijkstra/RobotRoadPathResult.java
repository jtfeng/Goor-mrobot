package cn.mrobot.bean.dijkstra;

import cn.mrobot.bean.assets.robot.Robot;

/**
 * Created by chay on 2017/12/21.
 * @author chay
 */
public class RobotRoadPathResult implements Comparable<RobotRoadPathResult>{
    Robot robot;
    RoadPathResult roadPathResult;

    public RobotRoadPathResult() {

    }

    public RobotRoadPathResult(Robot robot, RoadPathResult roadPathResult) {
        this.robot = robot;
        this.roadPathResult = roadPathResult;
    }


    public Robot getRobot() {
        return robot;
    }

    public void setRobot(Robot robot) {
        this.robot = robot;
    }

    public RoadPathResult getRoadPathResult() {
        return roadPathResult;
    }

    public void setRoadPathResult(RoadPathResult roadPathResult) {
        this.roadPathResult = roadPathResult;
    }

    @Override
    public int compareTo(RobotRoadPathResult o) {
        Long thisWeight = this.roadPathResult.getTotalWeight();
        Long oWeight = o.roadPathResult.getTotalWeight();
        String thisName = this.robot.getName();
        //处理null为空字符
        thisName = thisName == null ? "" : thisName;
        String oName = o.getRobot().getName();
        //处理null为空字符
        oName = oName == null ? "" : oName;
        //先判断是不是null
        if(thisWeight == null && oWeight != null) {
            //thisWeight为null认为大，往后排
            return 1;
        }
        else if(thisWeight != null && oWeight == null) {
            //thisWeight为null认为大，往后排
            return -1;
        }
        else if(thisWeight == null && oWeight == null) {
            //都为null，根据机器人名称排序,注意名称的排序1，11，111会排在2，3的前面
            return thisName.compareTo(oName);
        }

        //如果都不是null，根据数值计算
        if(thisWeight > oWeight) {
            return 1;
        }else if(thisWeight < oWeight) {
            return -1;
        }
        else {
            //两者一样大，根据机器人名称排序,注意名称的排序1，11，111会排在2，3的前面
            return thisName.compareTo(oName);
        }
    }
}
