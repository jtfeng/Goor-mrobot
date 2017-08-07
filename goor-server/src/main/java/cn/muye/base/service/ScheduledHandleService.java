package cn.muye.base.service;


public interface ScheduledHandleService {

    void mqHealthCheck() throws Exception;

    void executeTwentyThreeAtNightPerDay() throws Exception;

    void executeRobotHeartBeat() throws Exception;
}
