<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>
	<acme:input-moment readonly="true" code="assistance-agent.claim.form.label.registrationMoment" path="registrationMoment"/>
	<acme:input-textbox code="assistance-agent.claim.form.label.email" path="email"/>
	<acme:input-textarea code="assistance-agent.claim.form.label.description" path="description"/>
	<acme:input-select code="assistance-agent.claim.form.label.type" path="type" choices="${types}"/>
    <acme:input-select code="assistance-agent.claim.form.label.leg" path="leg" choices="${legs}"/>
	
	<jstl:if test="${acme:anyOf(_command, 'show|update|delete|publish')}">
		<acme:input-textbox readonly="true" code="assistance-agent.claim.form.label.departureAirport" path="departureAirport"/>
		<acme:input-textbox readonly="true" code="assistance-agent.claim.form.label.arrivalAirport" path="arrivalAirport"/>
		<acme:input-textbox readonly="true" code="assistance-agent.claim.form.label.status" path="status"/>
	</jstl:if>
	
	<jstl:choose>
		<jstl:when test="${acme:anyOf(_command, 'show|update|delete|publish') && draftMode == true}">
			<acme:submit code="assistance-agent.claim.form.button.update" action="/assistance-agent/claim/update"/>
			<acme:submit code="assistance-agent.claim.form.button.delete" action="/assistance-agent/claim/delete"/>
			<acme:submit code="assistance-agent.claim.form.button.publish" action="/assistance-agent/claim/publish"/>
		</jstl:when>
    	<jstl:when test="${_command == 'create'}">
        	<acme:submit code="assistance-agent.claim.form.button.create" action="/assistance-agent/claim/create"/>
    	</jstl:when>
	</jstl:choose>
</acme:form>
