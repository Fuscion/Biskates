package exceptions;

public class ParamAlreadyExistsException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ParamAlreadyExistsException(String email) {

		super("o " + email + " ja se encontra registado");
	}

}
