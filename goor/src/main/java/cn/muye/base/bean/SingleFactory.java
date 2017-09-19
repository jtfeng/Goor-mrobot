package cn.muye.base.bean;

import cn.mrobot.bean.constant.TopicConstants;
import cn.muye.base.producer.ProducerCommon;
import edu.wpi.rail.jrosbridge.Ros;
import edu.wpi.rail.jrosbridge.Topic;
import edu.wpi.rail.jrosbridge.messages.std.UInt8MultiArray;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by enva on 2017/6/8.
 */
@Component
@Slf4j
public class SingleFactory {
    private static volatile ProducerCommon msg;
    private static volatile Topic x86_mission_dispatch;
    private static volatile Topic app_pub;
    private static volatile Topic app_sub;
    private static volatile Topic agent_pub;
    private static volatile Topic agent_sub;
    private static volatile Topic current_pose;
    private static volatile Topic x86_mission_queue_cancel;
    private static volatile Topic x86_mission_instant_control;
    private static volatile Topic x86_mission_common_request;
    private static volatile Topic x86_mission_queue_response;
    private static volatile Topic x86_mission_state_response;
    private static volatile Topic x86_mission_event;
    private static volatile Topic x86_mission_alert;
    private static volatile Topic x86_mission_receive;
    private static volatile Topic x86_elevator_lock;
    private static volatile Topic x86_roadpath_lock;
    private static volatile Topic android_joystick_cmd_vel;
    private static volatile Topic state_collector;
    private static volatile Topic checkHeartTopic;
    private static volatile Topic x86_mission_heartbeat;
    private static volatile Topic power;

    static Lock lock_msg=new ReentrantLock();
    static Lock lock_x86_mission_dispatch=new ReentrantLock();
    static Lock lock_app_pub=new ReentrantLock();
    static Lock lock_app_sub=new ReentrantLock();
    static Lock lock_agent_pub=new ReentrantLock();
    static Lock lock_agent_sub=new ReentrantLock();
    static Lock lock_current_pose=new ReentrantLock();
    static Lock lock_x86_mission_queue_cancel=new ReentrantLock();
    static Lock lock_x86_mission_instant_control=new ReentrantLock();
    static Lock lock_x86_mission_common_request=new ReentrantLock();
    static Lock lock_x86_mission_queue_response=new ReentrantLock();
    static Lock lock_x86_mission_state_response=new ReentrantLock();
    static Lock lock_x86_mission_event=new ReentrantLock();
    static Lock lock_x86_mission_alert=new ReentrantLock();
    static Lock lock_x86_mission_receive=new ReentrantLock();
    static Lock lock_x86_elevator_lock=new ReentrantLock();
    static Lock lock_x86_roadpath_lock=new ReentrantLock();
    static Lock lock_android_joystick_cmd_vel=new ReentrantLock();
    static Lock lock_state_collector=new ReentrantLock();
    static Lock lock_checkHeartTopic=new ReentrantLock();
    static Lock lock_x86_mission_heartbeat=new ReentrantLock();
    static Lock lock_power=new ReentrantLock();

    public static ProducerCommon getProducerCommon() {
        if (msg == null) {
            lock_msg.lock();
            try {
                if (msg == null) {
                    msg = new ProducerCommon();
                    log.info("get getProducerCommon msg="+msg);
                }
            }catch (Exception e){
                log.error("get getProducerCommon error", e);
            }finally {
                lock_msg.unlock();
            }
        }
        return msg;
    }

    public static Topic x86_mission_dispatch(Ros ros) throws Exception {
        if (x86_mission_dispatch == null) {
            if(null == ros){
                log.error("get x86_mission_dispatch ros is null error, return null");
                return x86_mission_dispatch;
            }
            lock_x86_mission_dispatch.lock();
            try {
                if (x86_mission_dispatch == null) {
                    x86_mission_dispatch = new Topic(ros, TopicConstants.X86_MISSION_DISPATCH, TopicConstants.TOPIC_TYPE_STRING);
                    log.info("get topic x86_mission_dispatch="+x86_mission_dispatch);
                }
            }catch (Exception e){
                log.error("get x86_mission_dispatch error", e);
            }finally {
                lock_x86_mission_dispatch.unlock();
            }
        }
        return x86_mission_dispatch;
    }

    public static Topic app_pub(Ros ros) throws Exception {
        if (app_pub == null) {
            if(null == ros){
                log.error("get app_pub ros is null error, return null");
                return app_pub;
            }
            lock_app_pub.lock();
            try {
                if (app_pub == null) {
                    app_pub = new Topic(ros, TopicConstants.APP_PUB, TopicConstants.TOPIC_TYPE_STRING);
                    log.info("get topic app_pub="+app_pub);
                }
            }catch (Exception e){
                log.error("get app_pub error", e);
            }finally {
                lock_app_pub.unlock();
            }
        }
        return app_pub;
    }

