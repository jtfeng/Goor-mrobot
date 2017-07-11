package cn.mrobot.bean.mission;

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

//	private Long id;

	private Mission mission;

	private Long missionId;

	private MissionItem missionItem;

	private Long missionItemId;

//	public Long getId() {
//		return id;
//	}
//
//	public void setId(Long id) {
//		this.id = id;
//	}

	public Long getMissionId() {
		return missionId;
	}

	public void setMissionId(Long missionId) {
		this.missionId = missionId;
	}

	public Long getMissionItemId() {
		return missionItemId;
	}

	public void setMissionItemId(Long missionItemId) {
		this.missionItemId = missionItemId;
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

		if (missionItemId != null && missionId != null){
			return missionId.equals(that.missionId) && missionItemId.equals(that.missionItemId);
		}
		return false;
	}

//	@Override
//	public int hashCode() {
//		int result = id != null ? id.hashCode() : 0;
//		result = 31 * result + missionId.hashCode();
//		result = 31 * result + missionItemId.hashCode();
//		return result;
//	}
}
