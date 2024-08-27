package main;

public class ResponseGeneric {
	private boolean success; 
	private Object data;
	private String message;
	
	public ResponseGeneric(boolean sucess,  Object data, String message) {
		this.data = data;
		this.success = sucess;
		this.message = message;
	}
	
	public static ResponseGeneric success(Object data) {
		return new ResponseGeneric(true, data, "OK");
	}
	
	public static ResponseGeneric error(String message) {
		return new ResponseGeneric(false, null, message);
	}
}