    public static Topic app_sub(Ros ros) throws Exception {
        if (app_sub == null) {
            if(null == ros){
                log.error("get app_sub ros is null error, return null");
                return app_sub;
            }
            lock_app_sub.lock();
            try {
                if (app_sub == null) {
                    app_sub = new Topic(ros, TopicConstants.APP_SUB, TopicConstants.TOPIC_TYPE_STRING);
                    log.info("get topic app_sub="+app_sub);
                }
            }catch (Exception e){
                log.error("get app_sub error", e);
            }finally {
                lock_app_sub.unlock();
            }
        }
        return app_sub;
    }

    public static Topic agent_pub(Ros ros) throws Exception {
        if (agent_pub == null) {
            if(null == ros){
                log.error("get agent_pub ros is null error, return null");
                return agent_pub;
            }
            lock_agent_pub.lock();
            try {
                if (agent_pub == null) {
                    agent_pub = new Topic(ros, TopicConstants.AGENT_PUB, TopicConstants.TOPIC_TYPE_STRING);
                    log.info("get topic agent_pub="+agent_pub);
                }
            }catch (Exception e){
                log.error("get agent_pub error", e);
            }finally {
                lock_agent_pub.unlock();
            }
        }
        return agent_pub;
    }

    public static Topic agent_sub(Ros ros) throws Exception {
        if (agent_sub == null) {
            if(null == ros){
                log.error("get agent_sub ros is null error, return null");
                return agent_sub;
            }
            lock_agent_sub.lock();
            try {
                if (agent_sub == null) {
                    agent_sub = new Topic(ros, TopicConstants.AGENT_SUB, TopicConstants.TOPIC_TYPE_STRING);
                    log.info("get topic agent_sub="+agent_sub);
                }
            }catch (Exception e){
                log.error("get agent_sub error", e);
            }finally {
                lock_agent_sub.unlock();
            }
        }
        return agent_sub;
    }

    public static Topic current_pose(Ros ros) throws Exception {
        if (current_pose == null) {
            if(null == ros){
                log.error("get current_pose ros is null error, return null");
                return current_pose;
            }
            lock_current_pose.lock();
            try {
                if (current_pose == null) {
                    current_pose = new Topic(ros, TopicConstants.CURRENT_POSE, TopicConstants.TOPIC_NAV_MSGS);
                    log.info("get topic current_pose="+current_pose);
                }
            }catch (Exception e){
                log.error("get current_pose error", e);
            }finally {
                lock_current_pose.unlock();
            }
        }
        return current_pose;
    }

    public static Topic x86_mission_queue_cancel(Ros ros) throws Exception {
        if (x86_mission_queue_cancel == null) {
            if(null == ros){
                log.error("get x86_mission_queue_cancel ros is null error, return null");
                return x86_mission_queue_cancel;
            }
            lock_x86_mission_queue_cancel.lock();
            try {
                if (x86_mission_queue_cancel == null) {
                    x86_mission_queue_cancel = new Topic(ros, TopicConstants.X86_MISSION_QUEUE_CANCEL, TopicConstants.TOPIC_TYPE_STRING);
                    log.info("get topic x86_mission_queue_cancel="+x86_mission_queue_cancel);
                }
            }catch (Exception e){
                log.error("get x86_mission_queue_cancel error", e);
            }finally {
                lock_x86_mission_queue_cancel.unlock();
            }
        }
        return x86_mission_queue_cancel;
    }

    public static Topic x86_mission_instant_control(Ros ros) throws Exception {
        if (x86_mission_instant_control == null) {
            if(null == ros){
                log.error("get x86_mission_instant_control ros is null error, return null");
                return x86_mission_instant_control;
            }
            lock_x86_mission_instant_control.lock();
            try {
                if (x86_mission_instant_control == null) {
                    x86_mission_instant_control = new Topic(ros, TopicConstants.X86_MISSION_INSTANT_CONTROL, TopicConstants.TOPIC_TYPE_STRING);
                    log.info("get topic x86_mission_instant_control="+x86_mission_instant_control);
                }
            }catch (Exception e){
                log.error("get x86_mission_instant_control error", e);
            }finally {
                lock_x86_mission_instant_control.unlock();
            }
        }
        return x86_mission_instant_control;
    }

