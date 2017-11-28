package com.sp.web.service.pdf;

import java.io.ByteArrayOutputStream;
import java.util.Map;

/**
 * @author Dax Abraham
 *
 *         The interface to use to create PDF's.
 */
public interface PDFCreatorService {

  /**
   * The service to create the pdf file.
   *  
   * @param templateName
   *          - the template name
   * @param params
   *          - the parameters
   * @return
   *      the input stream for the content
   */
  ByteArrayOutputStream createPDF(String templateName, Map<String, Object> params,
      String companyId, String notificationType);
  
  /**
   * The service method to retrieve the pdf with the given xhtml pdf text.
   * 
   * @param pdfTextUrl
   *          - xhtml pdf text
   * @return
   *      the byte array output stream of the pdf
   */
  ByteArrayOutputStream createPDF(String pdfTextUrl);  
}
