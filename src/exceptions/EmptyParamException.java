package exceptions;

public class EmptyParamException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public EmptyParamException(String param) {
		super("O parametro " + param + " esta vazio");
	}
}
