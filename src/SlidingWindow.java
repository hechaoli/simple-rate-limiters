import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

public class SlidingWindow extends RateLimiter {

  // TODO: Clean up stale entries
  private final ConcurrentMap<Long, AtomicInteger> windows = new ConcurrentHashMap<>();

  protected SlidingWindow(int maxRequestPerSec) {
    super(maxRequestPerSec);
  }

  @Override
  boolean allow() {
    long curTime = System.currentTimeMillis();
    long curWindowKey = curTime / 1000 * 1000;
    windows.putIfAbsent(curWindowKey, new AtomicInteger(0));
    long preWindowKey = curWindowKey - 1000;
    AtomicInteger preCount = windows.get(preWindowKey);
    if (preCount == null) {
      return windows.get(curWindowKey).incrementAndGet() <= maxRequestPerSec;
    }

    double preWeight = 1 - (curTime - curWindowKey) / 1000.0;
    long count = (long) (preCount.get() * preWeight
        + windows.get(curWindowKey).incrementAndGet());
    return count <= maxRequestPerSec;
  }
}
