package cf.leduyquang753.nbsapi;

import javax.sound.midi.*;

/**
 * A MIDI meta message to set the tempo.
 * @author Le Duy Quang
 *
 */
public class TempoMessage extends MetaMessage {
	
	/**
	 * Used internally to set the message data.
	 * @param tps Song's ticks per second.
	 * @return The message's data for invoking the super constructor.
	 */
	private static byte[] getData(float tps) {
		int lengthEachBeat = (int)(4000000f/tps);
		byte[] data = new byte[3];
		for (int i = 0; i < 3; i++) {
			data[2-i] = (byte)((lengthEachBeat >> 8*i) & 0xFF);
		}
		return data;
	}
	
	public TempoMessage(float tps) throws InvalidMidiDataException {
		super(0x51, getData(tps), 3);
	}
	
	public MetaMessage clone() {
		return this;
	}
}
