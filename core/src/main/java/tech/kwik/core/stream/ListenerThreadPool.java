package tech.kwik.core.stream;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

import static java.util.concurrent.TimeUnit.SECONDS;

final class ListenerThreadPool implements AutoCloseable {

    private final List<QuicStreamImpl> pending;
    private final ExecutorService executor;

    ListenerThreadPool() {
        this.pending = new ArrayList<>();
        this.executor = new ThreadPoolExecutor(1, 1, 1,
                SECONDS, new LinkedBlockingQueue<>(), runnable -> {
			Thread thread = new Thread(runnable, "kwik-listener");
			thread.setDaemon(true);

			return thread;
		});
    }

    void execute(QuicStreamImpl stream, Runnable runnable) {
        if (pending.contains(stream)) return;
        executor.execute(() -> {
            try {
                runnable.run();
            } finally {
                pending.remove(stream);
            }
        });
    }
    
    @Override
    public void close() {
        //todo
    }
}
