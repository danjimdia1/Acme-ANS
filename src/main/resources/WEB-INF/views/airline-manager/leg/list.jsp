
<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:list>
	<acme:list-column code="airline-manager.leg.label.scheduledDeparture" path="scheduledDeparture" width="20%" />
	<acme:list-column code="airline-manager.leg.label.scheduledArrival" path="scheduledArrival" width="20%" />
	<acme:list-column code="airline-manager.leg.label.departureAirport" path="departureAirport" width="20%" />
	<acme:list-column code="airline-manager.leg.label.arrivalAirport" path="arrivalAirport" width="20%" />			
</acme:list>