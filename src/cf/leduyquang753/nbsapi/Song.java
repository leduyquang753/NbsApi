package cf.leduyquang753.nbsapi;

import java.util.*;
import java.io.*;

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
	
	public Song(
			short length,
			short height,
			String name,
			String author,
			String originalAuthor,
			String description,
			short tempo,
			boolean autoSave,
			byte autoSaveDuration,
			byte timeSignature,
			int leftClicks,
			int rightClicks,
			int blocksAdded,
			int blocksRemoved,
			String MidiSchematicFile,
			List<Layer> songBoard) throws IllegalArgumentException {
		setLength(length);
		setHeight(height);
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
		setLeftClicks(readInt());
		setRightClicks(readInt());
		setBlocksAdded(readInt());
		setBlocksRemoved(readInt());
		setMidiSchematicFile(readString());
		
		songBoard = new ArrayList<Layer>();
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
				if (songBoard.size() < layer+1)
					for (int i = 0; i < layer+1-songBoard.size(); i++) songBoard.add(new Layer("",(byte) 100));
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
	
	public void writeSong(File toFile) throws IOException {
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
		
		outstream = new FileOutputStream(toFile);
		out = new DataOutputStream(outstream);
		
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
		out.writeByte(num >> 8);
	}
	
	private void writeInt(int num) throws IOException {
		out.writeByte(num % 256);
		out.writeByte(num >> 8);
		out.writeByte(num >> 16);
		out.writeByte(num >> 24);
	}
	
	private void writeString(String str) throws IOException {
		writeInt(str.length());
		out.writeBytes(str);
	}
}
