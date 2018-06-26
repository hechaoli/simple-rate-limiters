public class TokenBucketLazyRefill extends RateLimiter {

  private int tokens;

  private long lastRefillTime;

  public TokenBucketLazyRefill(int maxRequestPerSec) {
    super(maxRequestPerSec);
    this.tokens = maxRequestPerSec;
    this.lastRefillTime = System.currentTimeMillis();
  }

  @Override
  public boolean allow() {
    synchronized (this) {
      refillTokens();
      if (tokens == 0) {
        return false;
      }
      tokens--;
      return true;
    }
  }

  private void refillTokens() {
    long curTime = System.currentTimeMillis();
    double secSinceLastRefill = (curTime - lastRefillTime) / 1000.0;
    int cnt = (int) (secSinceLastRefill * maxRequestPerSec);
    if (cnt > 0) {
      tokens = Math.min(tokens + cnt, maxRequestPerSec);
      lastRefillTime = curTime;
    }
  }
}
