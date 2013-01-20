package appixia.engine;

import java.util.HashMap;
import appixia.engine.Mediums.Encoder;
import appixia.engine.Mediums.Container;

public class Request
{
	public Container data;
	public HashMap<String,String> metadata;
	public Encoder encoder;
}
