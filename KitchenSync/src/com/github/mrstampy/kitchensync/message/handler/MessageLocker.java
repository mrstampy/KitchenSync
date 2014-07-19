package com.github.mrstampy.kitchensync.message.handler;

import java.io.Serializable;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MessageLocker implements Serializable {

	private static final long serialVersionUID = -4244487112507916182L;

	private static Map<Object, Lock> locks = new WeakHashMap<Object, Lock>();

	public Lock getLock(Object message) {
		Lock lock = locks.get(message);

		if (lock == null) {
			lock = new ReentrantLock(true);
			locks.put(message, lock);
		}

		return lock;
	}

	public void removeLock(Object message) {
		locks.remove(message);
	}
}
