<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>
	<acme:input-textbox code="any.flight.form.label.tag" path="tag"/>
	<acme:input-textbox code="any.flight.form.label.selfTransfer" path="selfTransfer"/>
	<acme:input-money code="any.flight.form.label.cost" path="cost"/>
	<acme:input-textbox code="any.flight.form.label.description" path="description"/>
	
	<acme:input-moment code="any.flight.form.label.scheduledDeparture" path="scheduledDeparture" readonly="true"/>
	<acme:input-moment code="any.flight.form.label.scheduledArrival" path="scheduledArrival" readonly="true"/>
	<acme:input-textbox code="any.flight.form.label.originCity" path="originCity" readonly="true"/>
	<acme:input-textbox code="any.flight.form.label.destinationCity" path="destinationCity" readonly="true"/>
	<acme:input-textbox code="any.flight.form.label.numberOfLayovers" path="numberOfLayovers" readonly="true"/>
	<acme:button code="any.flight.form.button.legs" action="/any/leg/list?flightId=${id}"/>
</acme:form>