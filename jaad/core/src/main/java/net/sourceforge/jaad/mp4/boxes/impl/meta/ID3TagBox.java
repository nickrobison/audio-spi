package net.sourceforge.jaad.mp4.boxes.impl.meta;

import net.sourceforge.jaad.mp4.MP4Input;
import net.sourceforge.jaad.mp4.boxes.FullBox;
import net.sourceforge.jaad.mp4.boxes.Utils;

import java.io.IOException;


// TODO: use nio ByteBuffer instead of array
public class ID3TagBox extends FullBox {

    private String language;
    private byte[] id3Data;

    public ID3TagBox() {
        super("ID3 Tag Box");
    }

    @Override
    public void decode(MP4Input in) throws IOException {
        super.decode(in);

        language = Utils.getLanguageCode(in.readBytes(2));

        id3Data = new byte[(int) getLeft(in)];
        in.readBytes(id3Data);
    }

    public byte[] getID3Data() {
        return id3Data;
    }

    /**
     * The language code for the following text. See ISO 639-2/T for the set of
     * three character codes.
     */
    public String getLanguage() {
        return language;
    }
}
