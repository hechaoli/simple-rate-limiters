import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

public class FixedWindowCounter extends RateLimiter {

  // TODO: Clean up stale entries
  private final ConcurrentMap<Long, AtomicInteger> windows = new ConcurrentHashMap<>();

  protected FixedWindowCounter(int maxRequestPerSec) {
    super(maxRequestPerSec);
  }

  @Override
  boolean allow() {
    long windowKey = System.currentTimeMillis() / 1000 * 1000;
    windows.putIfAbsent(windowKey, new AtomicInteger(0));
    return windows.get(windowKey).incrementAndGet() <= maxRequestPerSec;
  }
}
