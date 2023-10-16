package net.sourceforge.jaad.mp4.boxes.impl.meta;

import net.sourceforge.jaad.mp4.MP4InputStream;

import java.io.IOException;

public class ThreeGPPKeywordsBox extends ThreeGPPMetadataBox {

	private String[] keywords;

	public ThreeGPPKeywordsBox() {
		super("3GPP Keywords Box");
	}

	@Override
	public void decode(MP4InputStream in) throws IOException {
		decodeCommon(in);

		final int count = in.read();
		keywords = new String[count];

		int len;
		for(int i = 0; i<count; i++) {
			len = in.read();
			keywords[i] = in.readUTFString(len);
		}
	}

	public String[] getKeywords() {
		return keywords;
	}
}
