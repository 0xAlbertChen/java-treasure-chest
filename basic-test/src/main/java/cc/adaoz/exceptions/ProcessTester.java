package cc.adaoz.exceptions;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * <pre>
 * xen虚拟机,5.5G内存;8核CPU
 * LOOP = 100000000
 * THREADS = 20
 *
 * tc:  101412
 * ie:  100749
 * </pre>
 *
 * @author li.jinl 2010-7-9 上午10:47:56
 */
public class ProcessTester {

    private static final int             LOOP          = 100000000;
    private static final int             THREADS       = 20;

    private static final List<Long>      tryCatchTimes = new ArrayList<Long>(THREADS);
    private static final List<Long> ifElseTimes   = new ArrayList<Long>(THREADS);

    private static final ExecutorService POOL          = Executors.newFixedThreadPool(40);

    public static void main(String[] args) throws Exception {
        List<Callable<Boolean>> all = new ArrayList<Callable<Boolean>>();
        all.addAll(tasks(new TryCatch()));
        all.addAll(tasks(new IfElse()));

        POOL.invokeAll(all);

        long total = total(tryCatchTimes);
        System.out.println("tc:\t\t" + total/1000000);
        long total1 = total(ifElseTimes);
        System.out.println("ie:\t\t" + total1/1000000);

        POOL.shutdown();
    }

    private static List<Callable<Boolean>> tasks(Callable<Boolean> c) {
        List<Callable<Boolean>> list = new ArrayList<Callable<Boolean>>(THREADS);
        for (int i = 0; i < THREADS; i++) {
            list.add(c);
        }
        return list;
    }

    private static long total(List<Long> list) {
        long sum = 0;
        for (Long v : list) {
            sum += v;
        }
        return sum;
    }

    public static class TryCatch implements Callable<Boolean> {

        @Override
        public Boolean call() throws Exception {
            long start = System.nanoTime();
            for (int i = 0; i < LOOP; i++) {
                try {
                    exception();
                    //
                } catch (ExtCustomException e) {
                    //
                }
            }
            tryCatchTimes.add(System.nanoTime() - start);
            return true;
        }

        private void exception() throws ExtCustomException {
            throw new ExtCustomException("");
        }

    }

    public static class IfElse implements Callable<Boolean> {

        @Override
        public Boolean call() throws Exception {
            long start = System.nanoTime();
            for (int i = 0; i < LOOP; i++) {
                Exception e = exception();
                if (e instanceof ExtCustomException) {
                    //
                }
            }
            ifElseTimes.add(System.nanoTime() - start);
            return true;
        }

        private Exception exception() {
            return new ExtCustomException("");
        }

    }

    public static class ExtCustomException extends Exception {

        private static final long serialVersionUID = -6879298763723247455L;

        private String            message;

        public ExtCustomException(String message){
            this.message = message;
        }

        @Override
        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        @Override
        public Throwable fillInStackTrace() {
            return this;
        }

    }

}