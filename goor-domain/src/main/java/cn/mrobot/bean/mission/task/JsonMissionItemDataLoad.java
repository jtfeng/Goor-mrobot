package cn.mrobot.bean.mission.task;

import cn.mrobot.bean.assets.shelf.Shelf;

import java.io.Serializable;

/**
 * Created by abel on 17-7-19.
 */
public class JsonMissionItemDataLoad implements Serializable {

    private static final long serialVersionUID = 1L;

    Shelf shelf;

    public Shelf getShelf() {
        return shelf;
    }

    public void setShelf(Shelf shelf) {
        this.shelf = shelf;
    }
}
