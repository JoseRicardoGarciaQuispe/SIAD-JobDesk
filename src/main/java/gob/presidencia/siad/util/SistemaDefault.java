package gob.presidencia.siad.util;

public enum SistemaDefault {

	URLREDIRECTION("1");
	
	public static final String CODTABLA = "URLS_SWEB";
	
	private String code;

	private SistemaDefault(String _code) {
		code = _code;
	}

	public String getCode() {
		return code;
	}

}
