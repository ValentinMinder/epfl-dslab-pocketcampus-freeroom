import org.apache.thrift.TException;
import org.pocketcampus.plugin.transport.shared.TransportService;


public class TranspoortServiceImpl implements TransportService.Iface {

	@Override
	public String autocomplete(String constraint) throws TException {
		return "vigie";
	}

}
