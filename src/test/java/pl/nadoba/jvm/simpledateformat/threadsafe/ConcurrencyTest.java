package pl.nadoba.jvm.simpledateformat.threadsafe;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ConcurrencyTest extends TestCase {
    public ConcurrencyTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(ConcurrencyTest.class);
    }

    // 25th of November 2015
    private Date dateCorrect = new Date(1448406000000L);
    private String dateString = "2015-11-25";
    private String dateFormat = "yyyy-MM-dd";

    public void testSimpleDateFormatFailing() {
        try {
            System.out.println("\nRunning non thread-safe unit test...");
            ExecutorService executorService = Executors.newCachedThreadPool();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);


            Set<Callable<Date>> dateCallables = new HashSet<Callable<Date>>();

            for (int i = 0; i < 1000; i++) {
                dateCallables.add(new Callable<Date>() {
                    public Date call() throws Exception {
                        return simpleDateFormat.parse(dateString);
                    }
                });
            }

            List<Future<Date>> dateFutures = executorService.invokeAll(dateCallables);

            for (Future<Date> future : dateFutures) {
                assertEquals(dateCorrect, future.get());
            }

            System.out.println("Non thread-safe test finishes successfully!");
        } catch (Exception e) {
            System.out.println("System has thrown an exception: " + e.getCause());
            fail("Non thread-safe test failed due to " + e.getMessage());
        }

    }

    // the same case but with thread-safe format
    public void testSimpleDateFormat() throws InterruptedException, ExecutionException {

        System.out.println("\nRunning thread-safe unit test");

        ExecutorService executorService = Executors.newCachedThreadPool();
        ThreadLocal<SimpleDateFormat> safeSimpleDateFormat = new ThreadLocal<SimpleDateFormat>() {
            @Override
            protected SimpleDateFormat initialValue() {
                return new SimpleDateFormat(dateFormat);
            }
        };

        Set<Callable<Date>> dateCallables = new HashSet<Callable<Date>>();

        for (int i = 0; i < 1000; i++) {
            dateCallables.add(new Callable<Date>() {
                public Date call() throws Exception {
                    return safeSimpleDateFormat.get().parse(dateString);
                }
            });
        }

        List<Future<Date>> dateFutures = executorService.invokeAll(dateCallables);

        for (Future<Date> future : dateFutures) {
            assertEquals(dateCorrect, future.get());
        }

        System.out.println("Thread-safe test finishes successfully!");
    }
}
