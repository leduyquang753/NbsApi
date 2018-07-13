package cf.leduyquang753.nbsapi;

import java.util.*;

import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.Track;

import java.io.*;

/**
 * A note block song.
 * @author Le Duy Quang
 *
 */
public class Song {
	private short length;
	private short height;
	private String name;
	private String author;
	private String originalAuthor;
	private String description;
	private short tempo;
	private boolean autoSave;
	private byte autoSaveDuration;
	private byte timeSignature;
	private int minutesSpent;
	private int leftClicks;
	private int rightClicks;
	private int blocksAdded;
	private int blocksRemoved;
	private String MidiSchematicFile;
	private List<Layer> songBoard;
	
	private FileInputStream instream;
	private DataInputStream in;
	private FileOutputStream outstream;
	private DataOutputStream out;
	
	/**
	 * Builds a new song with the given information.
	 * @param length
	 * @param name
	 * @param author
	 * @param originalAuthor
	 * @param description
	 * @param tempo
	 * @param autoSave
	 * @param autoSaveDuration
	 * @param timeSignature
	 * @param minutesSpent
	 * @param leftClicks
	 * @param rightClicks
	 * @param blocksAdded
	 * @param blocksRemoved
	 * @param MidiSchematicFile
	 * @param songBoard
	 * @throws IllegalArgumentException
	 */
	public Song(
			short length,
			String name,
			String author,
			String originalAuthor,
			String description,
			short tempo,
			boolean autoSave,
			byte autoSaveDuration,
			byte timeSignature,
			int minutesSpent,
			int leftClicks,
			int rightClicks,
			int blocksAdded,
			int blocksRemoved,
			String MidiSchematicFile,
			List<Layer> songBoard) throws IllegalArgumentException {
		setLength(length);
		setName(name);
		setAuthor(author);
		setOriginalAuthor(originalAuthor);
		setDescription(description);
		setTempo(tempo);
		setAutoSave(autoSave);
		setAutoSaveDuration(autoSaveDuration);
		setTimeSignature(timeSignature);
		setLeftClicks(leftClicks);
		setRightClicks(rightClicks);
		setBlocksAdded(blocksAdded);
		setBlocksRemoved(blocksRemoved);
		setMidiSchematicFile(MidiSchematicFile);
		changeSongBoardTo(songBoard);
	}
	
	/**
	 * Reads a song from a file.
	 * @param fromFile The file that should be read.
	 * @throws IOException
	 */
	public Song(File fromFile) throws IOException {
		instream = new FileInputStream(fromFile);
		in = new DataInputStream(instream);
		setLength(readShort());
		setHeight(readShort());
		setName(readString());
		setAuthor(readString());
		setOriginalAuthor(readString());
		setDescription(readString());
		setTempo(readShort());
		setAutoSave(in.readBoolean());
		setAutoSaveDuration(in.readByte());
		setTimeSignature(in.readByte());
		setMinutesSpent(readInt());
		setLeftClicks(readInt());
		setRightClicks(readInt());
		setBlocksAdded(readInt());
		setBlocksRemoved(readInt());
		setMidiSchematicFile(readString());
		
		songBoard = new ArrayList<Layer>();
		for (int i = 0; i < height; i++) songBoard.add(new Layer("",(byte) 100));
		int tick = -1;
		while (true) {
			short jumpDistance = readShort();
			if (jumpDistance == 0) break;
			tick += jumpDistance;
			short layer = -1;
			while (true) {
				short jumpLayers = readShort();
				if (jumpLayers == 0) break;
				layer += jumpLayers;
				while (songBoard.size() < layer+1) {
					songBoard.add(new Layer("",(byte) 100));
				}
				songBoard.get(layer).setNote(tick, new Note(Instrument.fromID(in.readByte()), in.readByte()));
			}
		}
		for (int i = 0; i < getHeight(); i++) {
			songBoard.get(i).setName(readString());
			songBoard.get(i).setVolume(in.readByte());
		}
		in.close();
		instream.close();
	}
	
