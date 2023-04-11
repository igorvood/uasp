package ru.vtb.uasp.streaming.mdm.enrichment.itest.common

import java.util.concurrent.atomic.AtomicLong

class FooCounter(start: Int) {

  val counter: AtomicLong = new AtomicLong(start)

  def get(): Long = counter.get()

  def set(v: Long): Unit = counter.set(v)

  def inc(): Long = counter.incrementAndGet()

  def dec(): Long = counter.decrementAndGet()

  def modify(f: Long => Long): Unit = {
    var done = false
    var oldVal: Long = 0
    while (!done) {
      oldVal = counter.get()
      done = counter.compareAndSet(oldVal, f(oldVal))
    }
  }
}