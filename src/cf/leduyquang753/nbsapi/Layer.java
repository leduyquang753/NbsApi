package cf.leduyquang753.nbsapi;

import java.util.HashMap;

public class Layer {
	private HashMap<Integer, Note> notes = new HashMap<Integer, Note>();
	private String name;
	private byte volume;
	
	public Layer(String name, byte volume) throws IllegalArgumentException {
		setName(name);
		setVolume(volume);
	}
	
	public HashMap<Integer, Note> getNoteList() {
		return notes;
	}
	
	public void setNote(int pos, Note note) throws IllegalArgumentException {
		if (pos < 0) throw new IllegalArgumentException("Note position must not be negative.");
		notes.put(pos, note);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public byte getVolume() {
		return volume;
	}

	public void setVolume(byte volume) throws IllegalArgumentException {
		if (volume < 0 || volume > 100) throw new IllegalArgumentException("Volume must be from 0 to 100.");
		this.volume = volume;
	}
}
