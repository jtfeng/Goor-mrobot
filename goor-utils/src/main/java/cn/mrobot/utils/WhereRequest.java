package cn.mrobot.utils;



/**
 * Created by Ray.Fu on 2016/7/20.
 */
public class WhereRequest {

	private int page;

	private int pageSize;

    private String queryObj;

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public String getQueryObj() {
		return queryObj;
	}

	public void setQueryObj(String queryObj) {
		this.queryObj = queryObj;
	}
}
