<%@ page language="java" contentType="text/html; charset=EUC-KR" pageEncoding="EUC-KR"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=EUC-KR">
<title>JSP DB2 Connection Test !!!</title>
</head>
<body>

<%@ page language="java" %>
<%@ page import="java.sql.*" %>
<%@ page contentType="TEXT/HTML" %>

<%
try {
	Class.forName("COM.ibm.db2.jdbc.app.DB2Driver");
	String url = "jdbc:db2://172.25.24.54:50000/LGDEDWP";
	Connection con = DriverManager.getConnection(url, "davew", "davew");
	Statement stmt = con.createStatement();
	ResultSet rs = stmt.executeQuery("select distinct rtrim(creator) TABLE_SCHEM from sysibm.systables with ur");
	
	while(rs.next())
	{ 
		out.println(rs.getInt(1));
	}
}
catch(ClassNotFoundException ex)
{
	System.out.println(ex);
}

catch(SQLException ex)
{
	System.out.println(ex);
}
catch(Exception e)
{ 
	out.println(e);
}
%>

</body>
</html>