<%@ taglib  uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>

<html:html>
<head>
<title>
    <bean:message key="logon.title"/>
</title>
</head>

<body bgcolor="white">

<html:errors/>

<html:form action="/logon" focus="username">
<table border="0" width="100%">
    <tr>
        <th align="right">
            <bean:message key="prompt.username"/>
        </th>
        <td align="left">
            <html:text  property="username" size="16"/>
        </td>
    </tr>
    <tr>
        <th align="right">
            <bean:message key="prompt.password"/>
        </th>
        <td align="left">
            <html:password property="password" size="16"/>
        </td>
    </tr>
    <tr>
        <td align="right">
            <html:submit>
                <bean:message key="button.submit"/>
            </html:submit>
        </td>
        <td align="right">
            <html:reset>
                <bean:message key="button.reset"/>
            </html:reset>
        </td>
    </tr>
</table>

</html:form>
</body>
</html:html>
