package com.sp.web.service.pdf;

import com.sp.web.Constants;
import com.sp.web.exception.SPException;
import com.sp.web.service.stringtemplate.StringTemplate;
import com.sp.web.service.stringtemplate.StringTemplateFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;
import java.util.Map;

/**
 * @author Dax Abraham
 * 
 *         The iText implementation of the pdf creator service.
 */
@Service("itextPdfService")
public class ITextPDFCreatorService implements PDFCreatorService {
  
  @Autowired
  private StringTemplateFactory stringTemplateFactory;
  
  @Override
  public ByteArrayOutputStream createPDF(String templateName, Map<String, Object> params,
      String companyId, String templateType) {
    Assert.hasText(templateName, "Template name is required !!!");
    StringTemplate stringTemplate = stringTemplateFactory.getStringTemplate(templateName,
        Constants.DEFAULT_LOCALE, companyId, templateType, false);
    
    if (params != null && !params.isEmpty()) {
      params.keySet().forEach(k -> {
        stringTemplate.put(k, params.get(k));
      });
    }
    
    // get the pdf text
    String pdfText = stringTemplate.render();
    
    return createPDF(pdfText);
  }
  
  /**
   * Gets an input stream with the file.
   * 
   * @param pdfText
   *          - pdf text
   * @return the input stream
   */
  public ByteArrayOutputStream createPDF(String pdfText) {
    
    ITextRenderer renderer = new ITextRenderer();
    renderer.setDocumentFromString(pdfText);
    renderer.layout();
    renderer.finishPDF();
    try {
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      renderer.createPDF(bos);
      bos.close();
      return bos;
    } catch (Exception e) {
      throw new SPException("Could not create PDF !!!", e);
    }
  }
  
}
