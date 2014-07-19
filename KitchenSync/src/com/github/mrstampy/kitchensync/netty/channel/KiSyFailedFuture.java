package com.github.mrstampy.kitchensync.netty.channel;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class KiSyFailedFuture implements ChannelFuture {

	private Throwable cause;
	
	public KiSyFailedFuture() {
		
	}
	
	public KiSyFailedFuture(Throwable cause) {
		this.cause = cause;
	}

	@Override
	public boolean isSuccess() {
		return false;
	}

	@Override
	public boolean isCancellable() {
		return false;
	}

	@Override
	public Throwable cause() {
		return cause;
	}

	@Override
	public boolean await(long timeout, TimeUnit unit) throws InterruptedException {
		return true;
	}

	@Override
	public boolean await(long timeoutMillis) throws InterruptedException {
		return true;
	}

	@Override
	public boolean awaitUninterruptibly(long timeout, TimeUnit unit) {
		return true;
	}

	@Override
	public boolean awaitUninterruptibly(long timeoutMillis) {
		return true;
	}

	@Override
	public Void getNow() {
		return null;
	}

	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		return false;
	}

	@Override
	public Void get() throws InterruptedException, ExecutionException {
		return null;
	}

	@Override
	public Void get(long arg0, TimeUnit arg1) throws InterruptedException, ExecutionException, TimeoutException {
		return null;
	}

	@Override
	public boolean isCancelled() {
		return true;
	}

	@Override
	public boolean isDone() {
		return true;
	}

	@Override
	public Channel channel() {
		return null;
	}

	@Override
	public ChannelFuture addListener(GenericFutureListener<? extends Future<? super Void>> listener) {
		return this;
	}

	@SuppressWarnings("unchecked")
	@Override
	public ChannelFuture addListeners(GenericFutureListener<? extends Future<? super Void>>... listeners) {
		return this;
	}

	@Override
	public ChannelFuture removeListener(GenericFutureListener<? extends Future<? super Void>> listener) {
		return this;
	}

	@SuppressWarnings("unchecked")
	@Override
	public ChannelFuture removeListeners(GenericFutureListener<? extends Future<? super Void>>... listeners) {
		return this;
	}

	@Override
	public ChannelFuture sync() throws InterruptedException {
		return this;
	}

	@Override
	public ChannelFuture syncUninterruptibly() {
		return this;
	}

	@Override
	public ChannelFuture await() throws InterruptedException {
		return this;
	}

	@Override
	public ChannelFuture awaitUninterruptibly() {
		return this;
	}

}