    public static Topic x86_mission_common_request(Ros ros) throws Exception {
        if (x86_mission_common_request == null) {
            if(null == ros){
                log.error("get x86_mission_common_request ros is null error, return null");
                return x86_mission_common_request;
            }
            lock_x86_mission_common_request.lock();
            try {
                if (x86_mission_common_request == null) {
                    x86_mission_common_request = new Topic(ros, TopicConstants.X86_MISSION_COMMON_REQUEST, TopicConstants.TOPIC_TYPE_STRING);
                    log.info("get topic x86_mission_common_request="+x86_mission_common_request);
                }
            }catch (Exception e){
                log.error("get x86_mission_common_request error", e);
            }finally {
                lock_x86_mission_common_request.unlock();
            }
        }
        return x86_mission_common_request;
    }

    public static Topic x86_mission_queue_response(Ros ros) throws Exception {
        if (x86_mission_queue_response == null) {
            if(null == ros){
                log.error("get x86_mission_queue_response ros is null error, return null");
                return x86_mission_queue_response;
            }
            lock_x86_mission_queue_response.lock();
            try {
                if (x86_mission_queue_response == null) {
                    x86_mission_queue_response = new Topic(ros, TopicConstants.X86_MISSION_QUEUE_RESPONSE, TopicConstants.TOPIC_TYPE_STRING);
                    log.info("get topic x86_mission_queue_response="+x86_mission_queue_response);
                }
            }catch (Exception e){
                log.error("get x86_mission_queue_response error", e);
            }finally {
                lock_x86_mission_queue_response.unlock();
            }
        }
        return x86_mission_queue_response;
    }

    public static Topic x86_mission_state_response(Ros ros) throws Exception {
        if (x86_mission_state_response == null) {
            if(null == ros){
                log.error("get x86_mission_state_response ros is null error, return null");
                return x86_mission_state_response;
            }
            lock_x86_mission_state_response.lock();
            try {
                if (x86_mission_state_response == null) {
                    x86_mission_state_response = new Topic(ros, TopicConstants.X86_MISSION_STATE_RESPONSE, TopicConstants.TOPIC_TYPE_STRING);
                    log.info("get topic x86_mission_state_response="+x86_mission_state_response);
                }
            }catch (Exception e){
                log.error("get x86_mission_state_response error", e);
            }finally {
                lock_x86_mission_state_response.unlock();
            }
        }
        return x86_mission_state_response;
    }

    public static Topic x86_mission_event(Ros ros) throws Exception {
        if (x86_mission_event == null) {
            if(null == ros){
                log.error("get x86_mission_event ros is null error, return null");
                return x86_mission_event;
            }
            lock_x86_mission_event.lock();
            try {
                if (x86_mission_event == null) {
                    x86_mission_event = new Topic(ros, TopicConstants.X86_MISSION_EVENT, TopicConstants.TOPIC_TYPE_STRING);
                    log.info("get topic x86_mission_event="+x86_mission_event);
                }
            }catch (Exception e){
                log.error("get x86_mission_event error", e);
            }finally {
                lock_x86_mission_event.unlock();
            }
        }
        return x86_mission_event;
    }

    public static Topic x86_mission_alert(Ros ros) throws Exception {
        if (x86_mission_alert == null) {
            if(null == ros){
                log.error("get x86_mission_alert ros is null error, return null");
                return x86_mission_alert;
            }
            lock_x86_mission_alert.lock();
            try {
                if (x86_mission_alert == null) {
                    x86_mission_alert = new Topic(ros, TopicConstants.X86_MISSION_ALERT, TopicConstants.TOPIC_TYPE_STRING);
                    log.info("get topic x86_mission_alert="+x86_mission_alert);
                }
            }catch (Exception e){
                log.error("get x86_mission_alert error", e);
            }finally {
                lock_x86_mission_alert.unlock();
            }
        }
        return x86_mission_alert;
    }

    public static Topic x86_mission_receive(Ros ros) throws Exception {
        if (x86_mission_receive == null) {
            if(null == ros){
                log.error("get x86_mission_receive ros is null error, return null");
                return x86_mission_receive;
            }
            lock_x86_mission_receive.lock();
            try {
                if (x86_mission_receive == null) {
                    x86_mission_receive = new Topic(ros, TopicConstants.X86_MISSION_RECEIVE, TopicConstants.TOPIC_TYPE_STRING);
                    log.info("get topic x86_mission_receive="+x86_mission_receive);
                }
            }catch (Exception e){
                log.error("get x86_mission_receive error", e);
            }finally {
                lock_x86_mission_receive.unlock();
            }
        }
        return x86_mission_receive;
    }

    public static Topic x86_elevator_lock(Ros ros) throws Exception {
        if (x86_elevator_lock == null) {
            if(null == ros){
                log.error("get x86_elevator_lock ros is null error, return null");
                return x86_elevator_lock;
            }
            lock_x86_elevator_lock.lock();
            try {
                if (x86_elevator_lock == null) {
                    x86_elevator_lock = new Topic(ros, TopicConstants.X86_ELEVATOR_LOCK, TopicConstants.TOPIC_TYPE_STRING);
                    log.info("get topic x86_elevator_lock="+x86_elevator_lock);
                }
            }catch (Exception e){
                log.error("get x86_elevator_lock error", e);
            }finally {
                lock_x86_elevator_lock.unlock();
            }
        }
        return x86_elevator_lock;
    }

