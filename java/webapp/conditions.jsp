<%@ taglib uri="/WEB-INF/weather.tld" prefix="wx" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"  %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"  %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic"  %>

<html:html/>

<head></head>
<body>
    <table border="1">
        <html:form action="/changePeriod" >
            <tr>
                <td>
                <h3>
                    <center>Very Current Conditions</center>
                </h3>
                </td>
            </tr>
            <tr>
                <td>
                <table border="1">
                    <tr> </tr>
                    <tr>
                        <td>
                        <jsp:include page="_currentConditions.jsp" />
                        </td>
                    </tr>
                </table>
                </td>
            </tr>
            <tr>
                <td>
                <h3>
                    <center>
                      <logic:present name="currentPeriod" >
                        <bean:write name="currentPeriod"/>
                      </logic:present>
                      <logic:notPresent name="currentPeriod" >
                      Yesterday
                      </logic:notPresent>
                    </center>
                </h3>
                </td>
            </tr>
            <tr>
                <td><jsp:include page="_periodConditions.jsp" />
                </td>
            </tr>
            <tr>
                <td>
                <table>
                    <tr>
                        <td>New Period</td>
                        <td>
                        <html:select property="period">
                          <html:optionsCollection name="changePeriodActionForm" property="periods" label="period" value="period" />
                        </html:select>
                        </td>
                        <td>
                        <html:submit>Update</html:submit>
                        </td>
                    </tr>
                </table>
                </td>
            </tr>
        </html:form>
    </table>
</body>
