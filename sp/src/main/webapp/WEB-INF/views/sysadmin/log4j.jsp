
<!--
  Copyright (C) 2011, Earl Hood <earl AT earlhood DOT com>

  Permission is hereby granted, free of charge, to any person
  obtaining a copy of this software and associated documentation
  files (the "Software"), to deal in the Software without restriction,
  including without limitation the rights to use, copy, modify, merge,
  publish, distribute, sublicense, and/or sell copies of the Software,
  and to permit persons to whom the Software is furnished to do so,
  subject to the following conditions:

  The above copyright notice and this permission notice shall be
  included in all copies or substantial portions of the Software.

  THE SOFTWARE IS PROVIDED ``AS IS'', WITHOUT WARRANTY OF ANY
  KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
  WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
  AND NONINFRINGEMENT.  IN NO EVENT SHALL EARL HOOD BE LIABLE FOR
  ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF
  CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
  WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
-->
<%@page import="java.io.IOException" %>
<%@page import="java.io.PrintWriter" %>
<%@page import="java.net.URLEncoder" %>
<%@page import="java.util.ArrayList" %>
<%@page import="java.util.Arrays" %>
<%@page import="java.util.Enumeration" %>
<%@page import="org.apache.log4j.LogManager" %>
<%@page import="org.apache.log4j.Logger" %>
<%@page import="org.apache.log4j.Level" %>
<%!
  public String REQ_PARAM_ACTION  	= "action";
  public String REQ_PARAM_CATEGORY	= "cat";
  public String REQ_PARAM_PRIORITY	= "priority";
  public String REQ_ACTION_EDIT	= "edit";
  public String REQ_ACTION_LIST	= "list";
  public String REQ_ACTION_SET	= "set";

  public String simpleEncode(
      String string
  ) {
    if (string == null) return null;
    int len = string.length();
    if (len == 0) return string;

    StringBuilder buf = new StringBuilder((int)(len*1.25f));
    for (int i=0; i < len; ++i) {
      char c = string.charAt(i);
      switch (c) {
	case '<':
	  buf.append("&lt;");
	  break;
	case '>':
	  buf.append("&gt;");
	  break;
	case '&':
	  buf.append("&amp;");
	  break;
	case '"':
	  buf.append("&quot;");
	  break;
	default:
	  buf.append(c);
	  break;
      }
    }
    return buf.toString();
  }

  public boolean isBlank(String s) {
    if (s == null) return true;
    if (s.trim().equals("")) return true;
    return false;
  }

  public void setPriority(
      HttpServletRequest r,
      PrintWriter out
  ) throws IOException {
    String scat = r.getParameter(REQ_PARAM_CATEGORY);
    String spri = r.getParameter(REQ_PARAM_PRIORITY);
    if (scat != null) scat = scat.trim();

    Logger log = null;
    Level pri = null;

    if (!isBlank(spri)) {
      pri = Level.toLevel(spri, null);
    }
    out.println("<!-- pri="+pri+" -->");
    if (!isBlank(scat) && !scat.equals("root")) {
      log = Logger.getLogger(scat);
      log.setLevel(pri);
    } else {
      log = Logger.getRootLogger();
      if (pri != null) log.setLevel(pri);
    }
  }

  public void renderCategoryList(
      HttpServletRequest r,
      PrintWriter out
  ) throws IOException {
    String scriptName = getScriptName(r);

    out.print("<form method=get action=\"/sp/sysAdmin/logs");
    out.print("\"><table cellspacing=0 cellpadding=1>");
    out.println("<tr><td><b>Logger</b></td><td><b>Level</b></td></tr>");
    out.println(
	"<tr><td><hr noshade size=1></td><td><hr noshade size=1></td></tr>");
    printCategory(Logger.getRootLogger(), scriptName, out);
    Enumeration e = LogManager.getCurrentLoggers();
    ArrayList a = new ArrayList<String>();
    while (e.hasMoreElements()) {
      //printCategory((Logger)e.nextElement(), scriptName, out);
      a.add(((Logger)e.nextElement()).getName());
    }
    String[] logNames = new String[a.size()];
    logNames = (String[])a.toArray(logNames);
    Arrays.sort(logNames);
    for (String logName : logNames) {
      printCategory(Logger.getLogger(logName), scriptName, out);
    }
    out.println(
	"<tr><td><hr noshade size=1></td><td><hr noshade size=1></td></tr>");
    out.println("</table>");
    out.println("<input type=submit name=action value=\"Reload\">");
    out.println("<input type=submit name=action value=\" Edit \"></form>");
  }

  /** Render a single category row within a category list.
   */
  public void printCategory(
      Logger log,
      String scriptName,
      PrintWriter out
  ) {
    boolean isInherited = log.getLevel() == null;
    String catName = log.getName();

    out.print("<tr><td><b><tt><a href=\"");
    out.print(scriptName);
    out.print("?action=");
    out.print(REQ_ACTION_EDIT);
    out.print("&amp;cat=");
    try {
      out.print(URLEncoder.encode(catName, "UTF-8"));
    } catch (java.io.UnsupportedEncodingException uee) {
      // should not get here
    }
    out.print("\">");
    out.print(simpleEncode(catName));
    out.print("</a>");
    out.print("</tt></b></td><td>");
    if (isInherited) {
      out.print("<i>");
    } else {
      out.print("<b>");
    }
    out.print(log.getEffectiveLevel());
    if (isInherited) {
      out.print("</i>");
    } else {
      out.print("</b>");
    }
    out.println("</td></tr>");
  }

  /** Render category edit form.
   */
  public void renderEditForm(
      HttpServletRequest r,
      PrintWriter out
  ) throws IOException {
    String scriptName = getScriptName(r);

    out.print("<form method=get action=\"");
    out.print(scriptName);
    out.print("\">");

    Logger log = getCategory(r.getParameter(REQ_PARAM_CATEGORY));
    Level pri = log.getLevel();
    String spri = null;
    if (pri == null) {
      pri = Logger.getLogger(this.getClass().getName()).getEffectiveLevel();
      spri = "";
    } else {
      spri = pri.toString();
    }

    out.print("<table border=0>");

    out.print("<tr valign=top>");
    out.print("<td align=right><b>Category:</b></td>");
    out.print("<td align=left>");
    out.print("<input type=text size=50 name=cat value=\"");
    out.print(simpleEncode(log.getName()));
    out.println("\"></td></tr>");

    out.print("<tr valign=top>");
    out.print("<td align=right><b>Level:</b></td>");
    out.print("<td align=left><select name=priority>");
    printPriorityOption(out, "FATAL", "FATAL", spri);
    printPriorityOption(out, "ERROR", "ERROR", spri);
    printPriorityOption(out, "WARN", "WARN", spri);
    printPriorityOption(out, "INFO", "INFO", spri);
    printPriorityOption(out, "DEBUG", "DEBUG", spri);
    printPriorityOption(out, "(inherited)", "", spri);
    out.println("</select></td></tr>");
    out.println("</table>");

    out.println("<input type=submit name=action value=\" Set  \">");
    out.println("<input type=submit name=action value=\"Cancel\">");
    out.println("</form>");
  }

  public void printPriorityOption(
      PrintWriter out,
      String label,
      String value,
      String curValue
  ) {
    out.print("<option value=\"");
    out.print(value);
    out.print("\"");
    if (curValue != null && curValue.equals(value)) {
      out.print(" selected");
    }
    out.print(">");
    out.print(label);
    out.println("</option>");
  }

  public void renderHead(
      PrintWriter out
  ) {
    out.println("<html><body>");
  }

  public void renderFoot(
      PrintWriter out
  ) {
    out.println("</body></html>");
  }

  /** Retrieve category instance with given name.
   */
  public Logger getCategory(
      String name
  ) {
    if (name != null) name = name.trim();
    if (!isBlank(name) && !name.equals("root")) {
      return Logger.getLogger(name);
    }
    return Logger.getRootLogger();
  }

  public String getScriptName(
      HttpServletRequest r
  ) {
    return "/sp/sysAdmin/logs";
  }
 %>
<%
  java.io.PrintWriter writer = new java.io.PrintWriter(out);
  renderHead(writer);
  String action = request.getParameter(REQ_PARAM_ACTION);
  if (action == null) action = "";
  action = action.trim().toLowerCase();
  writer.println("<!-- REQ_ACTION: "+REQ_ACTION_SET+" -->");
  writer.println("<!-- ACTION: "+action+" -->");
  writer.println("<!-- CATEGORY: "+request.getParameter(REQ_PARAM_CATEGORY)+" -->");
  writer.println("<!-- PRIORITY: "+request.getParameter(REQ_PARAM_PRIORITY)+" -->");
  if (action.equals(REQ_ACTION_SET)) {
    setPriority(request, writer);
    renderCategoryList(request, writer);
  } else if (action.equals(REQ_ACTION_EDIT)) {
    renderEditForm(request, writer);
    return;
  } else {
    renderCategoryList(request, writer);
  }
  renderFoot(writer);
%>
