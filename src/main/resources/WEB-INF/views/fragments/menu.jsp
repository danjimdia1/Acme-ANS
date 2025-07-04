<%--
- menu.jsp
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
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:menu-bar>
	<acme:menu-left>
		<acme:menu-option code="master.menu.anonymous" access="isAnonymous()">
			<acme:menu-suboption code="master.menu.anonymous.favourite-link" action="http://www.example.com/"/>
			<acme:menu-suboption code="master.menu.anonymous.favourite-link.danjimdia1" action="https://github.com/danjimdia1"/>
			<acme:menu-suboption code="master.menu.anonymous.favourite-link.mansangar13" action="https://github.com/mansangar13"/>
			<acme:menu-suboption code="master.menu.anonymous.favourite-link.manpervel2" action="https://github.com/manpervel2"/>
			<acme:menu-suboption code="master.menu.anonymous.favourite-link.manloppav" action="https://github.com/manloppav"/>
			<acme:menu-suboption code="master.menu.anonymous.favourite-link.marbarmar16" action="https://github.com/meryknows"/>
		</acme:menu-option>
		
		<acme:menu-option code="master.menu.any">
      		<acme:menu-suboption code="master.menu.any.flights" action="/any/flight/list"/>
		</acme:menu-option>

		<acme:menu-option code="master.menu.administrator" access="hasRealm('Administrator')">
			<acme:menu-suboption code="master.menu.administrator.list-user-accounts" action="/administrator/user-account/list"/>
			<acme:menu-suboption code="master.menu.administrator.airport" action="/administrator/airport/list"/>
			<acme:menu-suboption code="master.menu.administrator.aircraft" action="/administrator/aircraft/list"/>
			<acme:menu-separator/>
			<acme:menu-suboption code="master.menu.administrator.airline" action="/administrator/airline/list"/>
			<acme:menu-separator/>
			<acme:menu-suboption code="master.menu.administrator.populate-db-initial" action="/administrator/system/populate-initial"/>
			<acme:menu-suboption code="master.menu.administrator.populate-db-sample" action="/administrator/system/populate-sample"/>			
			<acme:menu-separator/>
			<acme:menu-suboption code="master.menu.administrator.shut-system-down" action="/administrator/system/shut-down"/>
		</acme:menu-option>
		
		<acme:menu-option code="master.menu.customer" access="hasRealm('Customer')">
			<acme:menu-suboption code="master.menu.customer.list-bookings" action="/customer/booking/list"/>
			<acme:menu-suboption code="master.menu.customer.passenger" action="/customer/passenger/list"/>	
		</acme:menu-option>

		<acme:menu-option code="master.menu.provider" access="hasRealm('Provider')">
			<acme:menu-suboption code="master.menu.provider.favourite-link" action="http://www.example.com/"/>
		</acme:menu-option>

		<acme:menu-option code="master.menu.consumer" access="hasRealm('Consumer')">
			<acme:menu-suboption code="master.menu.consumer.favourite-link" action="http://www.example.com/"/>
		</acme:menu-option>
		
		<acme:menu-option code="master.menu.airline-manager" access="hasRealm('AirlineManager')">
			<acme:menu-suboption code="master.menu.airline-manager.list-flights" action="/airline-manager/flight/list"/>
			<acme:menu-suboption code="master.menu.airline-manager.dashboard" action="/airline-manager/airline-manager-dashboard/show"/>
		</acme:menu-option>
		
		<acme:menu-option code="master.menu.crew-member" access="hasRealm('CrewMember')">
			<acme:menu-suboption code="master.menu.crew-member.flight-assignment.completedlist" action="/crew-member/flight-assignment/completed-list"/>
			<acme:menu-suboption code="master.menu.crew-member.flight-assignment.plannedlist" action="/crew-member/flight-assignment/planned-list"/>
    </acme:menu-option>

		<acme:menu-option code="master.menu.assistance-agent" access="hasRealm('AssistanceAgent')">
			<acme:menu-suboption code="master.menu.assistance-agent.list-claims" action="/assistance-agent/claim/list"/>
		</acme:menu-option>
		
		<acme:menu-option code="master.menu.technician" access="hasRealm('Technician')">
			<acme:menu-suboption code="master.menu.technician.maintenanceRecords.maintenanceRecords-list" action="/technician/maintenance-record/list"/>
			<acme:menu-separator/>
			<acme:menu-suboption code="master.menu.technician.tasks.tasks-list" action="/technician/task/list"/>
			<acme:menu-suboption code="master.menu.technician.show-dashboard" action="/technician/dashboard/show" />	
		</acme:menu-option>
	</acme:menu-left>

	<acme:menu-right>		
		<acme:menu-option code="master.menu.user-account" access="isAuthenticated()">
			<acme:menu-suboption code="master.menu.user-account.general-profile" action="/authenticated/user-account/update"/>
			<acme:menu-suboption code="master.menu.user-account.become-provider" action="/authenticated/provider/create" access="!hasRealm('Provider')"/>
			<acme:menu-suboption code="master.menu.user-account.provider-profile" action="/authenticated/provider/update" access="hasRealm('Provider')"/>
			<acme:menu-suboption code="master.menu.user-account.become-consumer" action="/authenticated/consumer/create" access="!hasRealm('Consumer')"/>
			<acme:menu-suboption code="master.menu.user-account.consumer-profile" action="/authenticated/consumer/update" access="hasRealm('Consumer')"/>
			<acme:menu-suboption code="master.menu.user-account.become-technician" action="/authenticated/technician/create" access="!hasRealm('Technician')"/>
			<acme:menu-suboption code="master.menu.user-account.technician-profile" action="/authenticated/technician/update" access="hasRealm('Technician')"/>
		</acme:menu-option>
	</acme:menu-right>
</acme:menu-bar>

