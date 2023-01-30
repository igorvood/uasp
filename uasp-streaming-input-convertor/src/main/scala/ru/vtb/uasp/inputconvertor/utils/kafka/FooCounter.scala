package ru.vtb.uasp.inputconvertor.utils.kafka

import java.util.concurrent.atomic.AtomicLong

class FooCounter {
  val counter = new AtomicLong(0)
  def get():Long = counter.get()
  def set(v: Long): Unit = counter.set(v)
  def inc(): Long = counter.incrementAndGet()
  def modify(f: Long => Long): Unit = {
    var done = false
    var oldVal: Long = 0
    while (!done) {
      oldVal = counter.get()
      done = counter.compareAndSet(oldVal, f(oldVal))
    }
  }
}