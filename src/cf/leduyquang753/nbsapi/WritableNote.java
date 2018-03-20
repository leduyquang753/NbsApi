package cf.leduyquang753.nbsapi;

public class WritableNote {
	private Instrument instrument;
	private byte pitch;
	private int layer;
	private int location;
	
	public WritableNote(Instrument instrument, byte pitch, int layer, int location) throws IllegalArgumentException {
		if (pitch < 0 || pitch > 87) throw new IllegalArgumentException("Pitch must be frrom 0 to 87.");
		setInstrument(instrument);
		setPitch(pitch);
		setLayer(layer);
		setLocation(location);
	}

	public Instrument getInstrument() {
		return instrument;
	}

	public void setInstrument(Instrument instrument) {
		this.instrument = instrument;
	}

	public byte getPitch() {
		return pitch;
	}

	public void setPitch(byte pitch) throws IllegalArgumentException {
		if (pitch < 0 || pitch > 87) throw new IllegalArgumentException("Pitch must be from 0 to 87.");
		this.pitch = pitch;
	}

	public int getLocation() {
		return location;
	}

	public void setLocation(int location) {
		this.location = location;
	}

	public int getLayer() {
		return layer;
	}

	public void setLayer(int layer) {
		this.layer = layer;
	}
}
