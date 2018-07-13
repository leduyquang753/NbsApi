package cf.leduyquang753.nbsapi;

import javax.sound.midi.ShortMessage;

/**
 * A MIDI message to set channel's instruments.
 * @author Le Duy Quang
 *
 */
public class InstrumentMessage extends ShortMessage {	
	public InstrumentMessage(int id) {
		super(new byte[] {-63, 0});
	}
}
