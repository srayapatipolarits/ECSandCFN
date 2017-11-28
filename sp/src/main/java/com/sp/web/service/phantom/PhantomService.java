package com.sp.web.service.phantom;

import com.sp.web.exception.SPException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import javax.servlet.ServletContext;

/**
 * <code>PhantomService</code>will create the pdf and store it an temp file.
 * 
 * @author pradeepruhil
 *
 */
@Service
public class PhantomService {
  
  private static final Logger LOG = Logger.getLogger(PhantomService.class);
  
  private ReentrantLock lock = new ReentrantLock();
  
  @Autowired
  private ServletContext context;
  
  @Autowired
  private Environment environment;
  
  /**
   * <code>createPdf</code> method will create pdf for the ushb er
   * 
   * @param filePath
   *          name of the file.
   * @param fileName
   *          of the file.
   */
  public void creatPdf(String filePath, String fileName, String scriptPath) {
    
    Process process = null;
    try {
      lock.lock();
      String phatomJSInstalledPath = environment.getProperty("phatomjs.binary.path");
      LOG.info("Phatom js installed path " + phatomJSInstalledPath);
      
      LOG.info("Scriptpath " + scriptPath);
      
      StringBuilder pathBuilder = new StringBuilder(phatomJSInstalledPath);
      pathBuilder.append(" ").append(scriptPath).append(" ").append(filePath).append(" ")
          .append(fileName);
      String paramsProcess = pathBuilder.toString();
      
      int timeout = Integer.valueOf(environment.getProperty("phantomjs.timeout"));
      LOG.info("paramsProcess " + paramsProcess);
      
      /* start the lock to start the process */
      process = Runtime.getRuntime().exec(paramsProcess);
      boolean exitStatus = process.waitFor(timeout, TimeUnit.SECONDS);
      if (exitStatus) {
        StringBuilder stringBuilder = new StringBuilder("SUCCESS:");
        LOG.info("Stauts " + exitStatus + ": " + stringBuilder.toString());
      } else {
        throw new SPException("PRocess timeout out, Unable to create PDF ");
      }
      
    } catch (Exception exception) {
      LOG.error("Exception occured while processing the phatom service ", exception);
      throw new SPException("Unable to create the PDF");
    } finally {
      if (process != null) {
        try {
          process.destroyForcibly();
        } finally {
          lock.unlock();
        }
      } else {
        lock.unlock();
      }
    }
    LOG.info("PDF file created, successfully");
  }
}
