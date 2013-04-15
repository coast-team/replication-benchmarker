/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jbenchmarker.trace.git;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
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
     */
    public static void launchAndWait(String command, String currentDirectory, int timeout) throws IOException, TimeoutException {
        
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

    }

    /**
     * Print all element in stream in p
     *
     * @param s Stream
     * @param p Printer
     * @throws IOException
     */
    public static void printStream(InputStream s, PrintStream p) throws IOException {
        int c;
        while ((c = s.read()) > -1) {
            p.print((char) c);
        }
    }

    /**
     * Convert all element in stream in String
     *
     * @param s Stream
     * @return the result String
     * @throws IOException
     */
    public static String stream2Str(InputStream s) throws IOException {
        StringBuilder str = new StringBuilder();
        int c;
        while ((c = s.read()) > -1) {
            str.append((char) c);
        }
        return str.toString();

    }

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
        @Override
        public String toString(){
            return this.str.toString();
        } 
    }

    static class WaitForProcess implements Runnable {

        private Process process;
        private int returnValue = -1;
        private boolean finished = false;
        private Thread thread;

        public WaitForProcess(Process process) {
            this.process = process;

        }

        int waitFor(long mili) throws InterruptedException, TimeoutException {
            LOG.log(Level.INFO, "Waitfor {0}ms", mili);
            thread = new Thread(this);
            thread.start();
            thread.join(mili);
            synchronized (this) {
                if (!this.finished) {
                    LOG.info("processus timeout");
                    process.destroy();
                    throw new TimeoutException();
                }
                return this.returnValue;
            }
        }

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

        public int getReturnValue() {
            return returnValue;
        }

        public boolean isFinished() {
            return finished;
        }
    }
}
