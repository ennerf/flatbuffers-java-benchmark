// automatically generated, do not modify

package benchfb;

import java.nio.*;
import java.lang.*;
import java.util.*;
import com.google.flatbuffers.*;

public class Bar extends Struct {
  public Bar __init(int _i, ByteBuffer _bb) { bb_pos = _i; bb = _bb; return this; }

  public Foo parent() { return parent(new Foo()); }
  public Foo parent(Foo obj) { return obj.__init(bb_pos + 0, bb); }
  public int time() { return bb.getInt(bb_pos + 16); }
  public float ratio() { return bb.getFloat(bb_pos + 20); }
  public short size() { return bb.getShort(bb_pos + 24); }

  public static int createBar(FlatBufferBuilder builder, long Foo_id, short Foo_count, byte Foo_prefix, int Foo_length, int time, float ratio, short size) {
    builder.prep(8, 32);
    builder.pad(6);
    builder.putShort(size);
    builder.putFloat(ratio);
    builder.putInt(time);
    builder.prep(8, 16);
    builder.putInt(Foo_length);
    builder.pad(1);
    builder.putByte(Foo_prefix);
    builder.putShort(Foo_count);
    builder.putLong(Foo_id);
    return builder.offset();
  }
};

