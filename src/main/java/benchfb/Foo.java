// automatically generated, do not modify

package benchfb;

import java.nio.*;
import java.lang.*;
import java.util.*;
import com.google.flatbuffers.*;

public class Foo extends Struct {
  public Foo __init(int _i, ByteBuffer _bb) { bb_pos = _i; bb = _bb; return this; }

  public long id() { return bb.getLong(bb_pos + 0); }
  public short count() { return bb.getShort(bb_pos + 8); }
  public byte prefix() { return bb.get(bb_pos + 10); }
  public int length() { return bb.getInt(bb_pos + 12); }

  public static int createFoo(FlatBufferBuilder builder, long id, short count, byte prefix, int length) {
    builder.prep(8, 16);
    builder.putInt(length);
    builder.pad(1);
    builder.putByte(prefix);
    builder.putShort(count);
    builder.putLong(id);
    return builder.offset();
  }
};

