package cf.leduyquang753.nbsapi;

public enum Instrument {
	HARP (0),
	BASS (1),
	DRUM (2),
	SNARE (3),
	CLICK (4),
	GUITAR (5),
	FLUTE (6),
	BELL (7),
	CHIME (8),
	XYLOPHONE (9);
	
	private final int ID;
	private Instrument(int ID) {
		this.ID = ID;
	}
	
	public int getID() {
		return ID;
	}
	
	public static Instrument fromID(int ID) throws IllegalArgumentException {
		switch (ID) {
		case 0: return HARP;
		case 1: return BASS;
		case 2: return DRUM;
		case 3: return SNARE;
		case 4: return CLICK;
		case 5: return GUITAR;
		case 6: return FLUTE;
		case 7: return BELL;
		case 8: return CHIME;
		case 9: return XYLOPHONE;
		default: throw new IllegalArgumentException("ID must be from 1 to 9.");
		}
	}
}
