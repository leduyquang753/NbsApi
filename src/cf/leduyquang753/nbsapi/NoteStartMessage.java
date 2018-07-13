package cf.leduyquang753.nbsapi;

import javax.sound.midi.ShortMessage;

public class NoteStartMessage extends ShortMessage {
	public NoteStartMessage(int instrument, byte pitch) {
		super(new byte[] {(byte)(0b10010000 | instrument), (byte) (pitch+9), 64});
	}
	
	@Override
	public ShortMessage clone() {
		return this;
	}
}
