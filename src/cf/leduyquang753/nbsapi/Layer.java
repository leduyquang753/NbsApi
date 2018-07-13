package cf.leduyquang753.nbsapi;

import java.util.HashMap;

/**
 * A layer of a song.
 * @author Le Duy Quang
 *
 */
public class Layer {
	private HashMap<Integer, Note> notes = new HashMap<Integer, Note>();
	private String name;
	private byte volume;
	
	/**
	 * Creates an empty layer with name and volume.
	 * @param name The layer's name.
	 * @param volume The layer's volume (0-100).
	 * @throws IllegalArgumentException
	 */
	public Layer(String name, byte volume) throws IllegalArgumentException {
		setName(name);
		setVolume(volume);
	}
	
	/**
	 * Creates an empty layer with defaults (name = ""; volume = 100)
	 */
	public Layer() {
		setName("");
		setVolume((byte) 100);
	}
	
	/**
	 * Returns the note list of the layer.
	 * @return The list.
	 */
	public HashMap<Integer, Note> getNoteList() {
		return notes;
	}
	
	/**
	 * Sets the note at a tick on the layer.
	 * @param pos The tick where the note is at.
	 * @param note The note's properties.
	 * @throws IllegalArgumentException
	 */
	public void setNote(int pos, Note note) throws IllegalArgumentException {
		if (pos < 0) throw new IllegalArgumentException("Note position must not be negative.");
		notes.put(pos, note);
	}
	
	/**
	 * A shortcut to get a note on this layer instead of retrieving it from {@link Layer#getNoteList()}.
	 * @param pos The tick where the note is at.
	 * @return The note if there is a note at that tick, or {@code null} else.
	 */
	public Note getNote(int pos) {
		return getNoteList().get(pos);
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
