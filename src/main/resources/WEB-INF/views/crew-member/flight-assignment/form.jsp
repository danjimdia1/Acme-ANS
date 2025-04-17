<%--
- form.jsp
-
- Copyright (C) 2012-2025 Rafael Corchuelo.
-
- In keeping with the traditional purpose of furthering education and research, it is
- the policy of the copyright owner to permit non-commercial use and redistribution of
- this software. It has been tested carefully, but it is not guaranteed for any particular
- purposes.  The copyright owner does not offer any warranties or representations, nor do
- they accept any liabilities with respect to them.
--%>
<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form> 
	<acme:input-select code="Crew member" path="crewMember" choices="${crewMembers}"/>
	<acme:input-select code="Leg" path="leg" choices="${legs}"/>		
	<acme:input-select code="Duty" path="duty" choices="${duty}"/>
	<acme:input-select code="Current status" path="currentStatus" choices="${currentStatus}"/>
	<acme:input-textbox code="Remarks" path="remarks"/>
	<acme:input-moment code="Last update" path="lastUpdate" readonly="true"/>
	
	<jstl:choose>	 
		<jstl:when test="${acme:anyOf(_command, 'show|update|publish') && isCompleted == false && draftMode == true }"> 
			<acme:submit code="Publish" action="/crew-member/flight-assignment/publish"/>
			<acme:submit code="Update" action="/crew-member/flight-assignment/update"/>
			<acme:submit code="Delete" action="/crew-member/flight-assignment/delete"/>
		</jstl:when>
		<jstl:when test="${acme:anyOf(_command, 'show|update|publish') && draftMode == true}">
	
			<acme:submit code="Update" action="/crew-member/flight-assignment/update"/>
			<acme:submit code="Delete" action="/crew-member/flight-assignment/delete"/>
		</jstl:when>
		<jstl:when test="${_command == 'show' && isCompleted==true && draftMode == false }">
			<acme:button code="Activity-log" action="/crew-member/activity-log/list?masterId=${id}"/>			
	
		</jstl:when>
		<jstl:when test="${_command == 'create'}">
			<acme:input-checkbox code="Confirmation" path="confirmation"/>
			<acme:submit code="Create" action="/crew-member/flight-assignment/create"/>
		</jstl:when>		
	</jstl:choose>
</acme:form>