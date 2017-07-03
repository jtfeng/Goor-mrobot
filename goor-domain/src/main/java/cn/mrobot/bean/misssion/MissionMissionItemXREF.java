package cn.mrobot.bean.misssion;

/**
 * Created with IntelliJ IDEA.
 * Project Name : goor-common
 * User: Jelynn
 * Date: 2017/6/12
 * Time: 14:26
 * Describe:
 * Version:1.0
 */
public class MissionMissionItemXREF {

	private Long id;

	private Mission mission;

	private Long missionChainId;

	private MissionItem missionItem;

	private Long missionNodeId;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getMissionChainId() {
		return missionChainId;
	}

	public void setMissionChainId(Long missionChainId) {
		this.missionChainId = missionChainId;
	}

	public Long getMissionNodeId() {
		return missionNodeId;
	}

	public void setMissionNodeId(Long missionNodeId) {
		this.missionNodeId = missionNodeId;
	}

	public Mission getMission() {
		return mission;
	}

	public void setMission(Mission mission) {
		this.mission = mission;
	}

	public MissionItem getMissionItem() {
		return missionItem;
	}

	public void setMissionItem(MissionItem missionItem) {
		this.missionItem = missionItem;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		MissionMissionItemXREF that = (MissionMissionItemXREF) o;

		if (missionNodeId != null && missionChainId != null){
			return missionChainId.equals(that.missionChainId) && missionNodeId.equals(that.missionNodeId);
		}
		return false;
	}

	@Override
	public int hashCode() {
		int result = id != null ? id.hashCode() : 0;
		result = 31 * result + missionChainId.hashCode();
		result = 31 * result + missionNodeId.hashCode();
		return result;
	}
}
