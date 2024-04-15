package exceptions;

public class FactException extends Exception
{
	private static final long serialVersionUID = 1L;

	public FactException()
	{
	}

	public FactException(String message)
	{
		super(message);
	}

	public FactException(Throwable cause)
	{
		super(cause);
	}

	public FactException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public FactException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
	{
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
