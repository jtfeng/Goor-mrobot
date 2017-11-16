package cn.mrobot.bean.mission.task;

import java.io.Serializable;
import java.util.List;

/**
 * Created by abel on 17-7-19.
 */
public class JsonMissionItemDataMp3 implements Serializable {
    private static final long serialVersionUID = -5878496748298850437L;

    private String filename;
    private String resscene;
    private List<String> filenames; //mp3播放列表

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getResscene() {
        return resscene;
    }

    public void setResscene(String resscene) {
        this.resscene = resscene;
    }

    public List<String> getFilenames() {
        return filenames;
    }

    public void setFilenames(List<String> filenames) {
        this.filenames = filenames;
    }
}