	/**
	 * Writes the song to the specific file.
	 * @param file The file to write to.
	 * @throws IOException
	 */
	public void writeSongToFile(File file) throws IOException {
		short maxLength = -1;
		for (Layer l : songBoard) {
			short maxPos = -1;
			for (int i : l.getNoteList().keySet()) {
				if (i > maxPos) maxPos = (short) i;
			}
			if (maxPos > maxLength) maxLength = maxPos;
		}
		setLength(maxLength);
		setHeight((short) songBoard.size());
		
		outstream = new FileOutputStream(file);
		out = new DataOutputStream(outstream);
		writeShort(length);
		writeShort(height);
		writeString(name);
		writeString(author);
		writeString(originalAuthor);
		writeString(description);
		writeShort(tempo);
		out.writeByte(autoSave ? 1 : 0);
		out.writeByte(autoSaveDuration);
		out.writeByte(timeSignature);
		writeInt(minutesSpent);
		writeInt(leftClicks);
		writeInt(rightClicks);
		writeInt(blocksAdded);
		writeInt(blocksRemoved);
		writeString(MidiSchematicFile);
		
		List<NoteWithFullInfo> noteList = Utils.convertToWritable(songBoard);
		int oldTick = -1;
		int oldLayer = -1;
		for (NoteWithFullInfo i : noteList) {
			if (i.getLocation() > oldTick) {
				if (oldTick != -1) writeShort((short)0);
				writeShort((short) (i.getLocation() - oldTick));
				oldTick = i.getLocation();
				oldLayer = -1;
			}
			writeShort((short)(i.getLayer() - oldLayer));
			oldLayer = i.getLayer();
			out.writeByte(i.getInstrument().getID());
			out.writeByte(i.getPitch());
		}
		writeShort((short)0);
		writeShort((short)0);
		
		for (Layer l : songBoard) {
			writeString(l.getName());
			out.writeByte(l.getVolume());
		}
		
		out.writeByte(0);
		out.close();
		outstream.close();
	}
	
	/**
	 * Writes the song as MIDI file to be imported into other programs. The MIDI will have 11 channels:
	 * <li>The first channel containing the timing information.</li>
	 * <li>10 channels for 10 instruments.</li> 
	 * @param file The file to be written to.
	 * @throws Exception If any exception occurs, it will be thrown outside, so you have to <b>catch them explicitly</b>.
	 */
	public void writeSongToMidiFile(File file) throws Exception {
		Sequence songOut = new Sequence(Sequence.PPQ, 24);
		Track time = songOut.createTrack();
		time.add(new MidiEvent(new TempoMessage(getTempo()/100f), 0));
		Track[] tracks = new Track[10];
		for (int i = 0; i < 10; i++) {
			tracks[i] = songOut.createTrack();
		}
		for (Layer l : getSongBoard()) {
			HashMap<Integer, Note> list = l.getNoteList();
			for (int loc : list.keySet()) {
				Note n = list.get(loc);
				int instrument = n.getInstrument().getID();
				tracks[instrument].add(new MidiEvent(new NoteStartMessage(instrument, n.getPitch()), loc*6));
				tracks[instrument].add(new MidiEvent(new NoteEndMessage(instrument, n.getPitch()), loc*6+5));
			}
		}
		MidiSystem.write(songOut, MidiSystem.getMidiFileTypes(songOut)[0], file);
	}
	
