package cn.mrobot.bean.mission;

/**
 * Created with IntelliJ IDEA.
 * Project Name : goor-common
 * User: Jelynn
 * Date: 2017/6/13
 * Time: 9:37
 * Describe:
 * Version:1.0
 */
public class MissionListMissionXREF {

//	private Long id;

	private MissionList missionList;

	private Long missionListId;

	private Mission mission;

	private Long missionId;

//	public Long getId() {
//		return id;
//	}
//
//	public void setId(Long id) {
//		this.id = id;
//	}

	public MissionList getMissionList() {
		return missionList;
	}

	public void setMissionList(MissionList missionList) {
		this.missionList = missionList;
	}

	public Long getMissionListId() {
		return missionListId;
	}

	public void setMissionListId(Long missionListId) {
		this.missionListId = missionListId;
	}

	public Mission getMission() {
		return mission;
	}

	public void setMission(Mission mission) {
		this.mission = mission;
	}

	public Long getMissionId() {
		return missionId;
	}

	public void setMissionId(Long missionId) {
		this.missionId = missionId;
	}


	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		MissionListMissionXREF that = (MissionListMissionXREF) o;

		if (missionListId != null && missionId != null){
			return missionId.equals(that.getMissionId()) && missionListId.equals(that.getMissionListId());
		}
		return false;
	}

//	@Override
//	public int hashCode() {
//		int result = id != null ? id.hashCode() : 0;
//		result = 31 * result + (missionListId != null ? missionListId.hashCode() : 0);
//		result = 31 * result + (missionId != null ? missionId.hashCode() : 0);
//		return result;
//	}
}