    public static Topic x86_roadpath_lock(Ros ros) throws Exception {
        if (x86_roadpath_lock == null) {
            if(null == ros){
                log.error("get x86_roadpath_lock ros is null error, return null");
                return x86_roadpath_lock;
            }
            lock_x86_roadpath_lock.lock();
            try {
                if (x86_roadpath_lock == null) {
                    x86_roadpath_lock = new Topic(ros, TopicConstants.X86_ROADPATH_LOCK, TopicConstants.TOPIC_TYPE_STRING);
                    log.info("get topic x86_roadpath_lock="+x86_roadpath_lock);
                }
            }catch (Exception e){
                log.error("get x86_roadpath_lock error", e);
            }finally {
                lock_x86_roadpath_lock.unlock();
            }
        }
        return x86_roadpath_lock;
    }

    public static Topic android_joystick_cmd_vel(Ros ros) throws Exception {
        if (android_joystick_cmd_vel == null) {
            if(null == ros){
                log.error("get android_joystick_cmd_vel ros is null error, return null");
                return android_joystick_cmd_vel;
            }
            lock_android_joystick_cmd_vel.lock();
            try {
                if (android_joystick_cmd_vel == null) {
                    android_joystick_cmd_vel = new Topic(ros, TopicConstants.ANDROID_JOYSTICK_CMD_VEL, TopicConstants.ROS_YAOGAN_TOPIC_TYPE);
                    log.info("get topic android_joystick_cmd_vel="+android_joystick_cmd_vel);
                }
            }catch (Exception e){
                log.error("get android_joystick_cmd_vel error", e);
            }finally {
                lock_android_joystick_cmd_vel.unlock();
            }
        }
        return android_joystick_cmd_vel;
    }

    public static Topic state_collector(Ros ros) throws Exception {
        if (state_collector == null) {
            if(null == ros){
                log.error("get state_collector ros is null error, return null");
                return state_collector;
            }
            lock_state_collector.lock();
            try {
                if (state_collector == null) {
                    state_collector = new Topic(ros, TopicConstants.STATE_COLLECTOR, TopicConstants.TOPIC_TYPE_STRING);
                    log.info("get topic state_collector="+state_collector);
                }
            }catch (Exception e){
                log.error("get state_collector error", e);
            }finally {
                lock_state_collector.unlock();
            }
        }
        return state_collector;
    }


    public static Topic power(Ros ros) throws Exception {
        if (power == null) {
            if(null == ros){
                log.error("get power ros is null error, return null");
                return power;
            }
            lock_power.lock();
            try {
                if (power == null) {
                    power = new Topic(ros, TopicConstants.POWER, UInt8MultiArray.TYPE);
                    log.info("get topic power="+power);
                }
            }catch (Exception e){
                log.error("get power error", e);
            }finally {
                lock_power.unlock();
            }
        }
        return power;
    }

    public static Topic checkHeartTopic(Ros ros) throws Exception {
        if (checkHeartTopic == null) {
            if(null == ros){
                log.error("get checkHeartTopic ros is null error, return null");
                return checkHeartTopic;
            }
            lock_checkHeartTopic.lock();
            try {
                if (checkHeartTopic == null) {
                    checkHeartTopic = new Topic(ros, TopicConstants.CHECK_HEART_TOPIC, TopicConstants.TOPIC_TYPE_STRING);
                    log.info("get topic checkHeartTopic="+checkHeartTopic);
                }
            }catch (Exception e){
                log.error("get checkHeartTopic error", e);
            }finally {
                lock_checkHeartTopic.unlock();
            }
        }
        return checkHeartTopic;
    }

    public static Topic x86_mission_heartbeat(Ros ros) throws Exception {
        if (x86_mission_heartbeat == null) {
            if(null == ros){
                log.error("get x86_mission_heartbeat ros is null error, return null");
                return x86_mission_heartbeat;
            }
            lock_x86_mission_heartbeat.lock();
            try {
                if (x86_mission_heartbeat == null) {
                    x86_mission_heartbeat = new Topic(ros, TopicConstants.X86_MISSION_HEARTBEAT, TopicConstants.TOPIC_TYPE_STRING);
                    log.info("get topic x86_mission_heartbeat="+x86_mission_heartbeat);
                }
            }catch (Exception e){
                log.error("get x86_mission_heartbeat error", e);
            }finally {
                lock_x86_mission_heartbeat.unlock();
            }
        }
        return x86_mission_heartbeat;
    }


}
