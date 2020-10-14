package org.jumpmind.pos.print;

import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PeripheralConnection {

	OutputStream out; // write to printer.
	InputStream in; // read status, etc. from printer.
	Object rawConnection;

	public void close() {
		IOUtils.closeQuietly(in, out);
	}
}
