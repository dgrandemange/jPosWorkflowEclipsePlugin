package org.jpos.jposext.jposworkflow.io;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author dgrandemange
 *
 */
public class EnclosedInputStream extends InputStream {
	private enum State {
		PREFIX, STREAM, SUFFIX, EOF
	};

	private final byte[] prefix;
	private final InputStream stream;
	private final byte[] suffix;
	private State state = State.PREFIX;
	private int index;

	public EnclosedInputStream(byte[] prefix, InputStream stream, byte[] suffix) {
		this.prefix = prefix;
		this.stream = stream;
		this.suffix = suffix;
	}

	@Override
	public int read() throws IOException {
		if (state == State.PREFIX) {
			if (index < prefix.length) {
				return prefix[index++] & 0xFF;
			}
			state = State.STREAM;
		}
		if (state == State.STREAM) {
			int r = stream.read();
			if (r >= 0) {
				return r;
			}
			state = State.SUFFIX;
			index = 0;
		}
		if (state == State.SUFFIX) {
			if (index < suffix.length) {
				return suffix[index++] & 0xFF;
			}
			state = State.EOF;
		}
		return -1;
	}
}