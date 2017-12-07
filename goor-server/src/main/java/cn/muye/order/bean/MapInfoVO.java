package cn.muye.order.bean;

/**
 * Created by Selim on 2017/12/5.
 */
public class MapInfoVO {

    private String sceneName;

    private String mapName;

    private String rosMapUrl; //ros地图url

    private String beautifyMapUrl;  //美化版地图url

    private MapPointVO originMapPoint;  //原点坐标

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

    public String getRosMapUrl() {
        return rosMapUrl;
    }

    public void setRosMapUrl(String rosMapUrl) {
        this.rosMapUrl = rosMapUrl;
    }

    public String getBeautifyMapUrl() {
        return beautifyMapUrl;
    }

    public void setBeautifyMapUrl(String beautifyMapUrl) {
        this.beautifyMapUrl = beautifyMapUrl;
    }

    public MapPointVO getOriginMapPoint() {
        return originMapPoint;
    }

    public void setOriginMapPoint(MapPointVO originMapPoint) {
        this.originMapPoint = originMapPoint;
    }
}
