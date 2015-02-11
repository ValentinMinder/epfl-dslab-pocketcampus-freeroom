package org.pocketcampus.platform.server.tests;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.joda.time.Duration;
import org.junit.Ignore;
import org.junit.Test;
import org.pocketcampus.platform.server.CachingProxy;

import static org.junit.Assert.assertEquals;

/**
 * Tests for CachingProxy.
 * 
 * @author Solal Pirelli <solal.pirelli@epfl.ch>
 */
public class CachingProxyTests {
	@Test
	public void voidReturn() {
		DoNothingImpl useless = new DoNothingImpl();
		DoNothing proxy = CachingProxy.create(useless, Duration.standardDays(1), false);

		proxy.doNothing(1);
	}

	@Test
	public void noParameters() {
		DoNothingImpl useless = new DoNothingImpl();
		DoNothing proxy = CachingProxy.create(useless, Duration.standardDays(1), false);

		assertEquals(42, proxy.return42());
	}

	@Test
	public void returnValueIsCorrect() {
		CalculatorImpl calc = new CalculatorImpl();
		Calculator proxy = CachingProxy.create(calc, Duration.standardDays(1), false);

		assertEquals(3, proxy.add(1, 2));
	}

	@Test
	public void cachedReturnValueIsCorrect() {
		CalculatorImpl calc = new CalculatorImpl();
		Calculator proxy = CachingProxy.create(calc, Duration.standardDays(1), false);

		proxy.add(1, 2);

		assertEquals(3, proxy.add(1, 2));
	}

	@Test
	public void cacheWorks() {
		CalculatorImpl calc = new CalculatorImpl();
		Calculator proxy = CachingProxy.create(calc, Duration.standardDays(1), false);

		proxy.add(1, 2);
		proxy.add(1, 2);

		assertEquals(1, calc.getHitCount());
	}

	@Test
	public void cacheIsInvalidatedAfterDuration() {
		CalculatorImpl calc = new CalculatorImpl();
		Calculator proxy = CachingProxy.create(calc, Duration.standardDays(1), false);

		DateTimeUtils.setCurrentMillisFixed(new DateTime(2013, 11, 18, 00, 00, 00).getMillis());
		proxy.add(1, 2);
		DateTimeUtils.setCurrentMillisFixed(new DateTime(2013, 11, 19, 00, 00, 00).getMillis());
		proxy.add(1, 2);

		assertEquals(2, calc.getHitCount());
	}
	
	@Test
	public void cacheIsNotInvalidatedOnDifferentDayWhenNotRequested(){
		CalculatorImpl calc = new CalculatorImpl();
		Calculator proxy = CachingProxy.create(calc, Duration.standardHours(1), false);

		DateTimeUtils.setCurrentMillisFixed(new DateTime(2013, 11, 18, 23, 30, 00).getMillis());
		proxy.add(1, 2);
		DateTimeUtils.setCurrentMillisFixed(new DateTime(2013, 11, 19, 00, 10, 00).getMillis());
		proxy.add(1, 2);

		assertEquals(1, calc.getHitCount());
	}
	
	@Test
	public void cacheIsInvalidatedOnDifferentDayWhenRequested(){
		CalculatorImpl calc = new CalculatorImpl();
		Calculator proxy = CachingProxy.create(calc, Duration.standardHours(1), true);

		DateTimeUtils.setCurrentMillisFixed(new DateTime(2013, 11, 18, 23, 30, 00).getMillis());
		proxy.add(1, 2);
		DateTimeUtils.setCurrentMillisFixed(new DateTime(2013, 11, 19, 00, 10, 00).getMillis());
		proxy.add(1, 2);

		assertEquals(2, calc.getHitCount());
	}

	// This is a "manual" test
	// Un-ignore it, run it, and look at your task manager
	// Memory usage should peak and then stay flat, not continually increase until it hits an OOM
	@Test
	@Ignore
	public void cacheDoesNotRunOutOfMemory() {
		CalculatorImpl calc = new CalculatorImpl();
		Calculator proxy = CachingProxy.create(calc, Duration.standardDays(1), false);

		for (int a = 0; a < Integer.MAX_VALUE; a++) {
			proxy.add(a, 0);
		}

	}

	public interface DoNothing {
		int return42();

		void doNothing(int useless);
	}

	public class DoNothingImpl implements DoNothing {
		@Override
		public int return42() {
			return 42;
		}

		@Override
		public void doNothing(int useless) {
		}
	}

	public interface Calculator {
		int add(int a, int b);
	}

	private static final class CalculatorImpl implements Calculator {
		private int _hitCount;

		public int getHitCount() {
			return _hitCount;
		}

		@Override
		public int add(int a, int b) {
			_hitCount++;
			return a + b;
		}
	}
}