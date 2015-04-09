<%@ page import="java.sql.*,javax.naming.Context,javax.naming.InitialContext,javax.naming.NamingException,javax.sql.DataSource,java.util.Locale,oracle.jdbc.pool.OracleDataSource" %>
<HTML>
  <HEAD>
    <TITLE>JDBC Information for Oracle Support</TITLE>
  </HEAD>
  <BODY>
  
  <% if (request.getParameter("fromForm") == null) { %>
  <FORM method="post" action="JDBCInfo.jsp">
    <input type="hidden" name="fromForm" value="true" />
    <h3>JDBC Definition - parameters</h3>
    <table>
      <tr><td>Hostname        </td><td><INPUT TYPE="text"     name="paramHost"        size="50"/></td></tr>
      <tr><td>Port            </td><td><INPUT TYPE="text"     name="paramPort"        size="50"/></td></tr>
      <tr><td>SID             </td><td><INPUT TYPE="text"     name="paramSid"         size="50"/></td></tr>
      <tr><td>Username        </td><td><INPUT TYPE="text"     name="paramUser"        size="50"/></td></tr>
      <tr><td>Password        </td><td><INPUT TYPE="password" name="paramPassword"    size="50"/></td></tr>
      <tr><td>Type (thin/oci) </td><td><INPUT TYPE="text"     name="paramType"        size="50"/></td></tr>
    </table>
    <BR/>
    <h3>JDBC Definition - free format</h3>
    <table>
      <tr e="color: rgb(207, 191, 173);">><td valign="top">Free format URL </td><td><textarea name="paramUrl"         rows="5" cols="50" ></textarea></td></tr>
      <tr><td>Username        </td><td><INPUT TYPE="text"     name="paramUrlUser"     size="50"                    /></td></tr>
      <tr><td>Password        </td><td><INPUT TYPE="password" name="paramUrlPassword" size="50"                    /></td></tr>
    </table>
    <BR/>
    <h3>JDBC Definition - datasource</h3>
    <table>
      <tr><td>Datasource      </td><td><INPUT TYPE="text" name="paramDatasource"      size="50"/></td></tr>
    </table>
    <BR/>
    <BR/>
    <INPUT TYPE="submit" />
  </FORM>
  <% } else { %>
  <a href="<%=request.getServletPath() %>">back</a>
  <%
  
    out.println("<h3>DATABASE:</h3>");

    Connection conn = null;

    try {

      boolean dbInfo       = true;
    
      if (!("".equals(request.getParameter("paramHost")))) {
        out.println("Using: JDBC Definition – parameters.");
        out.println("<BR/>");
        OracleDataSource ods = new OracleDataSource();
        ods.setServerName(request.getParameter("paramHost"));
        ods.setPortNumber(Integer.parseInt(request.getParameter("paramPort")));
        ods.setDatabaseName(request.getParameter("paramSid"));
        ods.setUser(request.getParameter("paramUser"));
        ods.setPassword(request.getParameter("paramPassword"));
        ods.setDriverType(request.getParameter("paramType"));
        conn = ods.getConnection();

      }
      
      else if (!("".equals(request.getParameter("paramUrl")))) {
        out.println("Using: JDBC Definition – free format.");
        out.println("<BR/>");
        String dbUrl         = request.getParameter("paramUrl");
        String dbUrlUser     = request.getParameter("paramUrlUser");
        String dbUrlPassword = request.getParameter("paramUrlPassword");

        DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
        if ( ("".equals(dbUrlUser)) || ("".equals(dbUrlPassword)) ) {
          conn = DriverManager.getConnection(dbUrl);
        }
        else {
          conn = DriverManager.getConnection(dbUrl,dbUrlUser,dbUrlPassword);
        }

      }

      else if (!("".equals(request.getParameter("paramDatasource")))) {
        out.println("Using: JDBC Definition – Data Source <i>" + request.getParameter("paramDatasource") + "</i>");
        out.println("<BR/>");
        Context ic = new InitialContext();
        DataSource ds = (DataSource)ic.lookup(request.getParameter("paramDatasource"));
        conn = ds.getConnection();
      }
      
      else {
        out.println("No database information specified.");
        dbInfo = false;
      }
  
      // Only do when database information is supplied.
      if ( dbInfo ) {
        DatabaseMetaData meta = conn.getMetaData ();

        // gets driver info:
        out.println("Database Product Name    : " + meta.getDatabaseProductName());
        out.println("<BR/>");
        out.println("Database Product Version : " + meta.getDatabaseProductVersion());
        out.println("<BR/>");
        out.println("JDBC Driver Name         : " + meta.getDriverName());
        out.println("<BR/>");
        out.println("JDBC Driver Version      : " + meta.getDriverVersion());
        out.println("<BR/>");
        out.println("JDBC URL                 : " + meta.getURL());
        out.println("<BR/>");
        conn.close();
      }
    }
    catch (Throwable t) {
      out.println(" Error occured during retrieving database connection: ");
      out.println("<BR/>");
      out.println(t);
      out.println("<BR/>");
    }

    java.util.Properties props = System.getProperties();
    out.println("<h3>JVM:</h3>");
    out.println(props.getProperty("java.vm.vendor"));
    out.println("<BR/>");
    out.println(props.getProperty("java.vm.name"));
    out.println("<BR/>");
    out.println(props.getProperty("java.vm.version"));
    out.println("<BR/>");
    out.println(props.getProperty("java.version"));
    out.println("<BR/>");
    out.println("<h3>LOCALE:</h3>");
    out.println(Locale.getDefault());
    out.println("<BR/>");

    String pathseparator = props.getProperty("path.separator");

    String classpath = props.getProperty("java.class.path");
    out.println("<h3>CLASSPATH:</h3>");
    String[] strarr = classpath.split(pathseparator);
    for(int i = 0; i < strarr.length; i++) {
      out.println(strarr[i]);
      out.println("<BR/>");
    }

    String libpath = props.getProperty("java.library.path");
    out.println("<h3>LIBRARYPATH:</h3>");
    strarr = libpath.split(pathseparator);
    for(int i = 0; i < strarr.length; i++) {
      out.println(strarr[i]);
      out.println("<BR/>");
    }
    %>
<% } %>
  </BODY>

</HTML>

