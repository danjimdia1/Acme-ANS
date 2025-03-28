<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>
	<acme:input-textbox code="airline-manager.flight.form.label.tag" path="tag"/>
	<acme:input-textbox code="airline-manager.flight.form.label.selfTransfer" path="selfTransfer"/>
	<acme:input-money code="airline-manager.flight.form.label.cost" path="cost"/>
	<acme:input-textarea code="airline-manager.flight.form.label.description" path="description"/>	

	<acme:input-moment readonly="true" code="airline-manager.flight.form.label.scheduledDeparture" path="scheduledDeparture"/>
	<acme:input-moment readonly="true" code="airline-manager.flight.form.label.scheduledArrival" path="scheduledArrival"/>
	<acme:input-textbox readonly="true" code="airline-manager.flight.form.label.originCity" path="originCity"/>
	<acme:input-textbox readonly="true" code="airline-manager.flight.form.label.destinationCity" path="destinationCity"/>
	<acme:input-textbox readonly="true" code="airline-manager.flight.form.label.numberOfLayovers" path="numberOfLayovers" />
	<acme:input-textbox readonly="true" code="airline-manager.flight.form.label.draftMode" path="draftMode"/>
	<jstl:choose>
		<jstl:when test="${acme:anyOf(_command, 'show|update|delete|publish') && draftMode == true}">
			<acme:submit code="airline-manager.flight.form.button.update" action="/airline-manager/flight/update"/>
			<acme:submit code="airline-manager.flight.form.button.delete" action="/airline-manager/flight/delete"/>
			<acme:submit code="airline-manager.flight.form.button.publish" action="/airline-manager/flight/publish"/>
		</jstl:when>
		<jstl:when test="${_command == 'create'}">
			<acme:submit code="airline-manager.flight.form.button.create" action="/airline-manager/flight/create"/>
		</jstl:when>		
	</jstl:choose>
</acme:form>
