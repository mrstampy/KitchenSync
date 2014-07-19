package com.github.mrstampy.kitchensync.message.inbound;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import rx.Scheduler;
import rx.schedulers.Schedulers;

public class SingleThreadExecutorSchedulerProvider {

	private List<Scheduler> bufferedSchedulers = new ArrayList<>();
	
	private AtomicInteger next = new AtomicInteger(0);
	
	private int size;
	
	private Lock lock = new ReentrantLock();
	
	public SingleThreadExecutorSchedulerProvider(int size) {
		this.size = size;
		createSchedulers();
	}
	
	public Scheduler singleThreadScheduler() {
		lock.lock();
		try {
			return getNext();
		} finally {
			lock.unlock();
		}
	}

	private Scheduler getNext() {
		int index = next.getAndIncrement();
		if(index == size - 1) next.set(0);
		
		return bufferedSchedulers.get(index);
	}

	private void createSchedulers() {
		for(int i = 0; i < size; i++) {
			bufferedSchedulers.add(Schedulers.from(Executors.newSingleThreadExecutor()));
		}
	}
}
