package utils.io;

import java.io.IOException;
import java.io.OutputStream;

public interface BytesWriter {
	void writeTo(OutputStream out) throws IOException;
}
