package slimeknights.mantle.data.loadable.primitive;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import lombok.RequiredArgsConstructor;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import slimeknights.mantle.data.loadable.Loadable;

/**
 * Loadable for an long.
 * @see slimeknights.mantle.data.loadable.common.ColorLoadable
 */
@RequiredArgsConstructor
public class LongLoadable implements Loadable<Long> {
  /** Loadable ranging from long min to long max */
  public static final LongLoadable ANY_FULL = range(Long.MIN_VALUE, Long.MAX_VALUE);
  /** Loadable ranging from short min to short max */
  public static final LongLoadable ANY_SHORT = range(Short.MIN_VALUE, Short.MAX_VALUE);
  /** Loadable ranging from -1 to long max */
  public static final LongLoadable FROM_MINUS_ONE = range(-1, Short.MAX_VALUE);
  /** Loadable ranging from zero to long max */
  public static final LongLoadable FROM_ZERO = min(0);
  /** Loadable ranging from one to long max */
  public static final LongLoadable FROM_ONE = min(1);

  /** Minimum allowed value */
  private final long min;
  /** Maximum allowed value */
  private final long max;
  /** Method of writing to the network */
  private final LongNetwork network;

  /** Creates a loadable with defaulting networking */
  public static LongLoadable range(long min, long max) {
    return new LongLoadable(min, max, LongNetwork.recommended(min, max));
  }

  /** Creates a loadable ranging from the parameter to short max */
  public static LongLoadable min(long min) {
    return range(min, Long.MAX_VALUE);
  }

  /** ensures the long is within valid ranges */
  protected long validate(long value, String key) {
    if (min <= value && value <= max) {
      return value;
    }
    if (min == Long.MIN_VALUE) {
      throw new JsonSyntaxException(key + " must not be greater than " + max);
    }
    if (max == Long.MAX_VALUE) {
      throw new JsonSyntaxException(key + " must not be less than " + min);
    }
    throw new JsonSyntaxException(key + " must be between " + min + " and " + max);
  }

  @Override
  public Long convert(JsonElement element, String key) {
    return validate(GsonHelper.convertToLong(element, key), key);
  }

  @Override
  public JsonElement serialize(Long value) {
    return new JsonPrimitive(validate(value, "Value"));
  }


  /* Networking */

  @Override
  public Long decode(FriendlyByteBuf buffer) {
    return network.fromNetwork(buffer);
  }

  @Override
  public void encode(FriendlyByteBuf buffer, Long object) {
    network.toNetwork(object, buffer);
  }

  /** Methods of writing an long to the network */
  public enum LongNetwork {
    LONG {
      @Override
      long fromNetwork(FriendlyByteBuf buffer) {
        return buffer.readLong();
      }

      @Override
      void toNetwork(long value, FriendlyByteBuf buffer) {
        buffer.writeLong(value);
      }
    },
    INT {
      @Override
      long fromNetwork(FriendlyByteBuf buffer) {
        return buffer.readInt();
      }

      @Override
      void toNetwork(long value, FriendlyByteBuf buffer) {
        buffer.writeInt((int) value);
      }
    },
    VAR_INT {
      @Override
      long fromNetwork(FriendlyByteBuf buffer) {
        return buffer.readVarInt();
      }

      @Override
      void toNetwork(long value, FriendlyByteBuf buffer) {
        buffer.writeVarInt((int) value);
      }
    },
    SHORT {
      @Override
      long fromNetwork(FriendlyByteBuf buffer) {
        return buffer.readShort();
      }

      @Override
      void toNetwork(long value, FriendlyByteBuf buffer) {
        buffer.writeShort((int) value);
      }
    };

    /** Reads the long from the network */
    abstract long fromNetwork(FriendlyByteBuf buffer);

    /** Writes the long to the network */
    abstract void toNetwork(long value, FriendlyByteBuf buffer);

    /** Recommended long network type based on the ranged */
    public static LongNetwork recommended(long min, long max) {
      if (min >= 0) {
        return LongNetwork.VAR_INT;
      }
      if (min >= Short.MIN_VALUE && max <= Short.MAX_VALUE) {
        return LongNetwork.SHORT;
      }
      if (min >= Integer.MIN_VALUE && max <= Integer.MAX_VALUE) {
        return LongNetwork.INT;
      }
      return LongNetwork.LONG;
    }
  }


  /* Strings */

  /**
   * Creates an long loadable that writes to JSON as a string, can be used as a map key.
   * @param radix  Base for conversion, base 10 is standard JSON numbers.
   */
  public StringLoadable<Long> asString(int radix) {
    return new StringLongLoadable(min, max, radix, network);
  }


  /** Writes to a string instead of to an integer */
  private static class StringLongLoadable extends LongLoadable implements StringLoadable<Long> {
    private final int radix;
    public StringLongLoadable(long min, long max, int radix, LongNetwork network) {
      super(min, max, network);
      if (radix < Character.MIN_RADIX || radix > Character.MAX_RADIX) {
        throw new IllegalArgumentException("Invalid radix " + radix + ", must be between " + Character.MIN_RADIX + " and " + Character.MAX_RADIX);
      }
      this.radix = radix;
    }

    @Override
    public Long parseString(String value, String key) {
      try {
        return validate(Long.parseLong(value, radix), key);
      } catch (NumberFormatException e) {
        throw new JsonSyntaxException("Failed to parse long at " + key, e);
      }
    }

    @Override
    public Long convert(JsonElement element, String key) {
      return parseString(GsonHelper.convertToString(element, key), key);
    }

    @Override
    public String getString(Long value) {
      return Long.toString(value, radix);
    }

    @Override
    public JsonElement serialize(Long value) {
      return new JsonPrimitive(getString(value));
    }
  }
}
