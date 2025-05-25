<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:list>
	<acme:list-column code="customer.passenger.list.fullName" path="fullName" width="30%"/>
	<acme:list-column code="customer.passenger.list.passportNumber" path="passportNumber" width="20%"/>
	<acme:list-column code="customer.passenger.list.dateOfBirth" path="dateOfBirth" width="20%"/>
	<acme:list-column code="customer.passenger.list.specialNeeds" path="specialNeeds" width="15%"/>
	<acme:list-column code="customer.passenger.list.draftMode" path="draftMode" width="15%"/>
	
	
	<acme:list-payload path="payload"/>
</acme:list>

<jstl:if test="${empty bookingId}">
	<acme:button code="customer.passenger.list.button.create" action="/customer/passenger/create"/>
</jstl:if>

<jstl:if test="${draftMode == true}">
	<acme:button code="customer.booking-record.list.button.create" action="/customer/booking-record/create?bookingId=${bookingId}"/>
	<acme:button code="customer.passenger.list.button.create" action="/customer/passenger/create?bookingId=${bookingId}"/>
</jstl:if>
