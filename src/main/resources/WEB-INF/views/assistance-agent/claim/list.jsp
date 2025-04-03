<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:list>
	<acme:list-column code='assistance-agent.claim.list.label.registrationMoment' path="registrationMoment" width="33.33%"/>
	<acme:list-column code='assistance-agent.claim.list.label.type' path="type" width="33.33%"/>
	<acme:list-column code='assistance-agent.claim.list.label.status' path="status" width="33.33%"/>
</acme:list>

<jstl:if test="${_command == 'list'}">
	<acme:button code="assistance-agent.claim.list.button.create" action="/assistance-agent/claim/create"/>
</jstl:if>