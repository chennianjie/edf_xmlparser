package edf.xmlparser.entity;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;


public class ProcessBatchQueues {
	public static AtomicInteger parseEntityNum = new AtomicInteger(0);
	public static AtomicInteger parsePropertyNum = new AtomicInteger(0);
	public static AtomicInteger insertPropertyNum = new AtomicInteger(0);
	public static AtomicInteger insertEntityNum = new AtomicInteger(0);
	public static BlockingQueue<Entity> EntityQueue = new LinkedBlockingQueue<>();
}
