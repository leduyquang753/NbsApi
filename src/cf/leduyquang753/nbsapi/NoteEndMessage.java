package cf.leduyquang753.nbsapi;

import javax.sound.midi.ShortMessage;

/**
 * A MIDI message to end a note.
 * @author Le Duy Quang
 *
 */
public class NoteEndMessage extends ShortMessage {
	public NoteEndMessage(int instrument, byte pitch) {
		super(new byte[] {(byte)(0b10000000 | instrument), (byte) (pitch+9), 64});
	}
	
	@Override
	public ShortMessage clone() {
		return this;
	}
}