	/**
	 * Picks a note at an X; Y location.
	 * @param tick The tick (X axis) the note is at.
	 * @param layer The layer (Y axis) the note is on. <b>Zero based.</b>
	 * @return The note specified if there is, or <code>null</code> if there isn't or there is an exception.
	 */
	public Note getNoteAt(int tick, int layer) {
		try {
			return getSongBoard().get(layer).getNoteList().get(tick);
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * Sets a note at an X; Y location. If there is a note already there, it will be overwritten.
	 * @param tick The tick (X axis) the note is at.
	 * @param layer The layer (Y axis) the note is on. <b>Zero based.</b>
	 * @param note The note to be put.
	 * @return A {@link Note} that was there and was overwritten, or {@code null} if there wasn't a note there previously.
	 */
	public Note setNoteAt(int tick, int layer, Note note) {
		List<Layer> songBoard = getSongBoard();
		while(songBoard.size() < layer+1) {
			songBoard.add(new Layer());
		}
		Layer l = songBoard.get(layer);
		Note old = null;
		if (l.getNoteList().containsKey(tick)) old = l.getNoteList().get(tick);
		songBoard.get(layer).setNote(tick, note);
		return old;
	}
	
	/**
	 * Injects a note with full information into the song. If there is already a note at its location, that note will be overwritten.
	 * @param note The note to be injected.
	 * @return A {@link Note} that was at its location and was overwritten, or {@code null} if there wasn't a note there previously.
	 */
	public Note injectNote(NoteWithFullInfo note) {
		return setNoteAt(note.getLocation(), note.getLayer(), new Note(note.getInstrument(), note.getPitch()));
	}
	
	/**
	 * Retrieves all notes in the song.
	 * @return An {@link ArrayList} of {@link NoteWithFullInfo}s, each containing its full information, including its location.
	 */
	public List<NoteWithFullInfo> getAllNotes() {
		List<NoteWithFullInfo> notes = new ArrayList<NoteWithFullInfo>();
		int index = -1;
		for (Layer l : getSongBoard()) {
			index++;
			HashMap<Integer, Note> li = l.getNoteList();
			for (int loc : li.keySet()) {
				Note n = li.get(loc);
				notes.add(new NoteWithFullInfo(n.getInstrument(), n.getPitch(), index, loc));
			}
		}
		return notes;
	}
	
	public short getLength() {
		return length;
	}
	protected void setLength(short length) throws IllegalArgumentException {
		if (length < 0) throw new IllegalArgumentException("Song length must not be negative.");
		this.length = length;
	}
	public short getHeight() {
		return height;
	}
	protected void setHeight(short height) throws IllegalArgumentException {
		if (height < 0) throw new IllegalArgumentException("Song height must not be negative.");
		this.height = height;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getOriginalAuthor() {
		return originalAuthor;
	}
	public void setOriginalAuthor(String originalAuthor) {
		this.originalAuthor = originalAuthor;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public short getTempo() {
		return tempo;
	}
	public void setTempo(short tempo) throws IllegalArgumentException {
		if (tempo < 25) throw new IllegalArgumentException("Tempo is too small!");
		if (tempo%25 != 0) throw new IllegalArgumentException("Tempo must be a multiplication of 25.");
		this.tempo = tempo;
	}
	public boolean isAutoSaveEnabled() {
		return autoSave;
	}
	public void setAutoSave(boolean autoSave) {
		this.autoSave = autoSave;
	}
	public byte getAutoSaveDuration() {
		return autoSaveDuration;
	}
	public void setAutoSaveDuration(byte autoSaveDuration) throws IllegalArgumentException {
		if (autoSaveDuration < 1 || autoSaveDuration > 60) throw new IllegalArgumentException("Auto-save duration must be from 1 to 60.");
		this.autoSaveDuration = autoSaveDuration;
	}
	public byte getTimeSignature() {
		return timeSignature;
	}
	public void setTimeSignature(byte timeSignature) throws IllegalArgumentException {
		if (timeSignature < 2 || timeSignature > 8) throw new IllegalArgumentException("Time signature must be from 2 to 8.");
		this.timeSignature = timeSignature;
	}
	public int getMinutesSpent() {
		return minutesSpent;
	}
	public void setMinutesSpent(int minutesSpent) throws IllegalArgumentException {
		if (minutesSpent < 0) throw new IllegalArgumentException("RMinutes spent must not be negative.");
		this.minutesSpent = minutesSpent;
	}
	public int getRightClicks() {
		return rightClicks;
	}
	public void setRightClicks(int rightClicks) throws IllegalArgumentException {
		if (rightClicks < 0) throw new IllegalArgumentException("Right-click count must not be negative.");
		this.rightClicks = rightClicks;
	}
	public int getLeftClicks() {
		return leftClicks;
	}
	public void setLeftClicks(int leftClicks) throws IllegalArgumentException {
		if (leftClicks < 0) throw new IllegalArgumentException("Left-click count must not be negative.");
		this.leftClicks = leftClicks;
	}
	public int getBlocksAdded() {
		return blocksAdded;
	}
	public void setBlocksAdded(int blocksAdded) throws IllegalArgumentException {
		if (blocksAdded < 0) throw new IllegalArgumentException("Blocks added must not be negative.");
		this.blocksAdded = blocksAdded;
	}
	public int getBlocksRemoved() {
		return blocksRemoved;
	}
	public void setBlocksRemoved(int blocksRemoved) throws IllegalArgumentException {
		if (blocksRemoved < 0) throw new IllegalArgumentException("Blocks removed must not be negative.");
		this.blocksRemoved = blocksRemoved;
	}
	public String getMidiSchematicFile() {
		return MidiSchematicFile;
	}
	public void setMidiSchematicFile(String midiSchematicFile) {
		MidiSchematicFile = midiSchematicFile;
	}

	public List<Layer> getSongBoard() {
		return songBoard;
	}

	public void changeSongBoardTo(List<Layer> songBoard) {
		this.songBoard = songBoard;
	}
	
	// The code below is imported from xxmicloxx's NoteBlockAPI (LGPL 3.0).
	
	private short readShort() throws IOException {
		int byte1 = in.readUnsignedByte();
        int byte2 = in.readUnsignedByte();
        return (short) (byte1 + (byte2 << 8));
	}
	
	private int readInt() throws IOException {
        int byte1 = in.readUnsignedByte();
        int byte2 = in.readUnsignedByte();
        int byte3 = in.readUnsignedByte();
        int byte4 = in.readUnsignedByte();
        return (byte1 + (byte2 << 8) + (byte3 << 16) + (byte4 << 24));
    }
	
	private String readString() throws IOException {
        int length = readInt();
        StringBuilder sb = new StringBuilder(length);
        for (; length > 0; --length) {
            char c = (char) in.readByte();
            if (c == (char) 0x0D) {
                c = ' ';
            }
            sb.append(c);
        }
        return sb.toString();
    }
	
	// End of inported code.
	
	private void writeShort(short num) throws IOException {
		out.writeByte(num%256);
		out.writeByte(num/256);
	}
	
	private void writeInt(int num) throws IOException {
		out.writeByte(num%256);
		out.writeByte(num%65536/256);
		out.writeByte(num%16777216/65536);
		out.writeByte(num/16777216);
	}
	
	private void writeString(String str) throws IOException {
		writeInt(str.length());
		out.writeBytes(str);
	}
}
