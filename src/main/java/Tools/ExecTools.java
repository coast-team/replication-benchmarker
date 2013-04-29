/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Tools;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Stephane Martin <stephane@stephanemartin.fr>
 */
public class ExecTools {

    private static final Logger LOG = Logger.getLogger(ExecTools.class.getName());

    /**
     * Launch a command and wait the terminaison. If the return number of
     * command is not 0 then display all stream in warning LOG.
     *
     * @param command the command to be launch
     * @param currentDirectory Current working directory for the command
     * @throws IOException
     * @throws InterruptedException
     * @return The return value of process
     */
    public static int launchAndWait(String command, String currentDirectory, int timeout) throws IOException, TimeoutException {
        
        LOG.log(Level.INFO, "command line : {0}", command);
        Process p = Runtime.getRuntime().exec(command, new String[0], new File(currentDirectory));
        
        WaitForProcess watch = new WaitForProcess(p);
        ThreadReader output=new ThreadReader(p.getInputStream());
        ThreadReader error=new ThreadReader(p.getErrorStream());
        try {
            watch.waitFor(timeout);

        } catch (InterruptedException ex) {
        }


        if (LOG.isLoggable(Level.WARNING) && p.exitValue() != 0) {
            LOG.log(Level.WARNING, "command existed with error code : {0}", p.exitValue());
            LOG.log(Level.WARNING, "error : {0}", error);
            LOG.log(Level.WARNING, "output : {0}", output);
        } else if (LOG.isLoggable(Level.INFO)) {
            LOG.log(Level.WARNING, "error : {0}", error);
            LOG.log(Level.INFO, "output : {0}", output);
            LOG.log(Level.INFO, "{0} finished ", command);
        }
        return p.exitValue();
    }
    
    /**
     * This class read an InputStream in separated thread
     * The thread termine when the inputstream is closed
     * It can be read with to string function.
     */
    static class ThreadReader implements Runnable {
        StringBuilder str = new StringBuilder();
        InputStream s;
        Thread th;
        
        
        public ThreadReader(InputStream s) {
            this.s = s;
            lauch();
        }
        
        private void lauch(){
            th=new Thread(this);
            th.start();
        }
        /**
         * reads the stream and appens the string.
         */
        @Override
        public void run() {
            try {
                int c;
                while ((c = s.read()) > -1) {
                    str.append((char) c);
                }
            } catch (IOException ex) {
                Logger.getLogger(ExecTools.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        /**
         * 
         * @return Stream in String format
         */
        @Override
        public String toString(){
            return this.str.toString();
        } 
    }

    /**
     * This class wait the end of process or the end of a time out.
     * It create a thread with waitfor to wait end of process 
     * and join this thread with timout.
     * And check what is the first
     */
    
    static class WaitForProcess implements Runnable {

        private Process process;
        private int returnValue = -1;
        private boolean finished = false;
        private Thread thread;

        /**
         * 
         * @param process to wait
         */
        public WaitForProcess(Process process) {
            this.process = process;

        }
        /**
         * Wait the process with mili of time out.
         * 
         * @param milliS is waiting time in millisecond 0 is infinite
         * @return the return of processus if no timeoutException was throwed
         * @throws InterruptedException If any thread was interupted
         * @throws TimeoutException If the processus time has depassed the time
         */
        int waitFor(long milliS) throws InterruptedException, TimeoutException {
            LOG.log(Level.INFO, "Waitfor {0}ms", milliS);
            thread = new Thread(this);
            thread.start();
            thread.join(milliS);
            synchronized (this) {
                if (!this.finished) {
                    LOG.info("processus timeout");
                    process.destroy();
                    throw new TimeoutException();
                }
                return this.returnValue;
            }
        }

        /**
         * Wait the processus end inform that is ended.
         */
        @Override
        public void run() {
            try {
                returnValue = process.waitFor();
                finished = true;
                LOG.log(Level.INFO, "Processus is in time and return {0}", returnValue);
            } catch (InterruptedException ex) {
                LOG.info("Watchdog interupted");
            }
        }

        /**
         * 
         * @return the return value of processus.
         */
        public int getReturnValue() {
            return returnValue;
        }
        /**
         * 
         * @return if processus is not timed out
         */
        public boolean isFinished() {
            return finished;
        }
    }
}
