import com.enseed.mdtk.client.errors.ClientException;


public class NotFoundException extends ClientException {
	private static final long serialVersionUID = -898854202608293429L;

	public NotFoundException(String what, Exception cause) {
		super(what, cause);
	}

	public NotFoundException(Exception cause) {
		super(cause);
	}
}
