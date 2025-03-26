<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form readonly="true">
	<acme:input-textbox code="airline-manager.flight.form.label.tag" path="tag"/>
	<acme:input-textbox code="airline-manager.flight.form.label.selfTransfer" path="selfTransfer"/>
	<acme:input-money code="airline-manager.flight.form.label.cost" path="cost"/>
	<acme:input-textarea code="airline-manager.flight.form.label.description" path="description"/>	
	<acme:input-moment code="airline-manager.flight.form.label.scheduledDeparture" path="scheduledDeparture"/>
	<acme:input-moment code="airline-manager.flight.form.label.scheduledArrival" path="scheduledArrival"/>
	<acme:input-textbox code="airline-manager.flight.form.label.originCity" path="originCity"/>
	<acme:input-textbox code="airline-manager.flight.form.label.destinationCity" path="destinationCity"/>
	<acme:input-integer code="airline-manager.flight.form.label.numberOfLayovers" path="numberOfLayovers"/>
</acme:form>
