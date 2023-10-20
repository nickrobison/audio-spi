package net.sourceforge.jaad.mp4.boxes.impl.meta;

import net.sourceforge.jaad.mp4.MP4Input;
import net.sourceforge.jaad.mp4.boxes.FullBox;
import net.sourceforge.jaad.mp4.boxes.Utils;

import java.io.IOException;


public class ThreeGPPMetadataBox extends FullBox {

    private String languageCode, data;

    public ThreeGPPMetadataBox(String name) {
        super(name);
    }

    @Override
    public void decode(MP4Input in) throws IOException {
        decodeCommon(in);

        data = in.readUTFString((int) getLeft(in));
    }

    // called directly by subboxes that don't contain the 'data' string
    protected void decodeCommon(MP4Input in) throws IOException {
        super.decode(in);
        languageCode = Utils.getLanguageCode(in.readBytes(2));
    }

    /**
     * The language code for the following text. See ISO 639-2/T for the set of
     * three character codes.
     */
    public String getLanguageCode() {
        return languageCode;
    }

    public String getData() {
        return data;
    }
}
