/**
 * Autogenerated by Thrift Compiler (0.9.2)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package org.pocketcampus.plugin.events.shared;

import org.apache.thrift.scheme.IScheme;
import org.apache.thrift.scheme.SchemeFactory;
import org.apache.thrift.scheme.StandardScheme;

import org.apache.thrift.scheme.TupleScheme;
import org.apache.thrift.protocol.TTupleProtocol;
import org.apache.thrift.protocol.TProtocolException;
import org.apache.thrift.EncodingUtils;
import org.apache.thrift.TException;
import org.apache.thrift.async.AsyncMethodCallback;
import org.apache.thrift.server.AbstractNonblockingServer.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.EnumMap;
import java.util.Set;
import java.util.HashSet;
import java.util.EnumSet;
import java.util.Collections;
import java.util.BitSet;
import java.nio.ByteBuffer;
import java.util.Arrays;
import javax.annotation.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings({"cast", "rawtypes", "serial", "unchecked"})
public class eventsConstants {

  public static final long CONTAINER_EVENT_ID = -1L;

  public static final Map<Integer,String> EVENTS_CATEGS = new HashMap<Integer,String>();
  static {
    EVENTS_CATEGS.put(-1, "Featured events");
    EVENTS_CATEGS.put(0, "All");
    EVENTS_CATEGS.put(1, "Conferences - Seminars");
    EVENTS_CATEGS.put(-2, "Favorites");
    EVENTS_CATEGS.put(9, "Cultural events");
    EVENTS_CATEGS.put(8, "Inaugural lessons - Lessons of honor");
    EVENTS_CATEGS.put(13, "Academic calendar");
    EVENTS_CATEGS.put(10, "Sporting events");
    EVENTS_CATEGS.put(7, "Celebrations");
    EVENTS_CATEGS.put(5, "Exhibitions");
    EVENTS_CATEGS.put(6, "Movies");
    EVENTS_CATEGS.put(11, "Dating EPFL - economy");
    EVENTS_CATEGS.put(4, "Miscellaneous");
    EVENTS_CATEGS.put(2, "Meetings management tips");
    EVENTS_CATEGS.put(12, "Thesis defenses");
  }

  public static final Map<String,String> EVENTS_TAGS = new HashMap<String,String>();
  static {
    EVENTS_TAGS.put("epfl", "École Polytechnique Fédérale de Lausanne");
    EVENTS_TAGS.put("sb", "Basic Sciences");
    EVENTS_TAGS.put("sti", "Engineering");
    EVENTS_TAGS.put("ic", "Computer & Communication Sciences");
    EVENTS_TAGS.put("sv", "Life Sciences");
    EVENTS_TAGS.put("enac", "Architecture, Civil and Environmental Engineering");
    EVENTS_TAGS.put("cdm", "Management of Technology");
    EVENTS_TAGS.put("associations", "Associations");
    EVENTS_TAGS.put("cdh", "College of Humanities");
  }

  public static final Map<Integer,String> EVENTS_PERIODS = new HashMap<Integer,String>();
  static {
    EVENTS_PERIODS.put(2, "Two days");
    EVENTS_PERIODS.put(14, "Two weeks");
    EVENTS_PERIODS.put(30, "One month");
    EVENTS_PERIODS.put(365, "One year");
    EVENTS_PERIODS.put(7, "One week");
    EVENTS_PERIODS.put(180, "Six months");
    EVENTS_PERIODS.put(1, "One day");
  }

}
