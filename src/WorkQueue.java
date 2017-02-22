
import java.util.LinkedList;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
/**
 * A simple work queue implementation based on the IBM developerWorks article
 * by Brian Goetz. It is up to the user of this class to keep track of whether
 * there is any pending work remaining.
 *
 * @see <a href="http://www.ibm.com/developerworks/library/j-jtp0730/index.html">Java Theory and Practice: Thread Pools and Work Queues</a>
 */
public class WorkQueue {

	/** The default number of threads to use when not specified. */
	public static final int DEFAULT = 5;

	/**
	 * Declare logger for logger messages
	 */
	//private static final Logger logger = LogManager.getLogger();

	/** Pool of WORKER THREADS that will WAIT in the background UNTIL WORK IS AVAILABLE. */
	private final PoolWorker[] workers;

	/** Queue of PENDING WORK REQUESTS. */
	private final LinkedList<Runnable> queue;

	/** Used to signal the queue should be shutdown. */
	private volatile boolean shutdown;

	/**
	 * The number of worker threads waiting for work
	 */
	private int pending;


	/**
	 * Starts a work queue with the default number of threads.
	 * @see #WorkQueue(int)
	 */
	public WorkQueue() {
		this(DEFAULT);
	}

	/**
	 * Starts a work queue with the specified number of threads.
	 *
	 * @param threads number of worker threads; should be greater than 1
	 */
	public WorkQueue(int threads) {
		if (threads < 1) {
			threads = DEFAULT;
		}

		this.queue   = new LinkedList<Runnable>();
		this.workers = new PoolWorker[threads];
		pending = 0;
		shutdown = false;

		// start the threads so they are waiting in the background
		for (int i = 0; i < threads; i++) {
			workers[i] = new PoolWorker();
			workers[i].start();
		}
		//logger.info(workers.length);
	}


	/**
	 * Adds a work request to the queue. A thread will process this request
	 * when available.
	 *
	 * @param R - WORK REQUEST (in the form of a RUNNABLE OBJECT)
	 */
	public void execute(Runnable r) {
		synchronized (queue) {
			incrementPending();
			queue.addLast(r);
			queue.notifyAll();
		}
	}


	/**
	 * Indicates that we now have additional "pending" work to wait for. We
	 * need this since we can no longer call join() on the threads. (The
	 * threads keep running forever in the background.)
	 *
	 * We made this a synchronized method in the outer class, since locking
	 * on the "this" object within an inner class does not work.
	 */
	private void incrementPending() {
		synchronized (queue) {
			pending++;
		}
	}

	/**
	 * Indicates that we now have one less "pending" work, and will notify
	 * any waiting threads if we no longer have any more pending work left.
	 */
	private void decrementPending() {
		synchronized (queue) {
			pending--;
			if (pending <= 0) {
				// notify all if no work is available. 
				queue.notifyAll();
			}
		}
	}

	/**
	 * Helper method, that helps a thread wait until all of the current
	 * work is done. This is useful for resetting the counters or shutting
	 * down the work queue.
	 */
	public void finish() {
		synchronized (queue) {
			while (pending > 0) {
				try {
					// while still work available will wait
					queue.wait();
				}
				catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}
		}
	}

	/**
	 * Asks the queue to shutdown. Any unprocessed work will not be finished,
	 * but threads in-progress will not be interrupted.
	 */
	public void shutdown() {
		shutdown = true;

		synchronized (queue) {
			// calls all threads to wake up and kill them
			queue.notifyAll();
		}
	}

	/**
	 * Returns the number of worker threads being used by the work queue.
	 *
	 * @return number of worker threads
	 */
	public int size() {
		return workers.length;
	}

	/**
	 * Waits until work is available in the work queue. When work is found, will
	 * remove the work from the queue and run it. If a shutdown is detected,
	 * will exit instead of grabbing new work from the queue. These threads will
	 * continue running in the background until a shutdown is requested.
	 */
	private class PoolWorker extends Thread {

		@Override
		public void run() {
			Runnable r = null;

			while (true) {
				synchronized (queue) {
					while (queue.isEmpty() && !shutdown) {
						try {
							queue.wait();
						}
						catch (InterruptedException ex) {
							System.err.println("Warning: Work queue interrupted " +
									"while waiting.");
							Thread.currentThread().interrupt();
						}
					} // end while

					// exit while for one of two reasons:
					// (a) queue has work, or (b) shutdown has been called

					if (shutdown) {
						break;
					}
					else {
						r = queue.removeFirst();
					}
				} // end synchronized queue

				try {
					
					r.run();
				}
				catch (RuntimeException ex) {
					// catch runtime exceptions to avoid leaking threads
					System.err.println("Warning: Work queue encountered an " +
							"exception while running.");
				}
				finally {
					decrementPending();
				}
			}
		}
	}
}
