package net.sf.fmj.media.rtp;

import javax.media.Buffer;
import javax.media.Format;
import javax.media.PlugIn;

public interface Depacketizer extends PlugIn
{
    public static final int DEPACKETIZER = 6;

    public abstract Format[] getSupportedInputFormats();

    public abstract Format parse(Buffer buffer);

    public abstract Format setInputFormat(Format format);
}
