package cn.muye.bean;

public class AjaxResult {
	public static final int CODE_SUCCESS = 0;
	public static final int CODE_FAILED = 1;
	private int code;
	private String message;
	private Object data;

	public AjaxResult(int code, Object data, String message) {
		this.code = code;
		this.message = message;
		this.data = data;
	}

	public String getMessage() {
		return message;
	}

	public Object getData() {
		return data;
	}

	public int getCode(){return code;}

	public boolean isSuccess() {
		return CODE_SUCCESS == code;
	}

	public static final AjaxResult success() {
		return new AjaxResult(CODE_SUCCESS, null, null);
	}

	public static final AjaxResult success(Object data) {
		return new AjaxResult(CODE_SUCCESS, data, null);
	}

	public static final AjaxResult success(Object data, String message) {
		return new AjaxResult(CODE_SUCCESS, data, message);
	}

	public static final AjaxResult failed() {
		return new AjaxResult(CODE_FAILED, null, null);
	}

	public static final AjaxResult failed(String message) {
		return new AjaxResult(CODE_FAILED, null, message);
	}

	public static final AjaxResult failed(Object data, String message) {
		return new AjaxResult(CODE_FAILED, data, message);
	}
	
	public static final AjaxResult failed(Object data) {
		return new AjaxResult(CODE_FAILED, data, "");
	}
	
}
