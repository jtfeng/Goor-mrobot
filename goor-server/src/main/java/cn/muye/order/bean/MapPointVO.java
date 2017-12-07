package cn.muye.order.bean;

/**
 * Created by Selim on 2017/11/29.
 * 坐标显示
 */
public class MapPointVO {

    private Double x;

    private Double y;

    private Double th;

    private String mapName; //地图名称

    private String sceneName;  //场景名称

    private String name; //坐标关联的位置名称

    public Double getX() {
        return x;
    }

    public void setX(Double x) {
        this.x = x;
    }

    public Double getY() {
        return y;
    }

    public void setY(Double y) {
        this.y = y;
    }

    public Double getTh() {
        return th;
    }

    public void setTh(Double th) {
        this.th = th;
    }

    public String getMapName() {
        return mapName;
    }

    public void setMapName(String mapName) {
        this.mapName = mapName;
    }

    public String getSceneName() {
        return sceneName;
    }

    public void setSceneName(String sceneName) {
        this.sceneName = sceneName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
